package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.deals.jeetodeals.Model.Wishlist;
import com.deals.jeetodeals.databinding.RowWishlistBinding;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private List<Wishlist> wishlistList;
    private Context context;

    // Constructor to initialize the context and the list of Wishlist
    public WishlistAdapter(Context context, List<Wishlist> wishlistList) {
        this.context = context;
        this.wishlistList = wishlistList;
    }

    @Override
    public WishlistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout using data binding
        RowWishlistBinding binding = RowWishlistBinding.inflate(LayoutInflater.from(context), parent, false);
        return new WishlistViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(WishlistViewHolder holder, int position) {
        // Get the current wishlist item and set the binding
        Wishlist currentItem = wishlistList.get(position);
        holder.binding.vouchersTv.setText(currentItem.getVouchers());
        holder.binding.desc.setText(currentItem.getDescription()); // Binding the Wishlist model
    }

    @Override
    public int getItemCount() {
        return wishlistList.size();
    }

    // ViewHolder that holds the binding
    public static class WishlistViewHolder extends RecyclerView.ViewHolder {
        private RowWishlistBinding binding;

        public WishlistViewHolder(RowWishlistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
