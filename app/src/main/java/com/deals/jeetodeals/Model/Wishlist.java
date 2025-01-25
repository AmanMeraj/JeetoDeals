package com.deals.jeetodeals.Model;

public class Wishlist {
    private String status;
    private String description;
    private String vouchers;
    private String addToCartButtonText;
    private int imageResource;
    private int bagIconResource;

    // Constructor, getters and setters
    public Wishlist(String status, String description, String vouchers, String addToCartButtonText, int imageResource, int bagIconResource) {
        this.status = status;
        this.description = description;
        this.vouchers = vouchers;
        this.addToCartButtonText = addToCartButtonText;
        this.imageResource = imageResource;
        this.bagIconResource = bagIconResource;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVouchers() {
        return vouchers;
    }

    public void setVouchers(String vouchers) {
        this.vouchers = vouchers;
    }

    public String getAddToCartButtonText() {
        return addToCartButtonText;
    }

    public void setAddToCartButtonText(String addToCartButtonText) {
        this.addToCartButtonText = addToCartButtonText;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public int getBagIconResource() {
        return bagIconResource;
    }

    public void setBagIconResource(int bagIconResource) {
        this.bagIconResource = bagIconResource;
    }
}
