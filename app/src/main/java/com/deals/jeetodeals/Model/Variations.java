package com.deals.jeetodeals.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class Variations implements Serializable {
    public int id;
    public ArrayList<Attribute> attributes;

    public Variations() {
        this.attributes = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttribute(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(String attribute, String value) {
        this.attributes.add(new Attribute(attribute, value));
    }
}
