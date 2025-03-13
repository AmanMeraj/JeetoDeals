package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.Wishlist.WishlistResponse;
import com.deals.jeetodeals.databinding.RowWishlistBinding;
import java.util.ArrayList;
import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private List<WishlistResponse> wishlistList;
    private Context context;
    private OnWishlistItemClickListener listener;

    // Interface for item click actions
    public interface OnWishlistItemClickListener {
        void onDeleteClick(WishlistResponse item, int position);
        void onAddToCartClick(WishlistResponse item, int position);
    }

    // Constructor to initialize context, list, and listener
    public WishlistAdapter(Context context, List<WishlistResponse> wishlistList, OnWishlistItemClickListener listener) {
        this.context = context;
        this.wishlistList = wishlistList; // Use the same reference (don't make a copy)
        this.listener = listener;
    }

    @Override
    public WishlistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowWishlistBinding binding = RowWishlistBinding.inflate(LayoutInflater.from(context), parent, false);
        return new WishlistViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(WishlistViewHolder holder, int position) {
        WishlistResponse currentItem = wishlistList.get(position);

        holder.binding.timeTv.setText(currentItem.getPrice() + " Vouchers");
        holder.binding.desc.setText(currentItem.getProduct_name());

        Glide.with(context)
                .load(currentItem.image_url)
                .placeholder(R.drawable.no_image)
                .error(R.drawable.error_image)
                .into(holder.binding.image);

        // Set click listeners using holder.getBindingAdapterPosition() for reliable position
        holder.binding.delBtn.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION && listener != null) {
                listener.onDeleteClick(wishlistList.get(adapterPosition), adapterPosition);
            }
        });

        holder.binding.addCartRel.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION && listener != null) {
                listener.onAddToCartClick(wishlistList.get(adapterPosition), adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wishlistList.size();
    }

    // We don't need a separate removeItem method since we'll directly modify the shared list
    // This method can be removed or simplified

    // Method to update the entire list
    public void updateList(List<WishlistResponse> newList) {
        this.wishlistList.clear();
        this.wishlistList.addAll(newList);
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class WishlistViewHolder extends RecyclerView.ViewHolder {
        private RowWishlistBinding binding;

        public WishlistViewHolder(RowWishlistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}