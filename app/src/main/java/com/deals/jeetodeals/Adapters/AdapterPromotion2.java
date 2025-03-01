package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deals.jeetodeals.BottomSheet.BottomSheetPromotion;
import com.deals.jeetodeals.Fragments.HomeFragment.HomeResponse;
import com.deals.jeetodeals.Model.Promotion;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.Utils.SharedPref;
import com.deals.jeetodeals.databinding.RowPromotion2Binding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AdapterPromotion2 extends RecyclerView.Adapter<AdapterPromotion2.PromotionViewHolder> {
    private final Context context;
    SharedPref pref= new SharedPref();
    private final ArrayList<HomeResponse> itemList;
    private final OnItemClickListener2 listener;

    // Constructor
    public AdapterPromotion2(Context context, ArrayList<HomeResponse> itemList, OnItemClickListener2 listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;

        // Optional check to ensure context is a FragmentActivity
        if (!(context instanceof FragmentActivity)) {
            throw new IllegalArgumentException("Context must be a FragmentActivity.");
        }
    }
    public interface OnItemClickListener2 {
        void onAddToCartClicked2(HomeResponse item);
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
        HomeResponse promotion = itemList.get(position);

        // Set data to views
        if (promotion.getImages() != null && !promotion.getImages().isEmpty()) {
            String thumbnailUrl = promotion.getImages().get(0).getThumbnail(); // Use the first thumbnail of the current item
            Glide.with(context)
                    .load(thumbnailUrl)
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.error_image)
                    .into(holder.binding.image);
        } else {
            holder.binding.image.setImageResource(R.drawable.no_image);
        }

        holder.binding.textItemLeft.setText(promotion.getExtensions().getCustom_lottery_data().getMax_tickets());
        holder.binding.heading.setText(promotion.getName());
        holder.binding.heading2.setText(promotion.getName());
        // Set data to views
        Log.d("AdapterCard2", "Raw Price: " + promotion.getPrices().getPrice());
        Log.d("AdapterCard2", "Currency Prefix: " + promotion.getPrices().getCurrency_prefix());

        if (context != null) {
            int voucherRate = pref.getPrefInteger(context, pref.voucher_rate);
            Log.d("AdapterCard2", "Voucher Rate from SharedPref: " + voucherRate);

            // Ensure voucherRate is not zero to prevent division errors
            if (voucherRate != 0) {
                try {
                    float price = Float.parseFloat(promotion.getPrices().getPrice());
                    float calculatedPrice = price / voucherRate;
                    Log.d("AdapterCard2", "Calculated Price: " + calculatedPrice);
                    holder.binding.vouchersTv.setText((int) calculatedPrice + " " + promotion.getPrices().getCurrency_prefix());
                } catch (NumberFormatException e) {
                    Log.e("AdapterCard2", "Error parsing price: " + promotion.getPrices().getPrice(), e);
                    holder.binding.vouchersTv.setText("Invalid Price");
                }
            } else {
                holder.binding.vouchersTv.setText("Invalid Rate");
            }
        } else {
            Log.e("AdapterCard2", "Context is null");
            holder.binding.vouchersTv.setText("Context is null");
        }
        String endDate = promotion.getExtensions().getCustom_lottery_data().getLottery_dates_to(); // e.g., "2025-05-31 00:00"
        calculateCountdown(holder, endDate);
        String formattedDate = formatDate(endDate);
        holder.binding.desc.setText("Draw date: " + formattedDate + " or earlier based on the time passed.");

        // Info button click listener
        holder.binding.infoBtn.setOnClickListener(v -> {
            // Get the clicked HomeResponse object from the adapter's data
            HomeResponse clickedHomeResponse = itemList.get(position);

            // Create and show the BottomSheet with the clicked HomeResponse data
            // Ensure context is FragmentActivity for show() method
            if (context instanceof FragmentActivity) {
                BottomSheetPromotion bottomSheet = BottomSheetPromotion.newInstance(clickedHomeResponse);
                bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
            }
        });
        holder.binding.addCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.binding.addCartBtn.setEnabled(false);
                holder.binding.tvAdd.setText("Adding...");
                if (listener != null) {
                    listener.onAddToCartClicked2(promotion);
                }
                // Re-enable button after delay (this is a backup in case the fragment's cooldown doesn't work)
                new Handler().postDelayed(() -> {
                    holder.binding.addCartBtn.setEnabled(true);
                    holder.binding.tvAdd.setText("Add to Cart");
                }, 1000); // 1
            }
        });
    }
    private void calculateCountdown(PromotionViewHolder holder, String endDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date endDateTime = sdf.parse(endDate);
            if (endDateTime == null) return;

            long currentTime = System.currentTimeMillis();
            long endTime = endDateTime.getTime();
            long timeDiff = endTime - currentTime;

            if (timeDiff > 86400000) { // More than 1 day left (86400000 ms = 1 day)
                long daysLeft = TimeUnit.MILLISECONDS.toDays(timeDiff);
                holder.binding.timeTv.setText("Closing in "+daysLeft+" Day's");
            } else if (timeDiff > 0) { // Less than a day, start countdown
                new CountDownTimer(timeDiff, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
                        long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
                        holder.binding.timeTv.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
                    }

                    @Override
                    public void onFinish() {
                        holder.binding.timeTv.setText("Ended");
                    }
                }.start();
            } else {
                holder.binding.timeTv.setText("Ended");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            holder.binding.timeTv.setText("Error");
        }
    }
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            return (date != null) ? outputFormat.format(date) : "Invalid date";
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid date";
        }
    }



    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class PromotionViewHolder extends RecyclerView.ViewHolder {
        private final RowPromotion2Binding binding;

        public PromotionViewHolder(RowPromotion2Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
