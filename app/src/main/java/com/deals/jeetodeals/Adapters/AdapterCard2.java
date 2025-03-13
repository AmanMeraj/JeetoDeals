package com.deals.jeetodeals.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deals.jeetodeals.Model.ShopResponse;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.databinding.RowCategory2Binding;

import java.util.List;

public class AdapterCard2 extends RecyclerView.Adapter<AdapterCard2.PromotionViewHolder> {
    private List<ShopResponse> promotionList;
    private OnItemClickListener listener;

    // Constructor
    public AdapterCard2(List<ShopResponse> promotionList) {
        this.promotionList = promotionList;
    }

    // Interface for click events
    public interface OnItemClickListener {
        void onLikeClick(String productId);
        void onShareClick(String permalink);
        void onAddToCartClick(ShopResponse product);
        void onItemClick(ShopResponse product);
    }

    // Setter method for the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PromotionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowCategory2Binding binding = RowCategory2Binding.inflate(inflater, parent, false);
        return new PromotionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PromotionViewHolder holder, int position) {
        ShopResponse promotion = promotionList.get(position);

        // Set data to views
        holder.binding.timeTv.setText(promotion.getPrices().getPrice() + " " + promotion.getPrices().getCurrency_prefix());
        holder.binding.desc.setText(promotion.name);
        Log.d("TAG", "onBindViewHolder: " + promotion.getDescription());

        // Load image using Glide
        Glide.with(holder.binding.image.getContext())
                .load(promotion.getImages().get(0).getThumbnail())
                .placeholder(R.drawable.no_image)
                .error(R.drawable.error_image)
                .into(holder.binding.image);

        // Click listeners
        holder.binding.likeBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLikeClick(String.valueOf(promotion.getId())); // Pass product ID
            }
        });

        holder.binding.shareBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onShareClick(promotion.getPermalink());
            }
        });

        holder.binding.addCartRel.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddToCartClick(promotion);
            }
        });
        holder.itemView.setOnClickListener(v ->{
            if(listener!= null){
                listener.onItemClick(promotion);
            }
        });
    }

    @Override
    public int getItemCount() {
        return promotionList != null ? promotionList.size() : 0;
    }

    static class PromotionViewHolder extends RecyclerView.ViewHolder {
        RowCategory2Binding binding;

        public PromotionViewHolder(@NonNull RowCategory2Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
