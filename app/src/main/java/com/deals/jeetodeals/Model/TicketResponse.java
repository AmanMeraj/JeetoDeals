package com.deals.jeetodeals.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

public class TicketResponse implements Serializable {
    @SerializedName("active_tickets")
    private Map<String, ProductTickets> active_tickets;
    @SerializedName("past_tickets")
    private Map<String, ProductTickets> past_tickets;

    public Map<String, ProductTickets> getActive_tickets() {
        return active_tickets;
    }

    public Map<String, ProductTickets> getPast_tickets() {
        return past_tickets;
    }
}
