package com.deals.jeetodeals.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddItems implements Serializable {
    private int id;
    private int quantity;
    private List<Map<String, String>> variation;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private  String key;

    public AddItems() {
        this.variation = new ArrayList<>();
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

    public List<Map<String, String>> getVariation() {
        return variation;
    }

    public void setVariation(List<Map<String, String>> variation) {
        this.variation = variation;
    }
}
