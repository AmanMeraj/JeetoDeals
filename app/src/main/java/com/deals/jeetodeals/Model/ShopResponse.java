package com.deals.jeetodeals.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class ShopResponse implements Serializable {
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

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isOn_sale() {
        return on_sale;
    }

    public void setOn_sale(boolean on_sale) {
        this.on_sale = on_sale;
    }

    public Prices getPrices() {
        return prices;
    }

    public void setPrices(Prices prices) {
        this.prices = prices;
    }

    public String getPrice_html() {
        return price_html;
    }

    public void setPrice_html(String price_html) {
        this.price_html = price_html;
    }

    public String getAverage_rating() {
        return average_rating;
    }

    public void setAverage_rating(String average_rating) {
        this.average_rating = average_rating;
    }

    public int getReview_count() {
        return review_count;
    }

    public void setReview_count(int review_count) {
        this.review_count = review_count;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public boolean isHas_options() {
        return has_options;
    }

    public void setHas_options(boolean has_options) {
        this.has_options = has_options;
    }

    public boolean isIs_purchasable() {
        return is_purchasable;
    }

    public void setIs_purchasable(boolean is_purchasable) {
        this.is_purchasable = is_purchasable;
    }

    public boolean isIs_in_stock() {
        return is_in_stock;
    }

    public void setIs_in_stock(boolean is_in_stock) {
        this.is_in_stock = is_in_stock;
    }

    public boolean isIs_on_backorder() {
        return is_on_backorder;
    }

    public void setIs_on_backorder(boolean is_on_backorder) {
        this.is_on_backorder = is_on_backorder;
    }

    public boolean isSold_individually() {
        return sold_individually;
    }

    public void setSold_individually(boolean sold_individually) {
        this.sold_individually = sold_individually;
    }

    public AddToCart getAdd_to_cart() {
        return add_to_cart;
    }

    public void setAdd_to_cart(AddToCart add_to_cart) {
        this.add_to_cart = add_to_cart;
    }


    public int id;
    public String name;
    public String slug;
    public int parent;
    public String type;
    public String variation;
    public String permalink;
    public String sku;
    public String short_description;
    public String description;
    public boolean on_sale;
    public Prices prices;
    public String price_html;
    public String average_rating;
    public int review_count;
    public ArrayList<Image> images;
    public ArrayList<Category> categories;
//    public ArrayList<Object> tags;
//    public ArrayList<Attribute> attributes;
//    public ArrayList<Object> variations;
    public boolean has_options;
    public boolean is_purchasable;
    public boolean is_in_stock;
    public boolean is_on_backorder;
//    public Object low_stock_remaining;
    public boolean sold_individually;
    public AddToCart add_to_cart;
    public ExtensionsShop extensions;
}
