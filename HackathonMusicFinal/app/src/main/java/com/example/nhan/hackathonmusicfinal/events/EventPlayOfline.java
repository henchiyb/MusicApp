package com.example.nhan.hackathonmusicfinal.events;

import com.example.nhan.hackathonmusicfinal.models.SongRealmObject;

/**
 * Created by Nhan on 10/31/2016.
 */

public class EventPlayOfline {
    private SongRealmObject songRealmObject;
    public EventPlayOfline(SongRealmObject songRealmObject){
        this.songRealmObject = songRealmObject;
    }

    public SongRealmObject getSongRealmObject() {
        return songRealmObject;
    }
}
