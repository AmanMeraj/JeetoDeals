package com.deals.jeetodeals.Model;

import java.io.Serializable;

public class ChangePasswordResponse implements Serializable {
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean status;
    public String message;
}
