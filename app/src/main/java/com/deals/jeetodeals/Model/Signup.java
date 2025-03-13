package com.deals.jeetodeals.Model;

import java.io.Serializable;

public class Signup implements Serializable {
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOtp_method() {
        return otp_method;
    }

    public void setOtp_method(String otp_method) {
        this.otp_method = otp_method;
    }

    public String firstname;
    public String lastname;
    public String email;

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String dob;
    public String password;
    public String gender;
    public String phone;
    public String otp_method;
}
