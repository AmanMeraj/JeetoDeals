package com.deals.jeetodeals.Model;

import java.io.Serializable;

public class MetaData implements Serializable {

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

    public String key;
    public String value;
}
