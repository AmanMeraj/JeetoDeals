package com.deals.jeetodeals.Adapters;

// Class to hold sort option details
public class SortOption {
    private final String displayName;
    private final String order;
    private final String orderBy;

    public SortOption(String displayName, String order, String orderBy) {
        this.displayName = displayName;
        this.order = order;
        this.orderBy = orderBy;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getOrder() {
        return order;
    }

    public String getOrderBy() {
        return orderBy;
    }
}
