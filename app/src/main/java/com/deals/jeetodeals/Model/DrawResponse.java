package com.deals.jeetodeals.Model;

import java.io.Serializable;

public class DrawResponse implements Serializable {
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

    public String getDraw_button_link() {
        return draw_button_link;
    }

    public void setDraw_button_link(String draw_button_link) {
        this.draw_button_link = draw_button_link;
    }

    public int id;
    public String title;
    public String image;
    public String draw_button_link;
}
