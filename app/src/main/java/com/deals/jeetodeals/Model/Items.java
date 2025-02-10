package com.deals.jeetodeals.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class Items implements Serializable {
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public QuantityLimits getQuantity_limits() {
        return quantity_limits;
    }

    public void setQuantity_limits(QuantityLimits quantity_limits) {
        this.quantity_limits = quantity_limits;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Object getLow_stock_remaining() {
        return low_stock_remaining;
    }

    public void setLow_stock_remaining(Object low_stock_remaining) {
        this.low_stock_remaining = low_stock_remaining;
    }

    public boolean isBackorders_allowed() {
        return backorders_allowed;
    }

    public void setBackorders_allowed(boolean backorders_allowed) {
        this.backorders_allowed = backorders_allowed;
    }

    public boolean isShow_backorder_badge() {
        return show_backorder_badge;
    }

    public void setShow_backorder_badge(boolean show_backorder_badge) {
        this.show_backorder_badge = show_backorder_badge;
    }

    public boolean isSold_individually() {
        return sold_individually;
    }

    public void setSold_individually(boolean sold_individually) {
        this.sold_individually = sold_individually;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

    public ArrayList<Object> getVariation() {
        return variation;
    }

    public void setVariation(ArrayList<Object> variation) {
        this.variation = variation;
    }

    public ArrayList<Object> getItem_data() {
        return item_data;
    }

    public void setItem_data(ArrayList<Object> item_data) {
        this.item_data = item_data;
    }

    public Prices getPrices() {
        return prices;
    }

    public void setPrices(Prices prices) {
        this.prices = prices;
    }

    public Total getTotals() {
        return totals;
    }

    public void setTotals(Total totals) {
        this.totals = totals;
    }

    public String getCatalog_visibility() {
        return catalog_visibility;
    }

    public void setCatalog_visibility(String catalog_visibility) {
        this.catalog_visibility = catalog_visibility;
    }

    public Extensions getExtensions() {
        return extensions;
    }

    public void setExtensions(Extensions extensions) {
        this.extensions = extensions;
    }

    public String key;
    public int id;
    public String type;
    public int quantity;
    public QuantityLimits quantity_limits;
    public String name;
    public String short_description;
    public String description;
    public String sku;
    public Object low_stock_remaining;
    public boolean backorders_allowed;
    public boolean show_backorder_badge;
    public boolean sold_individually;
    public String permalink;
    public ArrayList<Image> images;
    public ArrayList<Object> variation;
    public ArrayList<Object> item_data;
    public Prices prices;
    public Total totals;
    public String catalog_visibility;
    public Extensions extensions;
}
