package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.deals.jeetodeals.Model.TicketGroupedByProduct;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.Utils.SharedPref;
import com.deals.jeetodeals.databinding.RowPastTicketBinding;
import java.util.List;

public class AdapterPastTickets extends RecyclerView.Adapter<AdapterPastTickets.ViewHolder> {
    private final Context context;
    private List<TicketGroupedByProduct> tickets;
    private static SharedPref pref= new SharedPref();
    private OnPastTicketClickListener listener;

    public interface OnPastTicketClickListener {
        void onPastTicketClick(TicketGroupedByProduct ticket);
    }

    public AdapterPastTickets(Context context, List<TicketGroupedByProduct> tickets) {
        this.context = context;
        this.tickets = tickets;
    }

    public void setOnPastTicketClickListener(OnPastTicketClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowPastTicketBinding binding = RowPastTicketBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding, context, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TicketGroupedByProduct ticket = tickets.get(position);
        holder.bind(ticket);
    }

    @Override
    public int getItemCount() {
        return tickets != null ? tickets.size() : 0;
    }

    public void updateTickets(List<TicketGroupedByProduct> newTickets) {
        this.tickets = newTickets;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RowPastTicketBinding binding;
        private final Context context;
        private final OnPastTicketClickListener listener;

        ViewHolder(RowPastTicketBinding binding, Context context, OnPastTicketClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = context;
            this.listener = listener;
        }

        void bind(TicketGroupedByProduct ticket) {
            binding.textPhoneName.setText(ticket.getProductName());

            Glide.with(context)
                    .load(ticket.getProductImage())
                    .placeholder(R.drawable.promotion_image)
                    .error(R.drawable.promotion_image)
                    .into(binding.imagePhone);

            // Set up RecyclerView for ticket numbers
            TickectChildAdapter ticketNumberAdapter = new TickectChildAdapter(context, ticket.getTicketNumbers(),pref.getPrefString(context,pref.user_name),ticket.getProductName());
            binding.ticketRow.setLayoutManager(new LinearLayoutManager(context));
            binding.ticketRow.setAdapter(ticketNumberAdapter);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPastTicketClick(ticket);
                }
            });
        }
    }
}
