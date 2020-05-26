package edu.upenn.cis350.karma.VendorSide;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import edu.upenn.cis350.karma.FoodItem;
import edu.upenn.cis350.karma.R;
import edu.upenn.cis350.karma.WebConnection.DataProcessor;
import edu.upenn.cis350.karma.WebConnection.FetchDataListener;

public class VendorHomeActivity extends AppCompatActivity {

    private final String TAG = "VendorHomeActivity";

    private RecyclerView rvOrders;
    private List<Order> orders;
    private String email;
    DataProcessor dp;
    OrderAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_home);

        dp = new DataProcessor();
        orders = new ArrayList<>();

        email = getIntent().getStringExtra("email");
        adapter = new OrderAdapter(orders);

        rvOrders = (RecyclerView) findViewById(R.id.rvOrders);

        rvOrders.setAdapter(adapter);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));

        dp.getVendorOrders(email, this.getApplicationContext(), new FetchDataListener() {
            @Override
            public void onFetchComplete(JSONObject data) {
                Log.d(TAG, data.toJSONString());
                List<FoodItem> items = new ArrayList<>();
                items.add(new FoodItem("Burger", (long) 5));
                Order o = new Order("Bob's Burgers", "Velma", new Date().toString(),
                        items, (long) 5, "ABC");

                JSONArray ordersArray = (JSONArray) data.get("orders");

                Iterator ordersIterator = ordersArray.iterator();
                int numOrders = 0;

                while (ordersIterator.hasNext()) {
                    JSONObject currOrderObj = (JSONObject) ordersIterator.next();
                    Order curr = new Order(currOrderObj);

                    orders.add(curr);
                    numOrders++;
                }

                adapter.notifyItemRangeInserted(0, numOrders);
            }

            @Override
            public void onFetchFailure(String msg) {
                Log.d(TAG, "fetch failure: " + msg);

                List<Order> orders = new ArrayList<>();
                List<FoodItem> items = new ArrayList<>();
                items.add(new FoodItem("Burger", new Long(5), new Long(1)));
                orders.add(new Order("Bob's Burgers", "Velma", new Date().toString(),
                        items, new Long(5), "ABC"));

                orders.addAll(orders);
                adapter.notifyItemRangeInserted(0, orders.size());
            }

            @Override
            public void onFetchStart() {
                Log.d(TAG, "getting orders from the db");
            }
        });
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
                Intent i = new Intent(this, VendorLoginActivity.class);
                startActivity(i);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
