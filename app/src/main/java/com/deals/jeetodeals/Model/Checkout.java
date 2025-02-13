package com.deals.jeetodeals.Model;

import java.io.Serializable;

public class Checkout implements Serializable{
        public static class Address implements Serializable {
            private String first_name;
            private String last_name;
            private String company;
            private String address_1;
            private String address_2;
            private String city;
            private String state;
            private String postcode;
            private String country;
            private String email;
            private String phone;

            // Getters and Setters
            public String getFirst_name() {
                return first_name;
            }

            public void setFirst_name(String first_name) {
                this.first_name = first_name;
            }

            public String getLast_name() {
                return last_name;
            }

            public void setLast_name(String last_name) {
                this.last_name = last_name;
            }

            public String getCompany() {
                return company;
            }

            public void setCompany(String company) {
                this.company = company;
            }

            public String getAddress_1() {
                return address_1;
            }

            public void setAddress_1(String address_1) {
                this.address_1 = address_1;
            }

            public String getAddress_2() {
                return address_2;
            }

            public void setAddress_2(String address_2) {
                this.address_2 = address_2;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getPostcode() {
                return postcode;
            }

            public void setPostcode(String postcode) {
                this.postcode = postcode;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }
        }

        private Address billing_address;
        private Address shipping_address;
        private String customer_note;
        private boolean create_account;
        private String payment_method;

        // Getters and Setters
        public Address getBilling_address() {
            return billing_address;
        }

        public void setBilling_address(Address billing_address) {
            this.billing_address = billing_address;
        }

        public Address getShipping_address() {
            return shipping_address;
        }

        public void setShipping_address(Address shipping_address) {
            this.shipping_address = shipping_address;
        }

        public String getCustomer_note() {
            return customer_note;
        }

        public void setCustomer_note(String customer_note) {
            this.customer_note = customer_note;
        }

        public boolean isCreate_account() {
            return create_account;
        }

        public void setCreate_account(boolean create_account) {
            this.create_account = create_account;
        }

        public String getPayment_method() {
            return payment_method;
        }

        public void setPayment_method(String payment_method) {
            this.payment_method = payment_method;
        }

}
