package com.deals.jeetodeals.Model;

import java.io.Serializable;

public class Prices implements Serializable {
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRegular_price() {
        return regular_price;
    }

    public void setRegular_price(String regular_price) {
        this.regular_price = regular_price;
    }

    public String getSale_price() {
        return sale_price;
    }

    public void setSale_price(String sale_price) {
        this.sale_price = sale_price;
    }

    public String getPrice_range() {
        return price_range;
    }

    public void setPrice_range(String price_range) {
        this.price_range = price_range;
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

    public String price;
    public String regular_price;
    public String sale_price;
    public String price_range;
    public String currency_code;
    public String currency_symbol;
    public int currency_minor_unit;
    public String currency_decimal_separator;
    public String currency_thousand_separator;
    public String currency_prefix;
    public String currency_suffix;
}
