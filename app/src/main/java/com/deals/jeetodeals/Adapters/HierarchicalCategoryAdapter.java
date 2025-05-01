package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deals.jeetodeals.CategoryTreeHelper;
import com.deals.jeetodeals.Model.Category;
import com.deals.jeetodeals.Model.CategoryTree;
import com.deals.jeetodeals.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import de.hdodenhof.circleimageview.CircleImageView;

public class HierarchicalCategoryAdapter extends RecyclerView.Adapter<HierarchicalCategoryAdapter.ViewHolder> {
    private static final String TAG = "HierarchicalCategoryAdapter";

    private List<CategoryTree> currentCategories; // Current visible categories
    private List<CategoryTree> rootCategories;    // Root level categories for reference
    private Context context;
    private OnCategoryClickListener listener;
    private int selectedPosition = -1;  // Changed from 0 to -1 to indicate no initial selection
    private Stack<NavigationState> navigationHistory = new Stack<>(); // Track navigation history
    private CategoryTree allCategoriesItem; // Special "All Categories" item

    // Class to track navigation state
    private static class NavigationState {
        List<CategoryTree> categories;
        int selectedPosition;
        CategoryTree parentCategory; // To know where we came from

        NavigationState(List<CategoryTree> categories, int selectedPosition, CategoryTree parentCategory) {
            this.categories = categories;
            this.selectedPosition = selectedPosition;
            this.parentCategory = parentCategory;
        }
    }

    public HierarchicalCategoryAdapter(List<CategoryTree> rootCategories, Context context, OnCategoryClickListener listener) {
        this.rootCategories = new ArrayList<>(rootCategories);

        // Create "All Categories" item for root level only
        Category allCategory = new Category();
        allCategory.setId(0);
        allCategory.setName(" All ");
        allCategory.setParent(0);
        allCategory.setCount(0);
        this.allCategoriesItem = new CategoryTree(allCategory);

        // Initialize with root categories and "All Categories" at position 0
        this.currentCategories = new ArrayList<>();
        this.currentCategories.add(allCategoriesItem); // Add "All" only at root level
        this.currentCategories.addAll(rootCategories);

        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_row_new, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryTree category = currentCategories.get(position);

        // Set category name
        holder.tvName.setText(category.getName());

        // Set category image
        if (category.getImage() != null && category.getImage().getSrc() != null && !category.getImage().getSrc().isEmpty()) {
            Glide.with(context)
                    .load(category.getImage().getSrc())
                    .placeholder(R.drawable.logo_jd)
                    .error(R.drawable.error_image)
                    .centerCrop()
                    .into(holder.image);
        } else {
            // Set a default image
            holder.image.setImageResource(R.drawable.logo_jd);
        }

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            int clickedPosition = holder.getBindingAdapterPosition();
            CategoryTree clickedCategory = currentCategories.get(clickedPosition);

            // Handle "All" item click at root level
            if (navigationHistory.isEmpty() && clickedPosition == 0 && clickedCategory == allCategoriesItem) {
                // Update selection
                int previousSelected = selectedPosition;
                selectedPosition = clickedPosition;
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);

                // Notify listener to show all products at root level
                listener.onCategoryClick(allCategoriesItem, holder.image, 0, 0);
                return;
            }

            // Update selection
            int previousSelected = selectedPosition;
            selectedPosition = clickedPosition;
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);

            // Check if category has subcategories
            if (clickedCategory.hasSubcategories()) {
                // Save current state
                navigationHistory.push(new NavigationState(
                        new ArrayList<>(currentCategories),
                        previousSelected,
                        clickedCategory
                ));

                // Switch to subcategories - without adding "All" at position 0
                currentCategories.clear();
                currentCategories.addAll(clickedCategory.getSubcategories());

                // Reset selection to -1 (no initial selection in subcategories)
                selectedPosition = -1;

                notifyDataSetChanged();

                // Notify listener of category change
                listener.onCategoryClick(clickedCategory, holder.image, clickedCategory.getId(), navigationHistory.size());
            } else {
                // No subcategories, just notify listener for filtering
                listener.onCategoryClick(clickedCategory, holder.image, clickedCategory.getId(), navigationHistory.size());
            }
        });
    }

    // Navigate back to the previous category level
    public boolean navigateBack() {
        if (navigationHistory.isEmpty()) {
            return false; // Already at root level
        }

        NavigationState previousState = navigationHistory.pop();
        currentCategories.clear();
        currentCategories.addAll(previousState.categories);
        selectedPosition = previousState.selectedPosition;

        notifyDataSetChanged();

        // Notify listener to update filtered products
        if (!navigationHistory.isEmpty()) {
            // If we still have history, we're at some subcategory level
            NavigationState currentLevel = navigationHistory.peek();
            listener.onCategoryClick(currentLevel.parentCategory, null, currentLevel.parentCategory.getId(), navigationHistory.size());
        } else {
            // Back at root level, use "All Categories"
            listener.onCategoryClick(allCategoriesItem, null, 0, 0);
        }

        return true;
    }

    @Override
    public int getItemCount() {
        return currentCategories.size();
    }

    public void updateData(List<CategoryTree> newRootCategories) {
        this.rootCategories = new ArrayList<>(newRootCategories);

        // Reset navigation state
        navigationHistory.clear();

        // Reset current categories to root level with "All" at position 0
        currentCategories.clear();
        currentCategories.add(allCategoriesItem); // Add "All" only at root level
        currentCategories.addAll(rootCategories);

        selectedPosition = -1; // Changed from 0 to -1 to indicate no initial selection
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public CategoryTree getSelectedItem() {
        if (selectedPosition >= 0 && selectedPosition < currentCategories.size()) {
            return currentCategories.get(selectedPosition);
        }
        return null;
    }

    // Check if we're currently at root level
    public boolean isAtRootLevel() {
        return navigationHistory.isEmpty();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        CircleImageView image;
        RelativeLayout relBg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCategoryName);
            image = itemView.findViewById(R.id.imgCategoryIcon);
            relBg = itemView.findViewById(R.id.rel_bg);
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(CategoryTree category, ImageView imageView, int categoryId, int navigationLevel);
    }
}