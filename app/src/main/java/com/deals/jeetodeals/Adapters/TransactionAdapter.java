package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deals.jeetodeals.Model.Transaction;
import com.deals.jeetodeals.databinding.RowTransactionBinding;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private final Context context;
    private final List<Transaction> transactions;

    // Constructor
    public TransactionAdapter(Context context, List<Transaction> transactions) {
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
        Transaction transaction = transactions.get(position);

        // Bind data to views
        holder.binding.heading.setText(transaction.getHeading());
        holder.binding.desc.setText(transaction.getDescription());
        holder.binding.amount.setText(String.format("₹ %s", transaction.getAmount()));
        holder.binding.status.setText(transaction.getStatus());
        holder.binding.imageCard.setBackgroundResource(transaction.getImageBackground());
        holder.binding.imageCardLogo.setImageResource(transaction.getImageResource());
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
