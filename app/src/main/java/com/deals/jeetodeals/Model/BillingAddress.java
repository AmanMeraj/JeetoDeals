package com.deals.jeetodeals.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BillingAddress implements Serializable {

    @SerializedName("billing_first_name")
    private String first_name;

    @SerializedName("billing_last_name")
    private String last_name;

    @SerializedName("billing_address_1")
    private String address_1;

    @SerializedName("billing_address_2")
    private String address_2;

    @SerializedName("billing_city")
    private String city;

    @SerializedName("billing_postcode")
    private String postcode;

    @SerializedName("billing_country")
    private String country;

    @SerializedName("billing_state")
    private String state;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @SerializedName("billing_phone")
    private String phone;
    @SerializedName("billing_email")
    private String email;

    // Getters
    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getAddress_1() {
        return address_1;
    }

    public String getAddress_2() {
        return address_2;
    }

    public String getCity() {
        return city;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }
}
