package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deals.jeetodeals.Model.Category;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.databinding.RowCategoryBinding;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<Category> categories;
    private final OnCategoryClickListener onCategoryClickListener;
    private Context context;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category, ImageView imageView,int categoryId);
    }

    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener,Context context) {
        this.categories = categories;
        this.onCategoryClickListener = listener;
        this.context=context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowCategoryBinding binding = RowCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);

        holder.binding.text.setText(category.getName());
        Glide.with(context).load(category.getImage()).placeholder(R.drawable.accessories).into(holder.binding.image);

        // Handle click to trigger animation
        holder.binding.getRoot().setOnClickListener(v ->
                onCategoryClickListener.onCategoryClick(category, holder.binding.image,category.getId())
        );
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final RowCategoryBinding binding;

        public CategoryViewHolder(RowCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
