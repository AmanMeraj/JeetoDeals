package com.deals.jeetodeals.Wishlist;

import java.io.Serializable;

public class WishlistAddResponse implements Serializable {
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean success;
    public String message;
}
