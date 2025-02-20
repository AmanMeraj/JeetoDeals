package com.deals.jeetodeals.Model;

public class RazorpayOrder {
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount_due() {
        return amount_due;
    }

    public void setAmount_due(int amount_due) {
        this.amount_due = amount_due;
    }

    public int getAmount_paid() {
        return amount_paid;
    }

    public void setAmount_paid(int amount_paid) {
        this.amount_paid = amount_paid;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public int getCreated_at() {
        return created_at;
    }

    public void setCreated_at(int created_at) {
        this.created_at = created_at;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Notes getNotes() {
        return notes;
    }

    public void setNotes(Notes notes) {
        this.notes = notes;
    }

    public Object getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(Object offer_id) {
        this.offer_id = offer_id;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int amount;
    public int amount_due;
    public int amount_paid;
    public int attempts;
    public int created_at;
    public String currency;
    public String entity;
    public String id;
    public Notes notes;
    public Object offer_id;
    public String receipt;
    public String status;
    public class Notes{
        public String getWoocommerce_order_number() {
            return woocommerce_order_number;
        }

        public void setWoocommerce_order_number(String woocommerce_order_number) {
            this.woocommerce_order_number = woocommerce_order_number;
        }

        public String woocommerce_order_number;
    }
}
