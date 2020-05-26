package edu.upenn.cis350.karma;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.upenn.cis350.karma.VendorSide.Order;
import edu.upenn.cis350.karma.WebConnection.DataProcessor;
import edu.upenn.cis350.karma.WebConnection.FetchDataListener;

public class SearchActivity extends AppCompatActivity {

    Intent i;
    private DataProcessor dp;
    private Button searchButton;
    private EditText query;
    private TextView msg;
    ArrayList<Order> orders;
    private String user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Search Page", "Started activity");

        i = getIntent();
        dp = new DataProcessor();
        orders = new ArrayList<Order>();
        setContentView(R.layout.activity_search);
        query = (EditText) findViewById(R.id.email_entry);
        searchButton = (Button) findViewById(R.id.sButton);
        msg = (TextView) findViewById(R.id.emailMsg);
        user = i.getStringExtra("user");

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = query.getText().toString();
                final Context mContext = v.getContext();
                dp.getUserProfile(email, v.getContext(), new FetchDataListener() {
                    @Override
                    public void onFetchComplete(JSONObject data) {
                        JSONArray arr = (JSONArray) data.get("orders");
                        Log.d("Search Page", arr.toJSONString());
                        Iterator iter = arr.iterator();
                        String friend = "";
                        while (iter.hasNext()) {
                            ArrayList<FoodItem> food = new ArrayList<>();
                            JSONObject item = (JSONObject) iter.next();
                            FoodItem f = new FoodItem((String) item.get("item"), (Long) item.get("price"));
                            food.add(f);
                            String vendor = (String) item.get("vendorName");
                            friend = (String) item.get("name");
                            Order o = new Order(vendor, friend, food);
                            orders.add(o);
                        }

                        if (!arr.isEmpty()) {
                            Intent profFound = new Intent(mContext, ProfileActivity.class);
                            profFound.putExtra("tUser", email);
                            profFound.putExtra("owner", friend);
                            profFound.putExtra("user", user);
                            profFound.putExtra("orders", orders);
                            startActivity(profFound);
                        } else {
                            Toast.makeText(mContext, "Friend not found... Try entering their email again or try again later", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFetchFailure(String msg) {
                        Log.d("Search Page", "Data Retrieval Failed");
                        Toast.makeText(mContext, "Friend not found... Try entering their email again or try again later", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFetchStart() {
                    }
                });
            }
        });

    }

}
