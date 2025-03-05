package com.deals.jeetodeals.Model;

import java.io.Serializable;

public class AppVersion implements Serializable {
    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public boolean isForce_update() {
        return force_update;
    }

    public void setForce_update(boolean force_update) {
        this.force_update = force_update;
    }

    public String getCalling() {
        return calling;
    }

    public void setCalling(String calling) {
        this.calling = calling;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String app_version;
    public boolean force_update;
    public String calling;
    public String email;
    public String whatsapp;

    public int getVoucher_rate() {
        return voucher_rate;
    }

    public void setVoucher_rate(int voucher_rate) {
        this.voucher_rate = voucher_rate;
    }

    public int voucher_rate;

    public String getPayment_key() {
        return payment_key;
    }

    public void setPayment_key(String payment_key) {
        this.payment_key = payment_key;
    }

    public String getIos_version() {
        return ios_version;
    }

    public void setIos_version(String ios_version) {
        this.ios_version = ios_version;
    }

    public String getAndriod_version() {
        return andriod_version;
    }

    public void setAndriod_version(String andriod_version) {
        this.andriod_version = andriod_version;
    }

    public String payment_key;
    public String ios_version;
    public String andriod_version;
}
