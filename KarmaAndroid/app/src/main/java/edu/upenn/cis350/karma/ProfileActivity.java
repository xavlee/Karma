package edu.upenn.cis350.karma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;

import edu.upenn.cis350.karma.VendorSide.Order;
import edu.upenn.cis350.karma.WebConnection.DataProcessor;
import edu.upenn.cis350.karma.WebConnection.FetchDataListener;

public class ProfileActivity extends AppCompatActivity {

    Intent i;
    private DataProcessor dp;
    private Button followButton;
    private TextView profOwner;
    private TextView msg;
    private RecyclerView rv;
    private FeedRecyclerAdapter adapter;
    private ArrayList<Order> orders;
    private String owner;
    private String ownerEmail;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Search Page", "Started activity");

        i = getIntent();
        dp = new DataProcessor();
        final Context mContext = this;

        owner = i.getStringExtra("owner");
        ownerEmail = i.getStringExtra("tUser");
        user = i.getStringExtra("user");
        orders = (ArrayList<Order>) i.getSerializableExtra("orders");

        setContentView(R.layout.activity_profile);
        adapter = new FeedRecyclerAdapter(this, orders);
        RecyclerView rv = findViewById(R.id.order_history);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        profOwner = (TextView) findViewById(R.id.profile_name);
        followButton = (Button) findViewById(R.id.follow_button);
        msg = (TextView) findViewById(R.id.history_ident);

        profOwner.setText("Welcome to " + owner + "'s Profile Page");
        msg.setText(owner + "'s Recent Orders:");

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dp.addFriend(user, ownerEmail, v.getContext(), new FetchDataListener() {
                    @Override
                    public void onFetchComplete(JSONObject data) {
                        Toast.makeText(mContext, "You are now following " + owner + "!", Toast.LENGTH_SHORT).show();
                        Log.d("Profile Activity", data.toJSONString());
                    }

                    @Override
                    public void onFetchFailure(String msg) {
                        Toast.makeText(mContext, "There was an error... try again later", Toast.LENGTH_SHORT).show();
                        Log.d("Profile Activity", msg);
                    }

                    @Override
                    public void onFetchStart() {}
                });
            }
        });

    }
}
