package com.deals.jeetodeals.Model;

import java.io.Serializable;

public class CustomLotteryData implements Serializable {

    public String getMax_tickets() {
        return max_tickets;
    }

    public void setMax_tickets(String max_tickets) {
        this.max_tickets = max_tickets;
    }

    public String getMax_tickets_per_user() {
        return max_tickets_per_user;
    }

    public void setMax_tickets_per_user(String max_tickets_per_user) {
        this.max_tickets_per_user = max_tickets_per_user;
    }

    public String getMin_tickets() {
        return min_tickets;
    }

    public void setMin_tickets(String min_tickets) {
        this.min_tickets = min_tickets;
    }

    public int getTotal_sales() {
        return total_sales;
    }

    public void setTotal_sales(int total_sales) {
        this.total_sales = total_sales;
    }

    public String max_tickets;
    public String max_tickets_per_user;
    public String min_tickets;
    public int total_sales;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int progress;
}
