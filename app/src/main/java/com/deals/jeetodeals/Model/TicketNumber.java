package com.deals.jeetodeals.Model;

public class TicketNumber {
    private String ticket_number;
    private String date_purchased;

    public TicketNumber() {
    }

    public TicketNumber(String ticket_number, String date_purchased) {
        this.ticket_number = ticket_number;
        this.date_purchased = date_purchased;
    }

    public String getTicket_number() {
        return ticket_number;
    }

    public void setTicket_number(String ticket_number) {
        this.ticket_number = ticket_number;
    }

    public String getDate_purchased() {
        return date_purchased;
    }

    public void setDate_purchased(String date_purchased) {
        this.date_purchased = date_purchased;
    }
}