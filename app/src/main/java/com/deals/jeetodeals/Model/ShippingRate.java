package com.deals.jeetodeals.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class ShippingRate implements Serializable {
    public int getPackage_id() {
        return package_id;
    }

    public void setPackage_id(int package_id) {
        this.package_id = package_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public ArrayList<Items> getItems() {
        return items;
    }

    public void setItems(ArrayList<Items> items) {
        this.items = items;
    }

    public ArrayList<ShippingRate> getShipping_rates() {
        return shipping_rates;
    }

    public void setShipping_rates(ArrayList<ShippingRate> shipping_rates) {
        this.shipping_rates = shipping_rates;
    }

    public String getRate_id() {
        return rate_id;
    }

    public void setRate_id(String rate_id) {
        this.rate_id = rate_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDelivery_time() {
        return delivery_time;
    }

    public void setDelivery_time(String delivery_time) {
        this.delivery_time = delivery_time;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTaxes() {
        return taxes;
    }

    public void setTaxes(String taxes) {
        this.taxes = taxes;
    }

    public int getInstance_id() {
        return instance_id;
    }

    public void setInstance_id(int instance_id) {
        this.instance_id = instance_id;
    }

    public String getMethod_id() {
        return method_id;
    }

    public void setMethod_id(String method_id) {
        this.method_id = method_id;
    }

    public ArrayList<MetaData> getMeta_data() {
        return meta_data;
    }

    public void setMeta_data(ArrayList<MetaData> meta_data) {
        this.meta_data = meta_data;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public String getCurrency_symbol() {
        return currency_symbol;
    }

    public void setCurrency_symbol(String currency_symbol) {
        this.currency_symbol = currency_symbol;
    }

    public int getCurrency_minor_unit() {
        return currency_minor_unit;
    }

    public void setCurrency_minor_unit(int currency_minor_unit) {
        this.currency_minor_unit = currency_minor_unit;
    }

    public String getCurrency_decimal_separator() {
        return currency_decimal_separator;
    }

    public void setCurrency_decimal_separator(String currency_decimal_separator) {
        this.currency_decimal_separator = currency_decimal_separator;
    }

    public String getCurrency_thousand_separator() {
        return currency_thousand_separator;
    }

    public void setCurrency_thousand_separator(String currency_thousand_separator) {
        this.currency_thousand_separator = currency_thousand_separator;
    }

    public String getCurrency_prefix() {
        return currency_prefix;
    }

    public void setCurrency_prefix(String currency_prefix) {
        this.currency_prefix = currency_prefix;
    }

    public String getCurrency_suffix() {
        return currency_suffix;
    }

    public void setCurrency_suffix(String currency_suffix) {
        this.currency_suffix = currency_suffix;
    }

    public int package_id;
    public String name;
    public Destination destination;
    public ArrayList<Items> items;
    public ArrayList<ShippingRate> shipping_rates;
    public String rate_id;
    public String description;
    public String delivery_time;
    public String price;
    public String taxes;
    public int instance_id;
    public String method_id;
    public ArrayList<MetaData> meta_data;
    public boolean selected;
    public String currency_code;
    public String currency_symbol;
    public int currency_minor_unit;
    public String currency_decimal_separator;
    public String currency_thousand_separator;
    public String currency_prefix;
    public String currency_suffix;
}
