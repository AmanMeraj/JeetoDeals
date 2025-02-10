package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deals.jeetodeals.Fragments.HomeFragment.HomeResponse;
import com.deals.jeetodeals.Model.MyItem;
import com.deals.jeetodeals.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;

public class AdapterPromotion1 extends RecyclerView.Adapter<AdapterPromotion1.MyViewHolder> {

    private final Context context;
    private final ArrayList<HomeResponse> itemList;
    private final OnItemClickListener listener;

    // Constructor
    public AdapterPromotion1(Context context, ArrayList<HomeResponse> itemList, OnItemClickListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onAddToCartClicked(HomeResponse item);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_promotion_1_row, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        HomeResponse item = itemList.get(position);

        int totalSales = item.getExtensions().getCustom_lottery_data().getTotal_sales();
        int maxTickets = Integer.parseInt(item.getExtensions().getCustom_lottery_data().getMax_tickets());

        // Calculate the percentage
        int percentage = (int) ((totalSales / (float) maxTickets) * 100);
        item.getExtensions().getCustom_lottery_data().setProgress(percentage);

        holder.totalCardsTv.setText(totalSales + " sold out of " + maxTickets);
        holder.progressIndicator.setProgress(item.getExtensions().getCustom_lottery_data().getProgress());

        // Correctly load the thumbnail for the current item
        if (item.getImages() != null && !item.getImages().isEmpty()) {
            String thumbnailUrl = item.getImages().get(0).getThumbnail(); // Use the first thumbnail of the current item
            Glide.with(context)
                    .load(thumbnailUrl)
                    .placeholder(R.drawable.promotion_image)
                    .error(R.drawable.promotion_image)
                    .into(holder.imagePromotion);
        } else {
            holder.imagePromotion.setImageResource(R.drawable.promotion_image);
        }

        holder.Voucher.setText(item.getPrices().getPrice() + " " + item.getPrices().getCurrency_prefix());

        holder.addToCartButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddToCartClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // ViewHolder class
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView totalCardsTv, Voucher;
        RelativeLayout addToCartButton;
        LinearProgressIndicator progressIndicator;
        ImageView imagePromotion;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Voucher = itemView.findViewById(R.id.my_ticket);
            totalCardsTv = itemView.findViewById(R.id.total_cards_tv);
            progressIndicator = itemView.findViewById(R.id.progress_indicator);
            imagePromotion = itemView.findViewById(R.id.image_promotion);
            addToCartButton = itemView.findViewById(R.id.button_rel);  // Correct the ID here
        }
    }

}
