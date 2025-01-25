package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deals.jeetodeals.Model.Ticket;
import com.deals.jeetodeals.databinding.RowActiveTicketBinding;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {
    private final Context context;
    private final List<Ticket> tickets;

    public TicketAdapter(Context context, List<Ticket> tickets) {
        this.context = context;
        this.tickets = tickets;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowActiveTicketBinding binding = RowActiveTicketBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new TicketViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = tickets.get(position);

        // Bind data to views
        holder.binding.textPhoneName.setText(ticket.getPhoneName());
        holder.binding.textTicketId.setText(ticket.getTicketId());
        holder.binding.imagePhone.setImageResource(ticket.getImageResource());
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        private final RowActiveTicketBinding binding;

        public TicketViewHolder(RowActiveTicketBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
