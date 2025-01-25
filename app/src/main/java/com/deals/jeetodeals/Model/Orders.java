package com.deals.jeetodeals.Model;

import java.io.Serializable;

public class Orders implements Serializable {
        private final String time;
        private final String status;
        private final String title;
        private final String voucher;
        private final String date;
        private final String imageUrl;

        public Orders(String time, String status, String title, String voucher, String date, String imageUrl) {
            this.time = time;
            this.status = status;
            this.title = title;
            this.voucher = voucher;
            this.date = date;
            this.imageUrl = imageUrl;
        }

        public String getTime() {
            return time;
        }

        public String getStatus() {
            return status;
        }

        public String getTitle() {
            return title;
        }

        public String getVoucher() {
            return voucher;
        }

        public String getDate() {
            return date;
        }

        public String getImageUrl() {
            return imageUrl;
        }


}
