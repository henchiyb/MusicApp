package com.example.nhan.hackathonmusicfinal.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Nhan on 10/31/2016.
 */

public class SongRealmObject extends RealmObject {
    @PrimaryKey private String songName;
    private String songArtist;
    private Boolean isDownloaded = false;
    private String linkPlaySong;
    private String linkDownloadSong;
    private String imageUrl;
    private String imageBigUrl;
    private String songPath;

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public Boolean getDownload() {
        return isDownloaded;
    }

    public void setDowload(Boolean dowload) {
        isDownloaded = dowload;
    }

    public String getLinkPlaySong() {
        return linkPlaySong;
    }

    public void setLinkPlaySong(String linkPlaySong) {
        this.linkPlaySong = linkPlaySong;
    }

    public String getLinkDownloadSong() {
        return linkDownloadSong;
    }

    public void setLinkDownloadSong(String linkDownloadSong) {
        this.linkDownloadSong = linkDownloadSong;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageBigUrl() {
        return imageBigUrl;
    }

    public void setImageBigUrl(String imageBigUrl) {
        this.imageBigUrl = imageBigUrl;
    }

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }
}
