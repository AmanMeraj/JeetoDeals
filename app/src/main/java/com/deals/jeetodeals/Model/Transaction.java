package com.deals.jeetodeals.Model;

public class Transaction {
    private final String heading;
    private final String description;
    private final String amount;
    private final String status;
    private final int imageResource;
    private final int imageBackground;

    public Transaction(String heading, String description, String amount, String status, int imageResource, int imageBackground) {
        this.heading = heading;
        this.description = description;
        this.amount = amount;
        this.status = status;
        this.imageResource = imageResource;
        this.imageBackground = imageBackground;
    }

    public String getHeading() {
        return heading;
    }

    public String getDescription() {
        return description;
    }

    public String getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public int getImageResource() {
        return imageResource;
    }

    public int getImageBackground() {
        return imageBackground;
    }
}
