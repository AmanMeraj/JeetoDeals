package com.deals.jeetodeals.Model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ItemData implements Serializable {
    public Map<String, DynamicItem> getItems() {
        return items;
    }

    public void setItems(Map<String, DynamicItem> items) {
        this.items = items;
    }

    public Map<String, DynamicItem> items = new HashMap<>();
}
