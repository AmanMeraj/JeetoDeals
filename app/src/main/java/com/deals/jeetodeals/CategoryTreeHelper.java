package com.deals.jeetodeals;

import android.util.Log;

import com.deals.jeetodeals.Model.Category;
import com.deals.jeetodeals.Model.CategoryTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to build and manage category tree from flat list of categories
 */
public class CategoryTreeHelper {
    private static final String TAG = "CategoryTreeHelper";

    /**
     * Builds a tree structure from a flat list of categories
     * @param categories List of all categories
     * @return List of root level categories with their subcategories
     */
    public static List<CategoryTree> buildCategoryTree(List<Category> categories) {
        // First create a map of all categories by ID for easy lookup
        Map<Integer, CategoryTree> categoryMap = new HashMap<>();

        // Convert all Category objects to CategoryTree objects
        for (Category category : categories) {
            categoryMap.put(category.getId(), new CategoryTree(category));
        }

        // Create the tree structure by adding subcategories to their parents
        List<CategoryTree> rootCategories = new ArrayList<>();

        for (Map.Entry<Integer, CategoryTree> entry : categoryMap.entrySet()) {
            CategoryTree categoryTree = entry.getValue();
            int parentId = categoryTree.getParent();

            if (parentId == 0) {
                // This is a root category
                rootCategories.add(categoryTree);
            } else {
                // This is a subcategory, add it to its parent
                CategoryTree parentCategory = categoryMap.get(parentId);
                if (parentCategory != null) {
                    parentCategory.addSubcategory(categoryTree);
                } else {
                    // Parent doesn't exist in our dataset, treat as root
                    Log.w(TAG, "Parent category with ID " + parentId + " not found for " + categoryTree.getName());
                    rootCategories.add(categoryTree);
                }
            }
        }

        return rootCategories;
    }

    /**
     * Creates an "All Categories" object
     */
    public static CategoryTree createAllCategoriesItem() {
        Category allCategory = new Category();
        allCategory.setId(0);
        allCategory.setName(" All ");
        allCategory.setParent(0);
        allCategory.setCount(0);
        return new CategoryTree(allCategory);
    }

    /**
     * Finds a category by ID in the category tree
     * @param rootCategories List of all root categories
     * @param categoryId ID of the category to find
     * @return Found category or null if not found
     */
    public static CategoryTree findCategoryById(List<CategoryTree> rootCategories, int categoryId) {
        for (CategoryTree category : rootCategories) {
            if (category.getId() == categoryId) {
                return category;
            }

            if (category.hasSubcategories()) {
                CategoryTree found = findCategoryInSubcategories(category.getSubcategories(), categoryId);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static CategoryTree findCategoryInSubcategories(List<CategoryTree> subcategories, int categoryId) {
        for (CategoryTree category : subcategories) {
            if (category.getId() == categoryId) {
                return category;
            }

            if (category.hasSubcategories()) {
                CategoryTree found = findCategoryInSubcategories(category.getSubcategories(), categoryId);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    /**
     * Gets the path from root to the specified category
     * @param rootCategories List of all root categories
     * @param categoryId ID of the target category
     * @return List of categories representing the path or empty list if not found
     */
    public static List<CategoryTree> getCategoryPath(List<CategoryTree> rootCategories, int categoryId) {
        List<CategoryTree> path = new ArrayList<>();
        for (CategoryTree rootCategory : rootCategories) {
            if (findPathToCategory(rootCategory, categoryId, path)) {
                return path;
            }
        }
        return path; // Empty if not found
    }

    private static boolean findPathToCategory(CategoryTree currentCategory, int targetId, List<CategoryTree> path) {
        // Add current category to path
        path.add(currentCategory);

        // Check if this is the target
        if (currentCategory.getId() == targetId) {
            return true;
        }

        // Check subcategories
        if (currentCategory.hasSubcategories()) {
            for (CategoryTree subCategory : currentCategory.getSubcategories()) {
                if (findPathToCategory(subCategory, targetId, path)) {
                    return true;
                }
            }
        }

        // If we get here, this category isn't on the path to target
        path.remove(path.size() - 1);
        return false;
    }
}