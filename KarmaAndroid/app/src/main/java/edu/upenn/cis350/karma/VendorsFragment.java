package edu.upenn.cis350.karma;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.upenn.cis350.karma.WebConnection.DataProcessor;
import edu.upenn.cis350.karma.WebConnection.FetchDataListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class VendorsFragment extends Fragment {


    ProgressDialog pd;
    View v;
    VendorRecyclerAdapter recyclerAdapter;
    private RecyclerView rv;
    private List<Vendor> vendors = new ArrayList<>();
    DataProcessor dp;
    private String user;

    public VendorsFragment(String user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_vendors, container, false);

        rv = (RecyclerView) v.findViewById(R.id.vendor_rv);
        recyclerAdapter = new VendorRecyclerAdapter(getContext(), vendors, user);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(recyclerAdapter);
        return v;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dp = new DataProcessor();
        vendors = new ArrayList<>();

        dp.getVendorInfo(this.getActivity(), new FetchDataListener() {
            @Override
            public void onFetchComplete(JSONObject data) {
                Log.d("Vendors Fragment", data.toJSONString());
                JSONArray arr = (JSONArray) data.get("vendors");

                Iterator iter = arr.iterator();
                while (iter.hasNext()) {
                    JSONObject currVendor = (JSONObject) iter.next();
                    Vendor v = new Vendor((String) currVendor.get("vendorName"), (String) currVendor.get("location"), (String) currVendor.get("description"), (String) currVendor.get("email"));
                    vendors.add(v);
                    System.out.println("vendors: " + v.toString());
                    recyclerAdapter.notifyItemInserted(0);
                }

            }

            @Override
            public void onFetchFailure(String msg) {
                Log.d("Vendors Fragment", "fetch failure: " + msg);
            }

            @Override
            public void onFetchStart() {
            }
        });
    }
}