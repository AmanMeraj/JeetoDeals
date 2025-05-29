package com.deals.jeetodeals.Adapters;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
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
import com.deals.jeetodeals.MyOrders.ActivityMyOrderDetails;
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
        String formattedDate = "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());

            Date date = inputFormat.parse(order.getDate_created());
            if (date != null) {
                formattedDate = outputFormat.format(date);
                holder.binding.tvDate.setText(formattedDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            formattedDate = order.getDate_created();
            holder.binding.tvDate.setText(formattedDate);
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
            holder.binding.tvTitle.setText(Html.fromHtml(item.getProduct_name()));

            Glide.with(context).load(item.getProduct_image()).placeholder(R.drawable.no_image).into(holder.binding.imageCardLogo);

            // Assuming OrderItem has a method getQuantity() to get item quantity
            int quantity = item.getQuantity();
            String itemPrice = order.getTotal();
            holder.binding.tvVoucher.setText("Vouchers: " + order.getTotal() + " for " + item.getQuantity() + " Items");
        }

        // Store the final formatted date for the onClick handler
        final String finalFormattedDate = formattedDate;

        holder.binding.arrowImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ActivityMyOrderDetails.class);
                intent.putExtra("order_id", order.getOrder_id());
                intent.putExtra("formatted_date", finalFormattedDate);
                context.startActivity(intent);
            }
        });
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