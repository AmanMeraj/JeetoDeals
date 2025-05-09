package com.deals.jeetodeals.Model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ShippingAddress implements Serializable {

    @SerializedName("shipping_first_name")
    public String first_name;

    @SerializedName("shipping_last_name")
    public String last_name;

    @SerializedName("shipping_company")
    public String company;

    @SerializedName("shipping_address_1")
    public String address_1;

    @SerializedName("shipping_address_2")
    public String address_2;

    @SerializedName("shipping_city")
    public String city;

    @SerializedName("shipping_state")
    public String state;

    @SerializedName("shipping_postcode")
    public String postcode;

    @SerializedName("shipping_country")
    public String country;

    @SerializedName("shipping_phone")
    public String phone;

    // Getters and Setters
    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress_1() {
        return address_1;
    }

    public void setAddress_1(String address_1) {
        this.address_1 = address_1;
    }

    public String getAddress_2() {
        return address_2;
    }

    public void setAddress_2(String address_2) {
        this.address_2 = address_2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
