package com.deals.jeetodeals.Model;

import java.io.Serializable;

public class GetCheckout implements Serializable {
    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrder_key() {
        return order_key;
    }

    public void setOrder_key(String order_key) {
        this.order_key = order_key;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getCustomer_note() {
        return customer_note;
    }

    public void setCustomer_note(String customer_note) {
        this.customer_note = customer_note;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }


    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public PaymentResult getPayment_result() {
        return payment_result;
    }

    public void setPayment_result(PaymentResult payment_result) {
        this.payment_result = payment_result;
    }

    public Extensions getExtensions() {
        return extensions;
    }

    public void setExtensions(Extensions extensions) {
        this.extensions = extensions;
    }

    public int order_id;
    public String status;
    public String order_key;
    public String order_number;
    public String customer_note;
    public int customer_id;

    public BillingAddressCheckout getBilling_address() {
        return billing_address;
    }

    public void setBilling_address(BillingAddressCheckout billing_address) {
        this.billing_address = billing_address;
    }

    public ShippingAddressCheckout getShipping_address() {
        return shipping_address;
    }

    public void setShipping_address(ShippingAddressCheckout shipping_address) {
        this.shipping_address = shipping_address;
    }

    public BillingAddressCheckout billing_address;
    public ShippingAddressCheckout shipping_address;
    public String payment_method;
    public PaymentResult payment_result;
    public Extensions extensions;
    public RazorpayOrder getRazorpay_order() {
        return razorpay_order;
    }

    public void setRazorpay_order(RazorpayOrder razorpay_order) {
        this.razorpay_order = razorpay_order;
    }

    public RazorpayOrder razorpay_order;
}
