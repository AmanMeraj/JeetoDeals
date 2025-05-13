package com.deals.jeetodeals.Model;

import com.google.gson.annotations.SerializedName;

public class CouponResponse {

    @SerializedName("code")
    private String code;

    @SerializedName("discount_type")
    private String discountType;

    @SerializedName("totals")
    private Totals totals;

    public String getCode() {
        return code;
    }

    public String getDiscountType() {
        return discountType;
    }

    public Totals getTotals() {
        return totals;
    }

    public static class Totals {
        @SerializedName("total_discount")
        private String totalDiscount;

        @SerializedName("total_discount_tax")
        private String totalDiscountTax;

        @SerializedName("currency_code")
        private String currencyCode;

        @SerializedName("currency_symbol")
        private String currencySymbol;

        @SerializedName("currency_minor_unit")
        private int currencyMinorUnit;

        @SerializedName("currency_decimal_separator")
        private String currencyDecimalSeparator;

        @SerializedName("currency_thousand_separator")
        private String currencyThousandSeparator;

        @SerializedName("currency_prefix")
        private String currencyPrefix;

        @SerializedName("currency_suffix")
        private String currencySuffix;

        // Getters
        public String getTotalDiscount() {
            return totalDiscount;
        }

        public String getTotalDiscountTax() {
            return totalDiscountTax;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

        public String getCurrencySymbol() {
            return currencySymbol;
        }

        public int getCurrencyMinorUnit() {
            return currencyMinorUnit;
        }

        public String getCurrencyDecimalSeparator() {
            return currencyDecimalSeparator;
        }

        public String getCurrencyThousandSeparator() {
            return currencyThousandSeparator;
        }

        public String getCurrencyPrefix() {
            return currencyPrefix;
        }

        public String getCurrencySuffix() {
            return currencySuffix;
        }
    }
}
