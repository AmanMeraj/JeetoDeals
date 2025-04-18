package com.deals.jeetodeals.Model;

import java.io.Serializable;

public class DynamicItem implements Serializable {

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDisplay_key() {
        return display_key;
    }

    public void setDisplay_key(String display_key) {
        this.display_key = display_key;
    }

    public String getDisplay_value() {
        return display_value;
    }

    public void setDisplay_value(String display_value) {
        this.display_value = display_value;
    }

    public String key;
    public String value;
    public String display_key;
    public String display_value;
}
