package com.deals.jeetodeals.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Wishlist implements Serializable {
    @SerializedName("product_id")
    private int product_id;

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

}
