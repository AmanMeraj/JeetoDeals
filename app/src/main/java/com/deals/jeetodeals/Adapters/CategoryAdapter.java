package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.deals.jeetodeals.Model.Category;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.databinding.RowCategoryBinding;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<Category> categories;
    private final OnCategoryClickListener onCategoryClickListener;
    private final Context context;

    // Track the selected position
    private int selectedPosition = 0; // Default to first item selected

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category, ImageView imageView, int categoryId);
    }

    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener, Context context) {
        this.categories = categories;
        this.onCategoryClickListener = listener;
        this.context = context;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowCategoryBinding binding = RowCategoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);

        // Set the text
        holder.binding.tvCategory.setText(category.getName());

        // IMPORTANT: Set the background based on selection state
        if (selectedPosition == position) {
            holder.binding.relBg.setBackgroundResource(R.drawable.selected_category_bg);
        } else {
            holder.binding.relBg.setBackgroundResource(R.drawable.category_bg);
        }

        // Load image
        if (category.getImage() != null && category.getImage().getSrc() != null) {
            Glide.with(context)
                    .load(category.getImage().getSrc())
                    .placeholder(R.drawable.logo_jd)
                    .error(R.drawable.logo_jd)
                    .into(holder.binding.imgCategory);
        } else {
            Glide.with(context)
                    .load(R.drawable.logo_jd)
                    .into(holder.binding.imgCategory);
        }

        // Set click listener with final position
        final int currentPos = position;
        holder.binding.getRoot().setOnClickListener(v -> {
            // Only proceed if this is a different position
            if (selectedPosition != currentPos) {
                // Store old position
                int oldPosition = selectedPosition;
                // Update to new position
                selectedPosition = currentPos;

                // Refresh the previous and new selections
                notifyItemChanged(oldPosition);
                notifyItemChanged(selectedPosition);

                // Call listener
                onCategoryClickListener.onCategoryClick(
                        category,
                        holder.binding.imgCategory,
                        category.getId()
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return categories.get(position).getId();
    }

    // Public method to set selection programmatically
    public void setSelectedPosition(int position) {
        if (position >= 0 && position < categories.size() && position != selectedPosition) {
            int oldPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(oldPosition);
            notifyItemChanged(position);
        }
    }

    // Get the currently selected position
    public int getSelectedPosition() {
        return selectedPosition;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final RowCategoryBinding binding;

        public CategoryViewHolder(RowCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}