package com.deals.jeetodeals;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.deals.jeetodeals.Adapters.HierarchicalCategoryAdapter;
import com.deals.jeetodeals.Fragments.FragmentsViewModel;
import com.deals.jeetodeals.Fragments.HomeFragment.HomeViewModel;
import com.deals.jeetodeals.Model.Category;
import com.deals.jeetodeals.Model.CategoryTree;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.Wishlist.WishlistViewModel;
import com.deals.jeetodeals.databinding.ActivityCategoryBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityCategory extends AppCompatActivity implements HierarchicalCategoryAdapter.OnCategoryClickListener {
    private static final String TAG = "ActivityCategory";
    public static final String EXTRA_CATEGORY_ID = "category_id";
    public static final String EXTRA_CATEGORY_NAME = "category_name";
    public static final int RESULT_CATEGORY_SELECTED = 101;

    private ActivityCategoryBinding binding;
    private List<CategoryTree> rootCategories;
    private HierarchicalCategoryAdapter categoryAdapter;
    private HomeViewModel viewModel;
    private FragmentsViewModel viewModel2;
    private WishlistViewModel wishlistViewModel;
    private Utility utility = new Utility();
    private View selectedRelativeLayout;
    private boolean finalSelectionMade = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        viewModel2 = new ViewModelProvider(this).get(FragmentsViewModel.class);
        wishlistViewModel = new ViewModelProvider(this).get(WishlistViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up back button
        binding.backBtn.setOnClickListener(v -> onBackPressed());

        // Initialize category list
        binding.rcCategory.setLayoutManager(new LinearLayoutManager(this));

        if (utility.isInternetConnected(this)) {
            getCategory();
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCategory() {
        // Show loader while fetching categories
        binding.loader.rlLoader.setVisibility(View.VISIBLE);

        viewModel2.getCategory().observe(this, response -> {
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response != null && response.isSuccess && response.data != null && !response.data.isEmpty()) {
                // Filter out unwanted categories
                List<Category> filteredCategories = new ArrayList<>();
                for (Category category : response.data) {
                    String categoryName = category.getName();
                    // Debug log to check category IDs being received
                    Log.d(TAG, "Category from API: " + categoryName + ", ID: " + category.getId());

                    if (!categoryName.equalsIgnoreCase("Ongoing Promotion") &&
                            !categoryName.equalsIgnoreCase("Promotion") &&
                            !categoryName.equalsIgnoreCase("Uncategorized") &&
                            !categoryName.equalsIgnoreCase("Future Campaign")) {
                        filteredCategories.add(category);
                    }
                }

                if (!filteredCategories.isEmpty()) {
                    // Build the category tree
                    rootCategories = CategoryTreeHelper.buildCategoryTree(filteredCategories);
                    setupCategoryAdapter();
                } else {
                    handleError("No valid categories found.");
                }
            } else {
                handleError(response != null ? response.message : "Unknown error");
            }
        });
    }

    // Set up the category adapter
    private void setupCategoryAdapter() {
        // Create adapter with the root categories
        categoryAdapter = new HierarchicalCategoryAdapter(rootCategories, this, this);
        binding.rcCategory.setAdapter(categoryAdapter);
    }

    // Handle category click
    @Override
    public void onCategoryClick(CategoryTree category, ImageView imageView, int categoryId, int navigationLevel) {
        // Add debug logging
        Log.d(TAG, "onCategoryClick - Category: " + (category != null ? category.getName() : "null") +
                ", ID: " + categoryId + ", Level: " + navigationLevel);

        // Safety check - don't proceed if category is null
        if (category == null) {
            Log.e(TAG, "Category is null in onCategoryClick");
            return;
        }

        // If categoryId is -1, try to get it directly from the category object
        if (categoryId == -1) {
            categoryId = category.getId();
            Log.d(TAG, "Retrieved category ID from object: " + categoryId);
        }

        // Highlight the selected category visually if needed
        if (imageView != null) {
            animateCategorySelection(imageView);
        }

        RelativeLayout relativeLayout = null;
        if (imageView != null && imageView.getParent() instanceof RelativeLayout) {
            relativeLayout = (RelativeLayout) imageView.getParent();
            updateCategorySelection(relativeLayout);
        }

        // Only return to the ShopFragment if we're at a leaf category or user explicitly selects a parent category
        if (!category.hasSubcategories() || (relativeLayout != null)) {
            finalSelectionMade = true;
            returnCategoryResult(category, categoryId);
        }
    }

    private void returnCategoryResult(CategoryTree category, int categoryId) {
        // Add debug logging
        Log.d(TAG, "returnCategoryResult - Category: " + category.getName() + ", ID: " + categoryId);

        // Make sure we're not returning -1 as the category ID
        if (categoryId < 0) {
            Log.w(TAG, "Invalid category ID: " + categoryId + ", using 0 instead");
            categoryId = 0; // Use 0 as a fallback
        }

        // Return the selected category to ShopFragment
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_CATEGORY_ID, categoryId);
        resultIntent.putExtra(EXTRA_CATEGORY_NAME, category.getName());
        setResult(RESULT_CATEGORY_SELECTED, resultIntent);
        finish();
    }

    private void updateCategorySelection(RelativeLayout newSelection) {
        if (selectedRelativeLayout != null && selectedRelativeLayout != newSelection) {
            selectedRelativeLayout.setBackgroundResource(R.drawable.category_bg);
        }
        newSelection.setBackgroundResource(R.drawable.selected_category_bg);
        selectedRelativeLayout = newSelection;
    }

    private void animateCategorySelection(ImageView imageView) {
        // You can implement animation if needed
    }

    @Override
    public void onBackPressed() {
        // Check if the category adapter can handle navigating back
        if (categoryAdapter != null && categoryAdapter.navigateBack()) {
            return; // Event consumed
        }
        super.onBackPressed(); // Not consumed, let the activity handle it
    }

    private void handleError(String message) {
        Log.e(TAG, "Error: " + message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}