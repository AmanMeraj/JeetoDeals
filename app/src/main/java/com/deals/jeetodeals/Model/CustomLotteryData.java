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

    public String getLottery_dates_from() {
        return lottery_dates_from;
    }

    public void setLottery_dates_from(String lottery_dates_from) {
        this.lottery_dates_from = lottery_dates_from;
    }

    public String getLottery_dates_to() {
        return lottery_dates_to;
    }

    public void setLottery_dates_to(String lottery_dates_to) {
        this.lottery_dates_to = lottery_dates_to;
    }

    public String lottery_dates_from;
    public String lottery_dates_to;
    public int total_sales;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int progress;
}
