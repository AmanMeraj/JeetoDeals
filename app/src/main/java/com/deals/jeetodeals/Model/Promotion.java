package com.deals.jeetodeals.Model;

import java.io.Serializable;

public class Promotion implements Serializable {
    private String time;
    private int imageResourceId;
    private String totalTickets;
    private String winText;
    private String heading;
    private String description;
    private String chanceText;
    private String heading2;
    private String vouchers;

    // Constructor
    public Promotion(String time, int imageResourceId, String totalTickets, String winText,
                     String heading, String description, String chanceText, String heading2,
                     String vouchers) {
        this.time = time;
        this.imageResourceId = imageResourceId;
        this.totalTickets = totalTickets;
        this.winText = winText;
        this.heading = heading;
        this.description = description;
        this.chanceText = chanceText;
        this.heading2 = heading2;
        this.vouchers = vouchers;
    }

    // Getters
    public String getTime() {
        return time;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public String getTotalTickets() {
        return totalTickets;
    }

    public String getWinText() {
        return winText;
    }

    public String getHeading() {
        return heading;
    }

    public String getDescription() {
        return description;
    }

    public String getChanceText() {
        return chanceText;
    }

    public String getHeading2() {
        return heading2;
    }

    public String getVouchers() {
        return vouchers;
    }
}
