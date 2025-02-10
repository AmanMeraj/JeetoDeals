package com.deals.jeetodeals.SignInScreen;

import java.io.Serializable;

public class ForgotPassResponse implements Serializable {


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String message;

}
