package edu.upenn.cis350.karma;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.simple.JSONObject;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import edu.upenn.cis350.karma.VendorSide.Order;
import edu.upenn.cis350.karma.WebConnection.DataProcessor;
import edu.upenn.cis350.karma.WebConnection.FetchDataListener;

public class CheckoutActivity extends AppCompatActivity {

    ArrayList<FoodItem> orders;
    Intent i;
    CheckoutAdapter adapter;
    Button confirmButton;
    Button cancelButton;
    Context mContext;
    Long total;
    String vendorEmail;
    String vendorName;
    String user;
    DataProcessor dp;
    TextView totalDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        i = this.getIntent();
        orders = (ArrayList) i.getExtras().get("orders");
        dp = new DataProcessor();
        total = (long) 0;


        Log.d("CheckoutActivity", orders.toString());
        Log.d("CheckoutActivity", orders.get(0).getQuantity().toString());
        totalDisplay = (TextView) findViewById(R.id.total);
        adapter = new CheckoutAdapter(this, orders, totalDisplay);
        vendorEmail = i.getStringExtra("vendorEmail");
        vendorName = i.getStringExtra("vendorName");
        Log.d("CheckoutActivity", vendorEmail);
        user = i.getStringExtra("user");
        RecyclerView rv = findViewById(R.id.recycler_view_checkout);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        cancelButton = (Button) findViewById(R.id.cancel_button);
        confirmButton = (Button) findViewById(R.id.confirm_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orders.clear();
                Intent returnHomeIntent = new Intent(v.getContext(), MainActivity.class);
                startActivity(returnHomeIntent);
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override @SuppressLint("NewApi")
            public void onClick(View v) {
                for (FoodItem item : orders) {
                    total = total + (item.getPrice()*item.getQuantity());
                }
                Order finalOrder = new Order(vendorEmail, user, LocalDateTime.now().toString(), orders, total, "1234");
                final Context ctx = v.getContext();
                dp.postNewOrder(finalOrder, ctx, new FetchDataListener() {
                    @Override
                    public void onFetchComplete(JSONObject data) {
                        Toast.makeText(ctx, "Give this to code to " + vendorName + "\n" + "Code: 1234", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFetchFailure(String msg) {
                        Toast.makeText(ctx, "Order Failed, try again soon", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFetchStart() {Log.d("CheckoutActivity", "Attempting to post order");}
                });
                Intent returnHomeIntent = new Intent(v.getContext(), MainActivity.class);
                returnHomeIntent.putExtra("user", user);
                startActivity(returnHomeIntent);
            }
        });
    }
}
