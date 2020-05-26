package edu.upenn.cis350.karma;
import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ViewHolder>  {

    private ArrayList<FoodItem> order;
    private Context mContext;
    Double total = 0.0;
    TextView totalDisplay;

    public CheckoutAdapter(Context context, ArrayList<FoodItem> order, TextView totalDisplay) {
        this.order = order;
        this.mContext = context;
        this.totalDisplay = totalDisplay;
        for (FoodItem item : order) {
            total = total + ((item.getPrice()/100.0)*item.getQuantity());
        }
        totalDisplay.setText("Grand Total: $" + String.format("%.2f", total));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.itemName.setText(order.get(position).getItem());
        Double d = order.get(position).getPrice()/100.0;
        holder.itemPrice.setText("$" + String.format("%.2f", d));
        Log.d("checkoutAdapter", order.get(position).getQuantity().toString());
        holder.itemQuantity.setText(order.get(position).getQuantity().toString());
        final Double subtotalAmount = d*order.get(position).getQuantity();
        holder.subtotal.setText("$" + String.format("%.2f", subtotalAmount));
        holder.cancelItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order.remove(position);
                total = total - subtotalAmount;
                totalDisplay.setText("Grand Total: $" + String.format("%.2f", total));
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return order.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        TextView itemPrice;
        TextView itemQuantity;
        TextView subtotal;
        Button cancelItem;
        RelativeLayout checkoutLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.food_name);
            itemPrice = itemView.findViewById(R.id.food_price);
            itemQuantity = itemView.findViewById(R.id.food_quantity);
            cancelItem = itemView.findViewById(R.id.remove_items_button);
            subtotal = itemView.findViewById(R.id.subtotal);
            checkoutLayout = itemView.findViewById(R.id.checkout_layout);
        }

    }


}
