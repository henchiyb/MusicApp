package com.example.nhan.hackathonmusicfinal.events;

/**
 * Created by Nhan on 10/31/2016.
 */

public class EventMiniPlayer {
    private Boolean isShow;

    public EventMiniPlayer(Boolean isShow) {
        this.isShow = isShow;
    }

    public Boolean getShow() {
        return isShow;
    }
}
