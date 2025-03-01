package com.deals.jeetodeals.Model;

import java.io.Serializable;

public class WinnerResponse implements Serializable {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getWinner_name() {
        return winner_name;
    }

    public void setWinner_name(String winner_name) {
        this.winner_name = winner_name;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public int id;
    public String title;
    public String image;
    public String winner_name;
    public String prize;
}
