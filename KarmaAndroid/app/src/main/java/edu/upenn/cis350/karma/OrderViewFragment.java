package edu.upenn.cis350.karma;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.upenn.cis350.karma.WebConnection.DataProcessor;
import edu.upenn.cis350.karma.WebConnection.FetchDataListener;

import static com.android.volley.VolleyLog.TAG;

public class OrderViewFragment extends Fragment {

    Intent i;
    ListView lv;
    ArrayAdapter<String> adapter;
    List<String> orders;
    DataProcessor dp;

    public OrderViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_order_view, container, false);
        lv = (ListView) view.findViewById(R.id.order_list);

        i = getActivity().getIntent();
        dp = new DataProcessor();
        orders = new ArrayList<>();

        String email = i.getStringExtra("user");
        System.out.println(email);

        dp.getUserOrders(email, this.getContext(), new FetchDataListener() {
            @Override
            public void onFetchComplete(JSONObject data) {
               // Log.d(TAG, data.toJSONString());
                //System.out.println(data.toJSONString());
                JSONArray ordersArray = (JSONArray) data.get("orders");

                if (ordersArray.isEmpty()) {
                    List<String> noOrders = new ArrayList<>();
                    noOrders.add("You don't have any orders, why don't you make one!");
                    adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, noOrders);
                    lv.setAdapter(adapter);
                } else {

                    Iterator ordersIterator = ordersArray.iterator();

                    while (ordersIterator.hasNext()) {
                        JSONObject currOrderObj = (JSONObject) ordersIterator.next();

                        String vendor = (String) currOrderObj.get("vendor");
                        String code = (String) currOrderObj.get("code");
                        //System.out.println("VENDOR : " + vendor);
                        List<String> items = new ArrayList<>();
                        JSONArray itemArray = (JSONArray) currOrderObj.get("items");
                        Iterator itemIterator = itemArray.iterator();
                        while (itemIterator.hasNext()) {
                            JSONObject curr = (JSONObject) itemIterator.next();
                            String itemName = (String) curr.get("item");
                            String quantity = curr.get("quantity").toString();
                            items.add(itemName + " (" + quantity + ")");
                        }
                        String displayedText = "You ordered the following items from " + vendor + ":";
                        for (String s : items) {
                            String curr = "\n  " + s;
                            displayedText += curr;
                        }
                        displayedText += "\nYour order code is " + code;
                        //System.out.println("TEXT : " + displayedText);
                        orders.add(displayedText);
                    }
                    adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, orders);
                    lv.setAdapter(adapter);
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                Log.d(TAG, "fetch failure: " + msg);
            }

            @Override
            public void onFetchStart() {
                //Log.d(TAG, "getting orders from the db");
            }
        });
        return view;
    }
}

