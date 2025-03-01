package com.deals.jeetodeals.Adapters;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deals.jeetodeals.Model.OrderItem;
import com.deals.jeetodeals.Model.Orders;
import com.deals.jeetodeals.MyOrders.MyOrdersResponse;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.Utils.SharedPref;
import com.deals.jeetodeals.databinding.RowOrderBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdapterOrders extends RecyclerView.Adapter<AdapterOrders.ViewHolder> {
    private final Context context;
    SharedPref pref = new SharedPref();
    private final List<MyOrdersResponse> itemList;

    public AdapterOrders(Context context, List<MyOrdersResponse> itemList) {
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
        MyOrdersResponse order = itemList.get(position);

        // Format date
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());

            Date date = inputFormat.parse(order.getDate_created());
            if (date != null) {
                holder.binding.tvDate.setText(outputFormat.format(date));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            holder.binding.tvDate.setText(order.getDate_created());
        }

        // Set order status
        holder.binding.tvStatus.setText(order.getStatus());
        holder.binding.timeTv.setText("# "+order.getOrder_id());
        setStatusColor(holder.binding.tvStatus, order.getStatus());

        // Get first item's name as title
        if (!order.getItems().isEmpty()) {
            Map.Entry<String, OrderItem> firstItem = order.getItems().entrySet().iterator().next();
            OrderItem item = firstItem.getValue();
            holder.binding.tvTitle.setText(item.getProduct_name());
        }

        // Set order ID as voucher number
        if (!order.getItems().isEmpty()) {
            Map.Entry<String, OrderItem> firstItem = order.getItems().entrySet().iterator().next();
            OrderItem item = firstItem.getValue();
            holder.binding.tvTitle.setText(item.getProduct_name());

            // Assuming OrderItem has a method getQuantity() to get item quantity
            int quantity = item.getQuantity();
            int voucherRate = pref.getPrefInteger(context, pref.voucher_rate);
            String itemPrice = order.getTotal();

            Log.d("AdapterCart", "Voucher Rate: " + voucherRate);
            Log.d("AdapterCart", "Item Price: " + itemPrice);
            Log.d("AdapterCart", "Order Total: " + order.getTotal());

            if (voucherRate != 0 && itemPrice != null && !itemPrice.isEmpty()) {
                try {
                    int calculatedPrice = (int) (Integer.parseInt(itemPrice) / (float) voucherRate);// Divide total by calculated price

                    holder.binding.tvVoucher.setText("Vouchers: " + calculatedPrice + " for " + quantity + " Items");
                } catch (NumberFormatException e) {
                    Log.e("AdapterCart", "Error parsing item price: " + itemPrice, e);
                    holder.binding.tvVoucher.setText("Vouchers: " + order.getTotal() + " for " + quantity + " Items");
                }
            } else {
                holder.binding.tvVoucher.setText("Vouchers: " + order.getTotal() + " for " + quantity + " Items");
            }

        }
    }

    private void setStatusColor(TextView textView, String status) {
        int color;
        switch (status.toLowerCase()) {
            case "completed":
                color = ContextCompat.getColor(context, R.color.green);
                break;
            case "pending":
                color = ContextCompat.getColor(context, R.color.orange);
                break;
            case "checkout-draft":
                color = ContextCompat.getColor(context, R.color.grey);
                break;
            default:
                color = ContextCompat.getColor(context, R.color.black);
                break;
        }
        textView.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void updateList(List<MyOrdersResponse> newList) {
        itemList.clear();
        itemList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final RowOrderBinding binding;

        public ViewHolder(RowOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}