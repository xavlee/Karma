package edu.upenn.cis350.karma;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VendorRecyclerAdapter extends RecyclerView.Adapter<VendorRecyclerAdapter.MyViewHolder> {

    Context mContext;
    List<Vendor> mVendor;
    String user;

    public VendorRecyclerAdapter(Context mContext, List<Vendor> mVendor, String user) {
        this.mContext = mContext;
        this.mVendor = mVendor;
        this.user = user;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_vendor, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tvName.setText(mVendor.get(position).getName());
        holder.tvLocation.setText(mVendor.get(position).getLocation());
        holder.tvDescription.setText(mVendor.get(position).getDescription());
        holder.menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v ) {
                Intent intent = new Intent(v.getContext(), MenuActivity.class);
                intent.putExtra("name", mVendor.get(position).getName());
                intent.putExtra("vendorEmail", mVendor.get(position).getEmail());
                intent.putExtra("user", user);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVendor.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvLocation, tvDescription;
        private Button menuBtn;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.vendor_name);
            tvLocation = (TextView) itemView.findViewById(R.id.vendor_location);
            tvDescription = (TextView) itemView.findViewById(R.id.vendor_description);
            menuBtn = (Button) itemView.findViewById(R.id.button);
        }
    }
}
