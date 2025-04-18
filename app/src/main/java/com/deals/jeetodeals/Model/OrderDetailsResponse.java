package com.deals.jeetodeals.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OrderDetailsResponse {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public ArrayList<Object> getCoupons() {
        return coupons;
    }

    public void setCoupons(ArrayList<Object> coupons) {
        this.coupons = coupons;
    }

    public ArrayList<Object> getFees() {
        return fees;
    }

    public void setFees(ArrayList<Object> fees) {
        this.fees = fees;
    }

    public Totals getTotals() {
        return totals;
    }

    public void setTotals(Totals totals) {
        this.totals = totals;
    }


    public boolean isNeeds_payment() {
        return needs_payment;
    }

    public void setNeeds_payment(boolean needs_payment) {
        this.needs_payment = needs_payment;
    }

    public boolean isNeeds_shipping() {
        return needs_shipping;
    }

    public void setNeeds_shipping(boolean needs_shipping) {
        this.needs_shipping = needs_shipping;
    }

    public ArrayList<String> getPayment_requirements() {
        return payment_requirements;
    }

    public void setPayment_requirements(ArrayList<String> payment_requirements) {
        this.payment_requirements = payment_requirements;
    }

    public ArrayList<Object> getErrors() {
        return errors;
    }

    public void setErrors(ArrayList<Object> errors) {
        this.errors = errors;
    }

    public int id;
    public String status;
    public ArrayList<Item> items;
    public ArrayList<Object> coupons;
    public ArrayList<Object> fees;
    public Totals totals;
    @SerializedName("shipping_address")
    private Address shipping_address;

    public Address getBilling_address() {
        return billing_address;
    }

    public void setBilling_address(Address billing_address) {
        this.billing_address = billing_address;
    }

    public Address getShipping_address() {
        return shipping_address;
    }

    public void setShipping_address(Address shipping_address) {
        this.shipping_address = shipping_address;
    }

    @SerializedName("billing_address")
    private Address billing_address;

    public boolean needs_payment;
    public boolean needs_shipping;
    public ArrayList<String> payment_requirements;
    public ArrayList<Object> errors;

}
