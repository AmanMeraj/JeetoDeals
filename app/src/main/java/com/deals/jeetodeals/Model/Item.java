package com.deals.jeetodeals.Model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Item implements Serializable {
    public String key;
    public int id;
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
    public ArrayList<Variations2> variation;

    @SerializedName("item_data")
    public Map<String, ItemDataValue> item_data;

    public Prices prices;
    public Totals totals;
    public String catalog_visibility;

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

    public ArrayList<Variations2> getVariation() {
        return variation;
    }

    public void setVariation(ArrayList<Variations2> variation) {
        this.variation = variation;
    }

    public Prices getPrices() {
        return prices;
    }

    public void setPrices(Prices prices) {
        this.prices = prices;
    }

    public Totals getTotals() {
        return totals;
    }

    public void setTotals(Totals totals) {
        this.totals = totals;
    }

    public String getCatalog_visibility() {
        return catalog_visibility;
    }

    public void setCatalog_visibility(String catalog_visibility) {
        this.catalog_visibility = catalog_visibility;
    }

    public Map<String, ItemDataValue> getItem_data() {
        return item_data;
    }

    public void setItem_data(Map<String, ItemDataValue> item_data) {
        this.item_data = item_data;
    }

    // Helper method to check if this is a variable product
    public boolean isVariableProduct() {
        return variation != null &&
                !variation.isEmpty() &&
                item_data != null &&
                !item_data.isEmpty();
    }

    // Helper method to get variation value for a specific attribute
    public String getVariationValueByAttribute(String attributeName) {
        if (variation != null) {
            for (Variations2 var : variation) {
                if (var.attribute != null && var.attribute.equals(attributeName)) {
                    if (var.value instanceof String) {
                        return (String) var.value;
                    } else if (var.value instanceof Map) {
                        // If it's a Map but not empty, try to extract a value
                        Map<?, ?> valueMap = (Map<?, ?>) var.value;
                        if (!valueMap.isEmpty()) {
                            // Return the first entry's value or key
                            return valueMap.values().iterator().next().toString();
                        } else {
                            // Empty map, return empty string
                            return "";
                        }
                    } else if (var.value != null) {
                        // For any other non-null type, convert to string
                        return var.value.toString();
                    }
                }
            }
        }
        return "";
    }

    // ItemDataValue class to handle the item_data structure
    public static class ItemDataValue implements Serializable {
        public String key;
        public String value;
        public String display_key;
        public String display_value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getDisplay_key() {
            return display_key;
        }

        public void setDisplay_key(String display_key) {
            this.display_key = display_key;
        }

        public String getDisplay_value() {
            return display_value;
        }

        public void setDisplay_value(String display_value) {
            this.display_value = display_value;
        }
    }
}