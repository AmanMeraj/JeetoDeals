package com.deals.jeetodeals.Model;

import java.io.Serializable;

public class QuantityLimits implements Serializable {

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    public int getMultiple_of() {
        return multiple_of;
    }

    public void setMultiple_of(int multiple_of) {
        this.multiple_of = multiple_of;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public int minimum;
    public int maximum;
    public int multiple_of;
    public boolean editable;
}
