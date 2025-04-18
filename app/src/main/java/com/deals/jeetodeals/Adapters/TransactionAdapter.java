package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deals.jeetodeals.Model.Transaction;
import com.deals.jeetodeals.Model.WalletResponse;
import com.deals.jeetodeals.databinding.RowTransactionBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private final Context context;
    private final List<WalletResponse> transactions;

    // Constructor
    public TransactionAdapter(Context context, List<WalletResponse> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowTransactionBinding binding = RowTransactionBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new TransactionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        WalletResponse transaction = transactions.get(position);

        // Bind data to views
        holder.binding.heading.setText(transaction.getDetails());
        Log.d("TAG", "onBindViewHolder: "+transaction.getDetails());
//        holder.binding.desc.setText(transaction.getDescription());
        try {
            double amount = Double.parseDouble(transaction.getAmount());
            holder.binding.amount.setText(transaction.currency+String.format(" %.0f", amount));
        } catch (NumberFormatException e) {
            holder.binding.amount.setText(transaction.currency+" 0.00"); // Fallback for invalid values
            e.printStackTrace(); // Logs the error for debugging
        }

        holder.binding.status.setText(transaction.getType());
        // Format date string to "28 Mar, 2025"
        String apiDate = transaction.getDate();  // e.g., "2025-04-16 15:34:06"

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());

            // Parse the date from the API
            Date date = inputFormat.parse(apiDate);

            // Format the parsed date to your required format
            if (date != null) {
                String formattedDate = outputFormat.format(date);
                holder.binding.date.setText(formattedDate);  // Assuming you have a TextView with this ID to display the date
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    // ViewHolder Class
    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final RowTransactionBinding binding;

        public TransactionViewHolder(RowTransactionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
