package com.deals.jeetodeals.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class PaymentResult implements Serializable {

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public ArrayList<Object> getPayment_details() {
        return payment_details;
    }

    public void setPayment_details(ArrayList<Object> payment_details) {
        this.payment_details = payment_details;
    }

    public String getRedirect_url() {
        return redirect_url;
    }

    public void setRedirect_url(String redirect_url) {
        this.redirect_url = redirect_url;
    }

    public String payment_status;
    public ArrayList<Object> payment_details;
    public String redirect_url;
}
