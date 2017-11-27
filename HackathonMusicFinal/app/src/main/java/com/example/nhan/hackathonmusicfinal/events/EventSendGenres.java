package com.example.nhan.hackathonmusicfinal.events;

import com.example.nhan.hackathonmusicfinal.models.GenresRealmObject;

/**
 * Created by Nhan on 10/31/2016.
 */

public class EventSendGenres {
    private GenresRealmObject genres;

    public EventSendGenres(GenresRealmObject genres){
        this.genres = genres;
    }

    public GenresRealmObject getGenres() {
        return genres;
    }
}
