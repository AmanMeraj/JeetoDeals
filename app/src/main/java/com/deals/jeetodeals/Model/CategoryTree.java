package com.deals.jeetodeals.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class to represent a category in a hierarchical tree structure.
 * Each category can have multiple subcategories.
 */
public class CategoryTree {
    private int id;
    private String name;
    private String slug;
    private String description;
    private int parent;
    private int count;
    private Image image;
    private int reviewCount;
    private String permalink;
    private List<CategoryTree> subcategories;
    private boolean isExpanded; // To track UI expansion state

    public CategoryTree(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.slug = category.getSlug();
        this.description = category.getDescription();
        this.parent = category.getParent();
        this.count = category.getCount();
        this.image = category.getImage();
        this.reviewCount = category.getReview_count();
        this.permalink = category.getPermalink();
        this.subcategories = new ArrayList<>();
        this.isExpanded = false;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public List<CategoryTree> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<CategoryTree> subcategories) {
        this.subcategories = subcategories;
    }

    public void addSubcategory(CategoryTree subcategory) {
        if (this.subcategories == null) {
            this.subcategories = new ArrayList<>();
        }
        this.subcategories.add(subcategory);
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public boolean hasSubcategories() {
        return subcategories != null && !subcategories.isEmpty();
    }

    public int getLevel() {
        // Calculate category level based on parent relationship
        // This can be set during tree building
        return 0; // Default level
    }
}