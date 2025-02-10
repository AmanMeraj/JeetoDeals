package com.deals.jeetodeals.Model;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private String product_name;
    private int quantity;

    // Getters and Setters
    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
