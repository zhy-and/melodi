package com.extraordinary.interstings.studios.beans;


public class bannerEvent {


    public bannerEvent(boolean bannerType) {
        this.bannerType = bannerType;
    }

    public Boolean getBannerType() {
        return bannerType;
    }

    public void setBannerType(boolean bannerType) {
        this.bannerType = bannerType;
    }

    private boolean bannerType;
}
