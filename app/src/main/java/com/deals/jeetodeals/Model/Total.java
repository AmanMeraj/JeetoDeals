package com.deals.jeetodeals.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class Total implements Serializable {
    public String getLine_subtotal() {
        return line_subtotal;
    }

    public void setLine_subtotal(String line_subtotal) {
        this.line_subtotal = line_subtotal;
    }

    public String getLine_subtotal_tax() {
        return line_subtotal_tax;
    }

    public void setLine_subtotal_tax(String line_subtotal_tax) {
        this.line_subtotal_tax = line_subtotal_tax;
    }

    public String getLine_total() {
        return line_total;
    }

    public void setLine_total(String line_total) {
        this.line_total = line_total;
    }

    public String getLine_total_tax() {
        return line_total_tax;
    }

    public void setLine_total_tax(String line_total_tax) {
        this.line_total_tax = line_total_tax;
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

    public String getTotal_items() {
        return total_items;
    }

    public void setTotal_items(String total_items) {
        this.total_items = total_items;
    }

    public String getTotal_items_tax() {
        return total_items_tax;
    }

    public void setTotal_items_tax(String total_items_tax) {
        this.total_items_tax = total_items_tax;
    }

    public String getTotal_fees() {
        return total_fees;
    }

    public void setTotal_fees(String total_fees) {
        this.total_fees = total_fees;
    }

    public String getTotal_fees_tax() {
        return total_fees_tax;
    }

    public void setTotal_fees_tax(String total_fees_tax) {
        this.total_fees_tax = total_fees_tax;
    }

    public String getTotal_discount() {
        return total_discount;
    }

    public void setTotal_discount(String total_discount) {
        this.total_discount = total_discount;
    }

    public String getTotal_discount_tax() {
        return total_discount_tax;
    }

    public void setTotal_discount_tax(String total_discount_tax) {
        this.total_discount_tax = total_discount_tax;
    }

    public String getTotal_shipping() {
        return total_shipping;
    }

    public void setTotal_shipping(String total_shipping) {
        this.total_shipping = total_shipping;
    }

    public String getTotal_shipping_tax() {
        return total_shipping_tax;
    }

    public void setTotal_shipping_tax(String total_shipping_tax) {
        this.total_shipping_tax = total_shipping_tax;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getTotal_tax() {
        return total_tax;
    }

    public void setTotal_tax(String total_tax) {
        this.total_tax = total_tax;
    }

    public ArrayList<Object> getTax_lines() {
        return tax_lines;
    }

    public void setTax_lines(ArrayList<Object> tax_lines) {
        this.tax_lines = tax_lines;
    }

    public String line_subtotal;
    public String line_subtotal_tax;
    public String line_total;
    public String line_total_tax;
    public String currency_code;
    public String currency_symbol;
    public int currency_minor_unit;
    public String currency_decimal_separator;
    public String currency_thousand_separator;
    public String currency_prefix;
    public String currency_suffix;
    public String total_items;
    public String total_items_tax;
    public String total_fees;
    public String total_fees_tax;
    public String total_discount;
    public String total_discount_tax;
    public String total_shipping;
    public String total_shipping_tax;
    public String total_price;
    public String total_tax;
    public ArrayList<Object> tax_lines;
}
