package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.os.Handler;
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
import com.deals.jeetodeals.Utils.SharedPref;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;

public class AdapterPromotion1 extends RecyclerView.Adapter<AdapterPromotion1.MyViewHolder> {

    private final Context context;
    SharedPref pref=new SharedPref();
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

        holder.name.setText(item.getName());

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
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.error_image)
                    .into(holder.imagePromotion);
        } else {
            holder.imagePromotion.setImageResource(R.drawable.no_image);
        }

// Get the voucher rate as a float
        // Use getPrefInt() instead of getPrefString() since it's stored as an integer
        int voucherRate = pref.getPrefInteger(context, pref.voucher_rate);

// Ensure voucherRate is not zero to prevent division errors
        if (voucherRate != 0) {
            int calculatedPrice = (int) (Integer.parseInt(item.getPrices().getPrice()) / (float) voucherRate);
            holder.Voucher.setText(calculatedPrice + " " + item.getPrices().getCurrency_prefix());
        } else {
            holder.Voucher.setText("Invalid Rate");
        }


        holder.addToCartButton.setOnClickListener(v -> {
            holder.addToCartButton.setEnabled(false);
            holder.add.setText("Adding...");
            if (listener != null) {
                listener.onAddToCartClicked(item);
            }
            new Handler().postDelayed(() -> {
                holder.addToCartButton.setEnabled(true);
                holder.add.setText("Add to Cart");
            }, 1000);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // ViewHolder class
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView totalCardsTv, Voucher,add,name;
        RelativeLayout addToCartButton;
        LinearProgressIndicator progressIndicator;
        ImageView imagePromotion;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Voucher = itemView.findViewById(R.id.my_ticket);
            totalCardsTv = itemView.findViewById(R.id.total_cards_tv);
            progressIndicator = itemView.findViewById(R.id.progress_indicator);
            imagePromotion = itemView.findViewById(R.id.image_promotion);
            add=itemView.findViewById(R.id.tv_add);
            name=itemView.findViewById(R.id.name);
            addToCartButton = itemView.findViewById(R.id.button_rel);  // Correct the ID here
        }
    }

}
