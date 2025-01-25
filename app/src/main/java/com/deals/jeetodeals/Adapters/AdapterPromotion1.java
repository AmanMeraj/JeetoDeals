package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.deals.jeetodeals.Model.MyItem;
import com.deals.jeetodeals.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.util.List;

public class AdapterPromotion1 extends RecyclerView.Adapter<AdapterPromotion1.MyViewHolder> {

    private final Context context;
    private final List<MyItem> itemList;

    // Constructor
    public AdapterPromotion1(Context context, List<MyItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_promotion_1_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MyItem item = itemList.get(position);

        // Set data to views
        holder.totalCardsTv.setText(item.getTotalCardsText());
        holder.progressIndicator.setProgress(item.getProgress());
        holder.imagePromotion.setImageResource(item.getImageResId());
        holder.chanceTv.setText(item.getChanceText());
        holder.winTv.setText(item.getWinText());
//        holder.addToCartButton.setText(item.getButtonText());

        // Handle button click
//        holder.addToCartButton.setOnClickListener(v -> {
//            // Handle Add to Cart action
//        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // ViewHolder class
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView totalCardsTv, chanceTv, winTv, addToCartButton;
        LinearProgressIndicator progressIndicator;
        ImageView imagePromotion;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            totalCardsTv = itemView.findViewById(R.id.total_cards_tv);
            progressIndicator = itemView.findViewById(R.id.progress_indicator);
            imagePromotion = itemView.findViewById(R.id.image_promotion);
            chanceTv = itemView.findViewById(R.id.chance_tv);
            winTv = itemView.findViewById(R.id.tv_win);
            addToCartButton = itemView.findViewById(R.id.button_rel).findViewById(android.R.id.text1); // Adjust if the ID is different
        }
    }
}
