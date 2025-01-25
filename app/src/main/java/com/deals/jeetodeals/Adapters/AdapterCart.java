package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deals.jeetodeals.Model.Cart;
import com.deals.jeetodeals.R;

import java.util.List;

public class AdapterCart extends RecyclerView.Adapter<AdapterCart.QuantityViewHolder> {

    private final Context context;
    private final List<Cart> itemList;

    public AdapterCart(Context context, List<Cart> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public QuantityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_cart, parent, false);
        return new QuantityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuantityViewHolder holder, int position) {
        Cart item = itemList.get(position);
        holder.textPhoneName.setText(item.getName());
        holder.textTicketId.setText(item.getVoucher());
        holder.desc.setText(item.getDescription());
        holder.textQuantity.setText(String.valueOf(item.getQuantity()));

        holder.plus.setOnClickListener(v -> {
            if (item.getQuantity() < 99) { // Ensure maximum quantity is 99
                item.setQuantity(item.getQuantity() + 1);
                notifyItemChanged(position);
            } else {
                Toast.makeText(context, "Maximum quantity reached", Toast.LENGTH_SHORT).show();
            }
        });

        holder.minus.setOnClickListener(v -> {
            if (item.getQuantity() > 0) { // Prevent negative quantities
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(position);
            } else {
                Toast.makeText(context, "Minimum quantity reached", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class QuantityViewHolder extends RecyclerView.ViewHolder {
        TextView textPhoneName, textTicketId, desc, textQuantity;
        ImageView plus, minus;

        public QuantityViewHolder(@NonNull View itemView) {
            super(itemView);
            textPhoneName = itemView.findViewById(R.id.text_phone_name);
            textTicketId = itemView.findViewById(R.id.text_ticket_id);
            desc = itemView.findViewById(R.id.desc);
            textQuantity = itemView.findViewById(R.id.text_quantity);
            plus = itemView.findViewById(R.id.plus);
            minus = itemView.findViewById(R.id.minus);
        }
    }
}
