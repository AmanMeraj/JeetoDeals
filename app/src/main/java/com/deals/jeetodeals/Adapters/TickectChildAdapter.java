package com.deals.jeetodeals.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deals.jeetodeals.databinding.DialogQrCodeBinding;
import com.deals.jeetodeals.databinding.RowTickectScannerBinding;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TickectChildAdapter extends RecyclerView.Adapter<TickectChildAdapter.ViewHolder> {
    private final Context context;
    private final List<Map<String, String>> ticketInfoList;
    private final String username;
    private final String promotion;
    String fDate;

    public TickectChildAdapter(Context context, List<Map<String, String>> ticketInfoList, String username, String promotion) {
        this.context = context;
        this.ticketInfoList = ticketInfoList;
        this.username = username;
        this.promotion = promotion;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowTickectScannerBinding binding = RowTickectScannerBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, String> ticketInfo = ticketInfoList.get(position);
        String ticketNumber = ticketInfo.get("ticket_number");
        String datePurchased = ticketInfo.get("date_purchased");

        holder.binding.textTicketId.setText(ticketNumber);

        // Display purchase date if available
        if (datePurchased != null && !datePurchased.isEmpty()) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                Date date = inputFormat.parse(datePurchased);
                String formattedDate = outputFormat.format(date);
                fDate=formattedDate;

                // Assuming you have a textDate field in your row_tickect_scanner.xml
//                if (holder.binding.textDate != null) {
//                    holder.binding.textDate.setVisibility(View.VISIBLE);
//                    holder.binding.textDate.setText(formattedDate);
//                }
            } catch (ParseException e) {
//                // If date parsing fails, show raw date string
//                if (holder.binding.textDate != null) {
//                    holder.binding.textDate.setVisibility(View.VISIBLE);
//                    holder.binding.textDate.setText(datePurchased);
//                }
            }
        } else {
//            // Hide date field if no date available
//            if (holder.binding.textDate != null) {
//                holder.binding.textDate.setVisibility(View.GONE);
//            }
        }

        // Click to show QR in AlertDialog
        holder.binding.image.setOnClickListener(view ->
                showQrDialog(ticketNumber, promotion, username, fDate != null ? fDate : "")
        );
    }

    @Override
    public int getItemCount() {
        return ticketInfoList != null ? ticketInfoList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RowTickectScannerBinding binding;

        ViewHolder(RowTickectScannerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    // Method to generate and show QR code in AlertDialog
    private void showQrDialog(String ticketNumber, String product, String username, String purchaseDate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Inflate the custom layout for the dialog
        DialogQrCodeBinding dialogBinding = DialogQrCodeBinding.inflate(LayoutInflater.from(context));
        builder.setView(dialogBinding.getRoot());

        AlertDialog dialog = builder.create();

        // Set background to transparent and apply rounded corners
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        dialog.setCanceledOnTouchOutside(true); // Dismiss dialog on outside touch

        // Generate QR Code
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(
                    "Customer: " + username + "\n" +
                            "Ticket No: " + ticketNumber + "\n" +
                            "Promotion: " + product + "\n" +
                            "Purchase Date: " + purchaseDate,
                    BarcodeFormat.QR_CODE, 500, 500
            );
            dialogBinding.qrImage.setImageBitmap(bitmap);



        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog.show();
    }
}