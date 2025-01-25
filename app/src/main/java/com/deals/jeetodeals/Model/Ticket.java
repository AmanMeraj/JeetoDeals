package com.deals.jeetodeals.Model;

public class Ticket {
    private final String phoneName;
    private final String ticketId;
    private final int imageResource;

    public Ticket(String phoneName, String ticketId, int imageResource) {
        this.phoneName = phoneName;
        this.ticketId = ticketId;
        this.imageResource = imageResource;
    }

    public String getPhoneName() {
        return phoneName;
    }

    public String getTicketId() {
        return ticketId;
    }

    public int getImageResource() {
        return imageResource;
    }
}
