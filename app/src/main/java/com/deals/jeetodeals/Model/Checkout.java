package com.deals.jeetodeals.Model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Checkout implements Serializable {
    // Making fields public to ensure proper serialization
    public static class Address implements Serializable {
        public String first_name = "";
        public String last_name = "";
        public String company = "";
        public String address_1 = "";
        public String address_2 = "";
        public String city = "";
        public String state = "";
        public String postcode = "";
        public String country = "";
        public String email = "";
        public String phone = "";
    }

    public static class PaymentData implements Serializable {
        public String razorpay_order_id;
        public String razorpay_payment_id;
        public String razorpay_signature;
    }

    // Making fields public and initializing them
    public Address billing_address = new Address();
    public Address shipping_address = new Address();
    public String customer_note = "";
    public boolean create_account = false;
    public String payment_method = "";
    public List<PaymentData> payment_data;
    public Map<String, Map<String, String>> extensions;
}