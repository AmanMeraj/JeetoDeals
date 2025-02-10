package com.deals.jeetodeals.Model;

import java.io.Serializable;

public class UpdateAddressResponse implements Serializable {
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String message;
}
