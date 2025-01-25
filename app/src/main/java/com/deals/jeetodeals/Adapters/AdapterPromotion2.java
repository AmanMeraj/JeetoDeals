package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.deals.jeetodeals.BottomSheet.BottomSheetPromotion;
import com.deals.jeetodeals.Model.Promotion;
import com.deals.jeetodeals.databinding.RowPromotion2Binding;

import java.util.List;

public class AdapterPromotion2 extends RecyclerView.Adapter<AdapterPromotion2.PromotionViewHolder> {
    private final Context context;
    private final List<Promotion> promotions;

    // Constructor
    public AdapterPromotion2(Context context, List<Promotion> promotions) {
        this.context = context;
        this.promotions = promotions;
    }

    @NonNull
    @Override
    public PromotionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowPromotion2Binding binding = RowPromotion2Binding.inflate(
                LayoutInflater.from(context), parent, false);
        return new PromotionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PromotionViewHolder holder, int position) {
        Promotion promotion = promotions.get(position);

        // Set data to views
        holder.binding.timeTv.setText(promotion.getTime());
        holder.binding.image.setImageResource(promotion.getImageResourceId());
        holder.binding.textItemLeft.setText(promotion.getTotalTickets());
        holder.binding.winTv.setText(promotion.getWinText());
        holder.binding.heading.setText(promotion.getHeading());
        holder.binding.desc.setText(promotion.getDescription());
        holder.binding.chanceTv.setText(promotion.getChanceText());
        holder.binding.heading2.setText(promotion.getHeading2());
        holder.binding.vouchersTv.setText(promotion.getVouchers());

        // Info button click listener
        holder.binding.infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show the bottom sheet dialog
                BottomSheetPromotion bottomSheetPromotion = BottomSheetPromotion.newInstance(promotion);
                if (context instanceof FragmentActivity) {
                    FragmentActivity activity = (FragmentActivity) context;
                    bottomSheetPromotion.show(activity.getSupportFragmentManager(), "BottomSheetPromotion");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return promotions.size();
    }

    public static class PromotionViewHolder extends RecyclerView.ViewHolder {
        private final RowPromotion2Binding binding;

        public PromotionViewHolder(RowPromotion2Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
