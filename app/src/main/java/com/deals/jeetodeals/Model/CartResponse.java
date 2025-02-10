package com.deals.jeetodeals.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class CartResponse implements Serializable {

    public ArrayList<Items> getItems() {
        return items;
    }

    public void setItems(ArrayList<Items> items) {
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

    public Total getTotals() {
        return totals;
    }

    public void setTotals(Total totals) {
        this.totals = totals;
    }

    public ShippingAddress getShipping_address() {
        return shipping_address;
    }

    public void setShipping_address(ShippingAddress shipping_address) {
        this.shipping_address = shipping_address;
    }

    public BillingAddress getBilling_address() {
        return billing_address;
    }

    public void setBilling_address(BillingAddress billing_address) {
        this.billing_address = billing_address;
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

    public boolean isHas_calculated_shipping() {
        return has_calculated_shipping;
    }

    public void setHas_calculated_shipping(boolean has_calculated_shipping) {
        this.has_calculated_shipping = has_calculated_shipping;
    }

    public ArrayList<ShippingRate> getShipping_rates() {
        return shipping_rates;
    }

    public void setShipping_rates(ArrayList<ShippingRate> shipping_rates) {
        this.shipping_rates = shipping_rates;
    }

    public int getItems_count() {
        return items_count;
    }

    public void setItems_count(int items_count) {
        this.items_count = items_count;
    }


    public ArrayList<Object> getCross_sells() {
        return cross_sells;
    }

    public void setCross_sells(ArrayList<Object> cross_sells) {
        this.cross_sells = cross_sells;
    }

    public ArrayList<Object> getErrors() {
        return errors;
    }

    public void setErrors(ArrayList<Object> errors) {
        this.errors = errors;
    }

    public ArrayList<String> getPayment_methods() {
        return payment_methods;
    }

    public void setPayment_methods(ArrayList<String> payment_methods) {
        this.payment_methods = payment_methods;
    }

    public Extensions getExtensions() {
        return extensions;
    }

    public void setExtensions(Extensions extensions) {
        this.extensions = extensions;
    }

    public ArrayList<Items> items;
    public ArrayList<Object> coupons;
    public ArrayList<Object> fees;
    public Total totals;
    public ShippingAddress shipping_address;
    public BillingAddress billing_address;
    public boolean needs_payment;
    public boolean needs_shipping;
    public ArrayList<String> payment_requirements;
    public boolean has_calculated_shipping;
    public ArrayList<ShippingRate> shipping_rates;
    public int items_count;

    public double getItems_weight() {
        return items_weight;
    }

    public void setItems_weight(double items_weight) {
        this.items_weight = items_weight;
    }

    public double items_weight;
    public ArrayList<Object> cross_sells;
    public ArrayList<Object> errors;
    public ArrayList<String> payment_methods;
    public Extensions extensions;
}
