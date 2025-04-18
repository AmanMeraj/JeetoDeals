package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deals.jeetodeals.Model.TrackingResponse;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.WebViewActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TrackingAdapter extends RecyclerView.Adapter<TrackingAdapter.ViewHolder> {

    private final ArrayList<TrackingResponse.Data> trackingList;
    private final Context context;

    public TrackingAdapter(Context context, ArrayList<TrackingResponse.Data> trackingList) {
        this.context = context;
        this.trackingList = trackingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_tracking_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TrackingResponse.Data trackingData = trackingList.get(position);

        holder.tvOrderId.setText("#" + trackingData.getId());
        holder.tvOrderDate.setText("Date : " + formatDate(trackingData.getDate()));

        // Check if note contains a tracking URL
        String note = trackingData.getNote();
        String trackingUrl = extractTrackingUrl(note);
        String trackingNumber = extractTrackingNumber(note);

        if (trackingUrl != null && !trackingUrl.isEmpty()) {
            holder.btnView.setVisibility(View.VISIBLE);
            holder.btnView.setOnClickListener(v -> {
                // Use the title "Tracking Details" or include tracking number if available
                String title = "Tracking Details";
                if (trackingNumber != null && !trackingNumber.isEmpty()) {
                    title = "Tracking #" + trackingNumber;
                }
                openWebViewActivity(trackingUrl, title);
            });
        } else {
            holder.btnView.setVisibility(View.GONE);
        }
    }

    private String extractTrackingUrl(String note) {
        if (note == null || note.isEmpty()) {
            return null;
        }

        // Extract URL from note - assuming format "Tracking URL-https://..."
        if (note.contains("Tracking URL-")) {
            String[] parts = note.split("Tracking URL-");
            if (parts.length > 1) {
                // If there are multiple lines after the URL, just get the URL part
                String url = parts[1].trim();
                if (url.contains("\n")) {
                    url = url.split("\n")[0].trim();
                }
                return url;
            }
        }
        return null;
    }

    private String formatDate(String dateTimeStr) {
        try {
            // Parse the input date string
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(dateTimeStr);

            // Format the date to the desired output format
            SimpleDateFormat outputFormat = new SimpleDateFormat("d MMMM yyyy hh:mm a", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            Log.e("DateFormatter", "Error parsing date: " + dateTimeStr, e);
            return dateTimeStr; // Return original string if parsing fails
        }
    }

    private String extractTrackingNumber(String note) {
        if (note == null || note.isEmpty()) {
            return null;
        }

        // Extract tracking number from note - assuming format "Tracking Number-123456789"
        if (note.contains("Tracking Number-")) {
            String[] parts = note.split("Tracking Number-");
            if (parts.length > 1) {
                // If there are multiple lines after the tracking number, just get the number part
                String trackingNumber = parts[1].trim();
                if (trackingNumber.contains("\n")) {
                    trackingNumber = trackingNumber.split("\n")[0].trim();
                }
                return trackingNumber;
            }
        }
        return null;
    }

    private void openWebViewActivity(String url, String title) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return trackingList != null ? trackingList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId;
        TextView tvOrderDate;
        Button btnView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            btnView = itemView.findViewById(R.id.btnView);
        }
    }
}