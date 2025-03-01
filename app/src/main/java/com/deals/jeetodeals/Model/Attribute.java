package com.deals.jeetodeals.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class Attribute implements Serializable {

    public Attribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(Object taxonomy) {
        this.taxonomy = taxonomy;
    }

    public boolean isHas_variations() {
        return has_variations;
    }

    public void setHas_variations(boolean has_variations) {
        this.has_variations = has_variations;
    }

    public ArrayList<Term> getTerms() {
        return terms;
    }

    public void setTerms(ArrayList<Term> terms) {
        this.terms = terms;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int id;
    public String name;
    public Object taxonomy;
    public boolean has_variations;
    public ArrayList<Term> terms;
    public String value;
}
