package com.deals.jeetodeals.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BillingAddress implements Serializable {

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setAddress_1(String address_1) {
        this.address_1 = address_1;
    }

    public void setAddress_2(String address_2) {
        this.address_2 = address_2;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setState(String state) {
        this.state = state;
    }

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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @SerializedName("billing_company")
    private String company;

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
