package edu.upenn.cis350.karma;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.upenn.cis350.karma.VendorSide.Order;
import edu.upenn.cis350.karma.WebConnection.DataProcessor;
import edu.upenn.cis350.karma.WebConnection.FetchDataListener;

public class FeedFragment extends Fragment {


    View v;
    RecyclerView rv;
    FeedRecyclerAdapter recyclerAdapter;
    DataProcessor dp;
    List<Order> orders;
    String user;
    String vendor;
    Intent i;
    String friend;
    Button fButton;




    public FeedFragment () {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("Feed Frag", "got here");
        v = inflater.inflate(R.layout.fragment_feed, container, false);
        rv = (RecyclerView) v.findViewById(R.id.feed_rv);
        fButton = (Button) v.findViewById(R.id.friend_button);

        i = getActivity().getIntent();
        dp = new DataProcessor();
        orders = new ArrayList<Order>();
        user = i.getStringExtra("user");



        dp.getUserFeed(user, getContext(), new FetchDataListener() {
            @Override
            public void onFetchComplete(JSONObject data) {
                Log.d("User Feed" ,"Retrieved data :)");
                JSONArray arr = (JSONArray) data.get("orders");
                System.out.println(arr.toJSONString());


                Iterator iter = arr.iterator();
                while (iter.hasNext()) {
                    ArrayList<FoodItem> food = new ArrayList<>();
                    JSONObject item = (JSONObject) iter.next();
                    FoodItem f = new FoodItem((String) item.get("item"), (Long) item.get("price"));
                    food.add(f);
                    vendor = (String) item.get("vendorName");
                    friend = (String) item.get("name");
                    Order o = new Order(vendor, friend, food);
                    orders.add(o);
                }

                recyclerAdapter = new FeedRecyclerAdapter(getActivity(), orders);
                rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                rv.setAdapter(recyclerAdapter);
            }



            @Override
            public void onFetchFailure(String msg) {
                Log.d("User Feed", "fetch failure: " + msg);
            }

            @Override
            public void onFetchStart() { }
        });

        fButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchInt = new Intent(v.getContext(), SearchActivity.class);
                searchInt.putExtra("user", user);
                startActivity(searchInt);
            }
        });

        return v;
    }

}
