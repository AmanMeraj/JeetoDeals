package com.deals.jeetodeals.Adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deals.jeetodeals.Model.Orders;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.databinding.RowOrderBinding;
import java.util.List;
public class AdapterOrders extends RecyclerView.Adapter<AdapterOrders.ViewHolder> {

        private final Context context;
        private final List<Orders> itemList;

        public AdapterOrders(Context context, List<Orders> itemList) {
            this.context = context;
            this.itemList = itemList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RowOrderBinding binding = RowOrderBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Orders item = itemList.get(position);

            // Bind data to views
            holder.binding.timeTv.setText(item.getTime());
            holder.binding.tvStatus.setText(item.getStatus());
            holder.binding.tvTitle.setText(item.getTitle());
            holder.binding.tvVoucher.setText(item.getVoucher());
            holder.binding.tvDate.setText(item.getDate());

            // Load image using Glide or other libraries
            Glide.with(context)
                    .load(item.getImageUrl()) // URL or resource ID
                    .placeholder(R.drawable.promotion_image) // Replace with your placeholder
                    .into(holder.binding.imagePhone);
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            final RowOrderBinding binding;

            public ViewHolder(RowOrderBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }

}
