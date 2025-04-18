package com.deals.jeetodeals.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class TrackingResponse implements Serializable {
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }

    public String status;
    public ArrayList<Data> data;


    public class Data implements Serializable{
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public int id;
        public String date;
        public String note;
    }


}
