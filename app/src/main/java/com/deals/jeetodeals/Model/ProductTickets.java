package com.deals.jeetodeals.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ProductTickets implements Serializable {
    private String product_name;
    private String product_image;
    private List<TicketNumber> tickets;

    // Getters
    public String getProduct_name() { return product_name; }
    public String getProduct_image() { return product_image; }
    public List<TicketNumber> getTickets() { return tickets; }
}
