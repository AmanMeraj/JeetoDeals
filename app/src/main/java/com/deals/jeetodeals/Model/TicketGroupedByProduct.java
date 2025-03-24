package com.deals.jeetodeals.Model;

import java.util.List;
import java.util.Map;

public class TicketGroupedByProduct {
    private String productName;
    private String productImage;
    private List<Map<String, String>> ticketInfo; // Changed to store both ticket number and date

    public TicketGroupedByProduct(String productName, String productImage, List<Map<String, String>> ticketInfo) {
        this.productName = productName;
        this.productImage = productImage;
        this.ticketInfo = ticketInfo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public List<Map<String, String>> getTicketInfo() {
        return ticketInfo;
    }

    public void setTicketInfo(List<Map<String, String>> ticketInfo) {
        this.ticketInfo = ticketInfo;
    }

    // For backward compatibility
    public List<Map<String, String>> getTicketNumbers() {
        return ticketInfo;
    }
}