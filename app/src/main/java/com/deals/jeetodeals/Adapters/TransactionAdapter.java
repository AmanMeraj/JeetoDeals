package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deals.jeetodeals.Model.Transaction;
import com.deals.jeetodeals.Model.WalletResponse;
import com.deals.jeetodeals.databinding.RowTransactionBinding;

import java.util.List;

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
        holder.binding.desc.setText(transaction.getDetails());
//        holder.binding.desc.setText(transaction.getDescription());
        try {
            double amount = Double.parseDouble(transaction.getAmount());
            holder.binding.amount.setText(String.format("₹ %.2f", amount));
        } catch (NumberFormatException e) {
            holder.binding.amount.setText("₹ 0.00"); // Fallback for invalid values
            e.printStackTrace(); // Logs the error for debugging
        }

        holder.binding.status.setText(transaction.getType());
//        holder.binding.imageCard.setBackgroundResource(transaction.getImageBackground());
//        holder.binding.imageCardLogo.setImageResource(transaction.getImageResource());
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
