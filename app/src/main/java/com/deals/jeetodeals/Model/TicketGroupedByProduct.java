package com.deals.jeetodeals.Model;

import java.util.List;

public class TicketGroupedByProduct {
    private String productName;
    private String productImage;
    private List<String> ticketNumbers;  // Store list instead of single string

    public TicketGroupedByProduct(String productName, String productImage, List<String> ticketNumbers) {
        this.productName = productName;
        this.productImage = productImage;
        this.ticketNumbers = ticketNumbers;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public List<String> getTicketNumbers() {
        return ticketNumbers;
    }
}
