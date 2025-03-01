package com.deals.jeetodeals.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deals.jeetodeals.R;

import java.util.List;

public class SizeSelectorAdapter extends RecyclerView.Adapter<SizeSelectorAdapter.SizeViewHolder> {

    public interface OnSizeSelectedListener {
        void onSizeSelected(String size);
    }

    private final List<String> sizeList;
    private final OnSizeSelectedListener listener;
    private int selectedPosition = -1;

    public SizeSelectorAdapter(List<String> sizeList, OnSizeSelectedListener listener) {
        this.sizeList = sizeList;
        this.listener = listener;
    }

    public static class SizeViewHolder extends RecyclerView.ViewHolder {
        TextView sizeTextView;
        View sizeItemContainer;

        public SizeViewHolder(@NonNull View itemView) {
            super(itemView);
            sizeTextView = itemView.findViewById(R.id.size_selector);
            sizeItemContainer = itemView.findViewById(R.id.rel_size);
        }
    }

    @NonNull
    @Override
    public SizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_size_selector, parent, false);
        return new SizeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SizeViewHolder holder, int position) {
        String size = sizeList.get(position);
        holder.sizeTextView.setText(size);

        // Highlight selected size
        if (position == selectedPosition) {
            holder.sizeItemContainer.setBackgroundResource(R.drawable.orange_oval);
            holder.sizeTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
        } else {
            holder.sizeItemContainer.setBackgroundResource(R.drawable.green_elipse);
            holder.sizeTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.black));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int previousSelected = selectedPosition;
                selectedPosition = holder.getAdapterPosition();

                // Update the previously selected and newly selected items
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);

                // Notify listener
                listener.onSizeSelected(size);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sizeList != null ? sizeList.size() : 0;
    }
}