package edu.upenn.cis350.karma;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.upenn.cis350.karma.VendorSide.Order;


public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.feedViewHolder> {
    Context mContext;
    List<Order> orders;

    public FeedRecyclerAdapter(Context mContext, List<Order> orders) {
        this.mContext = mContext;
        this.orders = orders;
    }

    @Override
    public feedViewHolder onCreateViewHolder (ViewGroup parent, int ViewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_feed,
                parent, false);
        FeedRecyclerAdapter.feedViewHolder holder = new feedViewHolder(view);
        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull FeedRecyclerAdapter.feedViewHolder holder, int position) {
        holder.friendName.setText("Your friend, " + orders.get(position).getUser() + ", bought");
        holder.purchase.setText(orders.get(position).getFirstItem());
        holder.fromVendor.setText(" from " + orders.get(position).getVendor() + ". Yum!");
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class feedViewHolder extends RecyclerView.ViewHolder {

        TextView friendName;
        TextView purchase;
        TextView fromVendor;
        RelativeLayout foodLayout;

        public feedViewHolder (View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.friend_name);
            purchase = itemView.findViewById(R.id.purchase_name);
            fromVendor = itemView.findViewById(R.id.from_vendor);
            foodLayout = itemView.findViewById(R.id.order_layout);
        }

    }

}
