package com.deals.jeetodeals.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deals.jeetodeals.Model.AllItems;
import com.deals.jeetodeals.Model.Promotion;
import com.deals.jeetodeals.databinding.RowCategory2Binding;

import java.util.List;

public class AdapterCard2 extends RecyclerView.Adapter<AdapterCard2.PromotionViewHolder> {
    private List<AllItems> promotionList;

    public AdapterCard2(List<AllItems> promotionList) {
        this.promotionList = promotionList;
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
        AllItems promotion = promotionList.get(position);
        holder.binding.timeTv.setText(promotion.getTitle());
        holder.binding.desc.setText(promotion.getDescription());
        holder.binding.image.setImageResource(promotion.getImageResId());

        holder.binding.likeBtn.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Liked " + promotion.getTitle(), Toast.LENGTH_SHORT).show();
        });

        holder.binding.shareBtn.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Shared " + promotion.getTitle(), Toast.LENGTH_SHORT).show();
        });

        holder.binding.addCartRel.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Added " + promotion.getTitle() + " to cart", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return promotionList.size();
    }

    static class PromotionViewHolder extends RecyclerView.ViewHolder {
        RowCategory2Binding binding;

        public PromotionViewHolder(@NonNull RowCategory2Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
