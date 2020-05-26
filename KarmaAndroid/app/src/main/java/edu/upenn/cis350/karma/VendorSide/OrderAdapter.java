package edu.upenn.cis350.karma.VendorSide;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;


import org.json.simple.JSONObject;

import java.util.List;

import edu.upenn.cis350.karma.R;
import edu.upenn.cis350.karma.WebConnection.DataProcessor;
import edu.upenn.cis350.karma.WebConnection.FetchDataListener;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private List<Order> orders;
    private OnItemClickListener listener;
    private DataProcessor dp;
    private Context ctx;

    public OrderAdapter(List<Order> orders) {
        if (orders == null) {
            throw new IllegalArgumentException("can't make an adapter with null orders");
        }
        this.orders = orders;

        dp = new DataProcessor();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);

        View orderView = inflater.inflate(R.layout.item_vendor_order_list,
                parent, false);

        ViewHolder viewHolder = new ViewHolder(orderView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        // Get the data model based on position
        final Order order = orders.get(position);

        // Set item views based on your views and data model
        TextView tvOrderCode = holder.tvOrderCode;
        TextView tvMenuItems = holder.tvMenuItems;
        Button doneBtn = holder.doneBtn;

        Resources res = holder.itemView.getResources();

        tvOrderCode.setText(res.getString(R.string.order_code_title, order.getCode()));
        tvMenuItems.setText(order.getItems());

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                dp.completeOrder(order.getTimestamp(), ctx.getApplicationContext(),
                        new FetchDataListener() {
                            @Override
                            public void onFetchComplete(JSONObject data) {
                                if (data.get("error") != null) {
                                    Log.d("OrderAdapterOnClick", (String) data.get("error"));
                                    Snackbar.make(view, "Order Couldn\'t be Completed",
                                            Snackbar.LENGTH_SHORT).show();
                                } else {
                                    Boolean completed = (Boolean) data.get("completed");
                                    if (completed) {
                                        orders.remove(position);
                                        notifyItemRemoved(position);

                                        Snackbar.make(view.getRootView(), "Order Completed!",
                                                Snackbar.LENGTH_SHORT).show();
                                    } else {
                                        Snackbar.make(view, "Order Couldn\'t be Completed",
                                                Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFetchFailure(String msg) {
                                Snackbar.make(view, "Order Couldn\'t be Completed",
                                        Snackbar.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFetchStart() {

                            }
                        });
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

        @Override
    public int getItemCount() {
        if (orders == null) {
            return 0;
        }

        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvOrderCode;
        public Button doneBtn;
        public TextView tvMenuItems;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            tvOrderCode = (TextView) itemView.findViewById(R.id.tvOrderCode);
            doneBtn = (Button) itemView.findViewById(R.id.doneBtn);
            tvMenuItems = (TextView) itemView.findViewById(R.id.tvMenuItems);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, position);
                        }
                    }
                }
            });
        }
    }
}
