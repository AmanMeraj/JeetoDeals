package com.deals.jeetodeals.Model;

import java.io.Serializable;

public class BannerResponse implements Serializable {
    public String getVideo_banner_url() {
        return video_banner_url;
    }

    public void setVideo_banner_url(String video_banner_url) {
        this.video_banner_url = video_banner_url;
    }

    public String getImage_banner_url() {
        return image_banner_url;
    }

    public void setImage_banner_url(String image_banner_url) {
        this.image_banner_url = image_banner_url;
    }

    public String video_banner_url;
    public String image_banner_url;

    public String getJeeto_shop_banner_video() {
        return jeeto_shop_banner_video;
    }

    public void setJeeto_shop_banner_video(String jeeto_shop_banner_video) {
        this.jeeto_shop_banner_video = jeeto_shop_banner_video;
    }

    public String jeeto_shop_banner_video;
}
