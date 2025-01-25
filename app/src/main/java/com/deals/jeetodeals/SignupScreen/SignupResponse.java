package com.deals.jeetodeals.SignupScreen;

import java.io.Serializable;

public class SignupResponse implements Serializable {

    private String status;
    private String message;

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
