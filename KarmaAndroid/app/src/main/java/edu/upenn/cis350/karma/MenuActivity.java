package edu.upenn.cis350.karma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import org.json.simple.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

import edu.upenn.cis350.karma.CustomerSide.CustomerLoginActivity;
import edu.upenn.cis350.karma.WebConnection.DataProcessor;
import edu.upenn.cis350.karma.WebConnection.FetchDataListener;

public class MenuActivity extends AppCompatActivity {

    DataProcessor dp;
    ArrayList<FoodItem> menu;
    Intent i;
    RecyclerView rv;
    MenuRecyclerAdapter adapter;
    Button checkoutButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ArrayList<FoodItem> testMenu = new ArrayList<>();
        //testMenu.add(new FoodItem("Burger", 300));
        //testMenu.add(new FoodItem("Pickle Rick", 100000));
        Log.d("MenuActivity", "started menu");

        menu = new ArrayList<>();
        dp = new DataProcessor();
        i = this.getIntent();
        final String vendorName = i.getStringExtra("name");
        final String user = i.getStringExtra("user");
        final String vendorEmail = i.getStringExtra("vendorEmail");

        setContentView(R.layout.activity_menu);
        adapter = new MenuRecyclerAdapter(menu,this);
        RecyclerView rv = findViewById(R.id.recycler_view_menu);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        checkoutButton = (Button) findViewById(R.id.checkout_button);


        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.getOrder().isEmpty()) {
                    Toast.makeText(v.getContext(), "Add some items to your order before moving to checkout!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent checkout = new Intent(v.getContext(), CheckoutActivity.class);
                    checkout.putExtra("orders", adapter.getOrder());
                    checkout.putExtra("vendorEmail", vendorEmail);
                    checkout.putExtra("user", user);
                    checkout.putExtra("vendorName", vendorName);
                    startActivity(checkout);
                }
            }
        });


        Log.d("MenuActivity", "Trying to get data for" + vendorName + "email" + vendorEmail);

        dp.getVendorMenu(vendorEmail, this.getApplicationContext(), new FetchDataListener() {
            @Override
            public void onFetchComplete(JSONObject data) {
                //Log.d("MenuActivity", data.toString());
                JSONObject menuData = (JSONObject) data.get("menu");
                Log.d("MenuActivity", data.toJSONString());
                JSONArray items = (JSONArray) menuData.get("items");

                Iterator iter = items.iterator();
                while (iter.hasNext()) {
                    JSONObject curr = (JSONObject) iter.next();
                    if (curr.get("price") != null && curr.get("item") != null && !((String)curr.get("item")).equals(""));
                    FoodItem food = new FoodItem((String) curr.get("item"), (Long) curr.get("price"));
                    menu.add(food);
                    adapter.notifyItemInserted(0);
                    Log.d("MenuActivity", menu.toString());
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                Log.d("MenuActivity", "fetch failure: " + msg);
            }

            @Override
            public void onFetchStart() {
                Log.d("MenuActivity","FetchStart");
            }
        });

        Log.d("MenuActivity", "Made it to end");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Intent i = new Intent(this, CustomerLoginActivity.class);
                startActivity(i);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
