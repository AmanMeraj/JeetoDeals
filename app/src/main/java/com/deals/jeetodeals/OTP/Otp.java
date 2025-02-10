package com.deals.jeetodeals.OTP;

import java.io.Serializable;

public class Otp implements Serializable {
    public void setEmail(String email) {
        this.email = email;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String email;
    public String otp;

}
