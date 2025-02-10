package com.deals.jeetodeals.Model;

import java.io.Serializable;

public class TicketGroupedByProduct implements Serializable {
    private String productName;
    private String productImage;
    private String ticketNumbers;

    public TicketGroupedByProduct(String productName, String productImage, String ticketNumbers) {
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

    public String getTicketNumbers() {
        return ticketNumbers;
    }
}
