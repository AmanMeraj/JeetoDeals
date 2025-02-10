package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deals.jeetodeals.Model.Cart;
import com.deals.jeetodeals.Model.Items;
import com.deals.jeetodeals.R;

import java.util.List;

public class AdapterCart extends RecyclerView.Adapter<AdapterCart.QuantityViewHolder> {

    private final Context context;
    private final List<Items> itemList;
    private final OnCartItemActionListener cartItemActionListener;

    public interface OnCartItemActionListener {
        void onItemDeleted(int position);
        void onQuantityIncreased(int position);
        void onQuantityDecreased(int position);
    }

    public AdapterCart(Context context, List<Items> itemList, OnCartItemActionListener cartItemActionListener) {
        this.context = context;
        this.itemList = itemList;
        this.cartItemActionListener = cartItemActionListener;
    }

    @NonNull
    @Override
    public QuantityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_cart, parent, false);
        return new QuantityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuantityViewHolder holder, int position) {
        Items item = itemList.get(position);
        Log.d("ITEM LIST", "onBindViewHolder: "+itemList.size());
        holder.textPhoneName.setText(item.getName());
        holder.textTicketId.setText(item.getPrices().getCurrency_symbol()+" "+item.getPrices().getPrice());
        holder.desc.setText(item.getName());
        holder.textQuantity.setText(String.valueOf(item.getQuantity()));

        holder.plus.setOnClickListener(v -> {
            if (cartItemActionListener != null) {
                cartItemActionListener.onQuantityIncreased(position);
            }
        });

        // Minus button (Decrease quantity)
        holder.minus.setOnClickListener(v -> {
            if (cartItemActionListener != null) {
                cartItemActionListener.onQuantityDecreased(position);
            }
        });
        holder.delete.setOnClickListener(v -> {
            if (cartItemActionListener != null) {
                cartItemActionListener.onItemDeleted(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class QuantityViewHolder extends RecyclerView.ViewHolder {
        TextView textPhoneName, textTicketId, desc, textQuantity;
        ImageView plus, minus,delete;

        public QuantityViewHolder(@NonNull View itemView) {
            super(itemView);
            textPhoneName = itemView.findViewById(R.id.text_phone_name);
            textTicketId = itemView.findViewById(R.id.text_ticket_id);
            desc = itemView.findViewById(R.id.desc);
            textQuantity = itemView.findViewById(R.id.text_quantity);
            plus = itemView.findViewById(R.id.plus);
            minus = itemView.findViewById(R.id.minus);
            delete= itemView.findViewById(R.id.delete);
        }
    }
}
