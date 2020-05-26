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

public class MenuRecyclerAdapter extends RecyclerView.Adapter<MenuRecyclerAdapter.ViewHolder>{

    private ArrayList<FoodItem> menuItems = new ArrayList<>();
    private Context mContext;


    public MenuRecyclerAdapter(ArrayList<FoodItem> menu, Context context) {
        this.menuItems = menu;
        this.mContext = context;
                
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_fooditem,
                parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.itemName.setText(menuItems.get(position).getItem());
        Double d = menuItems.get(position).getPrice()/100.0;
        holder.itemPrice.setText("$" + String.format("%.2f", d));
        holder.quantityDisplay.setText(holder.quantity.toString());

        holder.incrementer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.quantity = holder.quantity + 1;
                menuItems.get(position).setQuantity(holder.quantity.longValue());
                holder.quantityDisplay.setText(holder.quantity.toString());
            }
        });

        holder.decrementer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.quantity > 0) {
                    holder.quantity =  holder.quantity - 1;
                }
                menuItems.get(position).setQuantity(holder.quantity.longValue());
                holder.quantityDisplay.setText(holder.quantity.toString());
            }
        });

    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public ArrayList<FoodItem> getOrder() {
        ArrayList<FoodItem> order = new ArrayList<>();
        for (FoodItem f: menuItems) {
            if (f.getQuantity() != null && f.getQuantity() != 0) {
                order.add(f);
            }
        }
        return order;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        TextView itemPrice;
        TextView quantityDisplay;
        RelativeLayout foodLayout;
        Button incrementer;
        Button decrementer;
        Integer quantity;


        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            incrementer = itemView.findViewById(R.id.increment_button);
            decrementer = itemView.findViewById(R.id.decrement_button);
            quantityDisplay = itemView.findViewById(R.id.item_quantity);
            foodLayout = itemView.findViewById(R.id.food_layout);
            quantity = 0;
        }
    }

}
