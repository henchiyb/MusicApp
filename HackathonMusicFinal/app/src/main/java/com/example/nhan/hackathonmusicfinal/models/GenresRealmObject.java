package com.example.nhan.hackathonmusicfinal.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Nhan on 10/31/2016.
 */

public class GenresRealmObject extends RealmObject {
    @PrimaryKey private String name;
    private String idName;
    private int imageResId;
    private Boolean favorite = false;
    private RealmList<SongRealmObject> listSongInGenres;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public RealmList<SongRealmObject> getListSongInGenres() {
        return listSongInGenres;
    }

    public void setListSongInGenres(RealmList<SongRealmObject> listSongInGenres) {
        this.listSongInGenres = listSongInGenres;
    }
}
