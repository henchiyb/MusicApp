package com.example.nhan.hackathonmusicfinal.events;

import com.example.nhan.hackathonmusicfinal.models.SongRealmObject;

/**
 * Created by Nhan on 10/31/2016.
 */

public class EventSearchSong {
    private SongRealmObject songRealmObject;
    private Boolean notFound;

    public EventSearchSong(SongRealmObject songRealmObject, Boolean notFound) {
        this.songRealmObject = songRealmObject;
        this.notFound = notFound;
    }

    public SongRealmObject getSongRealmObject() {
        return songRealmObject;
    }

    public Boolean getNotFound() {
        return notFound;
    }
}
