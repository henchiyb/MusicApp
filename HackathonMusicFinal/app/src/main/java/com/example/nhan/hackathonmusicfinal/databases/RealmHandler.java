package com.example.nhan.hackathonmusicfinal.databases;

import com.example.nhan.hackathonmusicfinal.models.GenresRealmObject;
import com.example.nhan.hackathonmusicfinal.models.SongRealmObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Nhan on 10/31/2016.
 */

public class RealmHandler {
    private static RealmHandler instance;
    private Realm realm;

    public static RealmHandler getInstance() {
        if (instance == null)
            instance = new RealmHandler();
        return instance;
    }

    private RealmHandler(){
        this.realm = Realm.getDefaultInstance();
    }

    public void addGenresToRealm(GenresRealmObject genresRealmObject){
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(genresRealmObject);
        realm.commitTransaction();
    }

    public List<GenresRealmObject> getListGenresInRealm(){
        return realm.where(GenresRealmObject.class).findAll();
    }

    public void addSongToGenres(GenresRealmObject genresRealmObject, SongRealmObject songRealmObject){
        realm.beginTransaction();
        genresRealmObject.getListSongInGenres().add(songRealmObject);
        realm.commitTransaction();
    }

    public void setFavoriteOfGenresInRealm(GenresRealmObject genres, Boolean favorite){
        realm.beginTransaction();
        genres.setFavorite(favorite);
        realm.commitTransaction();
    }

    public void setPathOfSOngInRealm(SongRealmObject song, String path){
        realm.beginTransaction();
        song.setSongPath(path);
        realm.commitTransaction();
    }
    public void setIsDownloadSongInRealm(SongRealmObject song, Boolean isDowloand){
        realm.beginTransaction();
        song.setDowload(isDowloand);
        realm.commitTransaction();
    }

    public void addLinkToSongRealm(SongRealmObject song, String linkPlay, String linkDownload){
        realm.beginTransaction();
        song.setLinkPlaySong(linkPlay);
        song.setLinkDownloadSong(linkDownload);
        realm.commitTransaction();
    }

    public List<GenresRealmObject> getListGenresFavoriteFromRealm(){
        return realm.where(GenresRealmObject.class).equalTo("favorite",true).findAll();
    }

    public List<SongRealmObject> getListSongDownloadFromRealm(){
        return realm.where(SongRealmObject.class).equalTo("isDownloaded",true).findAll();
    }

    public GenresRealmObject getOneGenresFromRealm(String name){
        return realm.where(GenresRealmObject.class).equalTo("name", name).findFirst();
    }

    public void clearGenresRealmData(){
        realm.beginTransaction();
        realm.delete(GenresRealmObject.class);
        realm.commitTransaction();

    }

    public SongRealmObject getSongNextInGenres(GenresRealmObject genresRealmObject, SongRealmObject songRealmObject){
        int i;
        int nextSongId = 0;
        for (i = 0; i < genresRealmObject.getListSongInGenres().size(); i++) {
            if (songRealmObject.equals(genresRealmObject.getListSongInGenres().get(i))) {
                if (i < genresRealmObject.getListSongInGenres().size() - 1) {
                    nextSongId = i + 1;
                } else {
                    nextSongId = i;
                }
            }
        }
        return genresRealmObject.getListSongInGenres().get(nextSongId);
    }
    public SongRealmObject getSongNextOfflineInGenres(GenresRealmObject genresRealmObject, SongRealmObject songRealmObject){
        int i;
        int nextSongId = 0;
        List<SongRealmObject> listSongs = new ArrayList<>();
        for (i = 0; i < genresRealmObject.getListSongInGenres().size(); i++) {
            if (genresRealmObject.getListSongInGenres().get(i).getDownload()){
                listSongs.add(genresRealmObject.getListSongInGenres().get(i));
            }
        }

        for (i = 0; i < listSongs.size(); i++) {
            if (songRealmObject.equals(listSongs.get(i))) {
                if (i < listSongs.size() - 1) {
                    nextSongId = i + 1;
                } else {
                    nextSongId = i;
                }
            }
        }
        if (listSongs.size() == 0){
            return songRealmObject;
        } else {
            return listSongs.get(nextSongId);
        }
    }

    public SongRealmObject getSongRewindOfflineInGenres(GenresRealmObject genresRealmObject, SongRealmObject songRealmObject){
        int i;
        int preSongId = 0;
        List<SongRealmObject> listSongs = new ArrayList<>();
        for (i = 0; i < genresRealmObject.getListSongInGenres().size(); i++) {
            if (genresRealmObject.getListSongInGenres().get(i).getDownload()){
                listSongs.add(genresRealmObject.getListSongInGenres().get(i));
            }
        }

        for (i = 0; i < listSongs.size(); i++) {
            if (songRealmObject.equals(listSongs.get(i))) {
                if (i > 0) {
                    preSongId = i - 1;
                } else {
                    preSongId = 0;
                }
            }
        }
        if (listSongs.size() == 0){
            return songRealmObject;
        } else {
            return listSongs.get(preSongId);
        }
    }
    public SongRealmObject getSongRewindInGenres(GenresRealmObject genresRealmObject, SongRealmObject songRealmObject){
        int i;
        int preSongId = 0;
        for (i = 0; i < genresRealmObject.getListSongInGenres().size(); i++) {
            if (songRealmObject.equals(genresRealmObject.getListSongInGenres().get(i))) {
                if (i > 0) {
                    preSongId = i - 1;
                } else {
                    preSongId = 0;
                }
            }
        }
        return genresRealmObject.getListSongInGenres().get(preSongId);
    }

    public void clearListSongObject(GenresRealmObject genres){
        realm.beginTransaction();
        genres.getListSongInGenres().clear();
        realm.commitTransaction();

    }


}
