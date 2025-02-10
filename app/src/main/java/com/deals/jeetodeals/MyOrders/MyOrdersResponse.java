package com.deals.jeetodeals.MyOrders;

import com.deals.jeetodeals.Model.OrderItem;

import java.io.Serializable;
import java.util.Map;

public class MyOrdersResponse implements Serializable {

    private int order_id;
    private String status;
    private String total;
    private String date_created;
    private Map<String, OrderItem> items;

    // Getters and Setters
    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public Map<String, OrderItem> getItems() {
        return items;
    }

    public void setItems(Map<String, OrderItem> items) {
        this.items = items;
    }
}
