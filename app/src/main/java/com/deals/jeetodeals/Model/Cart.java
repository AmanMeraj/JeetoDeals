package com.deals.jeetodeals.Model;

public class Cart {
    private String name;
    private String voucher;
    private String description;
    private int quantity;

    public Cart(String name, String voucher, String description, int quantity) {
        this.name = name;
        this.voucher = voucher;
        this.description = description;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getVoucher() {
        return voucher;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
