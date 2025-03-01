package com.deals.jeetodeals.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deals.jeetodeals.databinding.DialogQrCodeBinding;
import com.deals.jeetodeals.databinding.RowTickectScannerBinding;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.List;

public class TickectChildAdapter extends RecyclerView.Adapter<TickectChildAdapter.ViewHolder> {
    private final Context context;
    private final List<String> ticketNumbers;
    private final String username; // Assuming username is available

    public TickectChildAdapter(Context context, List<String> ticketNumbers, String username) {
        this.context = context;
        this.ticketNumbers = ticketNumbers;
        this.username = username;
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
        String ticketNumber = ticketNumbers.get(position);
        holder.binding.textTicketId.setText(ticketNumber);

        // Click to show QR in AlertDialog
        holder.binding.image.setOnClickListener(view -> showQrDialog(ticketNumber));
    }

    @Override
    public int getItemCount() {
        return ticketNumbers != null ? ticketNumbers.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RowTickectScannerBinding binding;

        ViewHolder(RowTickectScannerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    // Method to generate and show QR code in AlertDialog
    private void showQrDialog(String ticketNumber) {
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
                    username + "-" + ticketNumber, // QR content
                    BarcodeFormat.QR_CODE, 500, 500
            );
            dialogBinding.qrImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog.show();
    }

}
