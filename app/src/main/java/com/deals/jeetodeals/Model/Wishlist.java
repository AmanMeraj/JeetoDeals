package com.deals.jeetodeals.Model;

import java.io.Serializable;

public class Wishlist implements Serializable {
    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public  int product_id;

}
