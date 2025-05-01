package com.deals.jeetodeals.Model;

import java.io.Serializable;

public class ChangePassword implements Serializable {
    public String getCurrent_password() {
        return current_password;
    }

    public void setCurrent_password(String current_password) {
        this.current_password = current_password;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public String current_password;
    public String new_password;
}
