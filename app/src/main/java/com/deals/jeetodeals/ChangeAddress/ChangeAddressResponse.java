package com.deals.jeetodeals.ChangeAddress;

import com.deals.jeetodeals.Model.BillingAddress;
import com.deals.jeetodeals.Model.ShippingAddress;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChangeAddressResponse implements Serializable {
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String message;
    @SerializedName("billing_address")
    private BillingAddress billingAddress;

    @SerializedName("shipping_address")
    private ShippingAddress shippingAddress;

    public BillingAddress getBillingAddress() {
        return billingAddress;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }
}
