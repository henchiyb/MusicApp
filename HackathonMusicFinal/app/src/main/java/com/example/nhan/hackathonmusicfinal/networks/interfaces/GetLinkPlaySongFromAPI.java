package com.example.nhan.hackathonmusicfinal.networks.interfaces;

import com.example.nhan.hackathonmusicfinal.networks.ApiUrl;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Nhan on 10/31/2016.
 */

public interface GetLinkPlaySongFromAPI {
    @GET(ApiUrl.API_GET_PLAY_SONG)
    Call<Docs> callDocs(@Query("requestdata") String string);

    class Docs{
        @SerializedName("docs")
        private List<InfoLinkSong> linkSongList;

        public List<InfoLinkSong> getLinkSongList() {
            return linkSongList;
        }
    }

    class InfoLinkSong{
        @SerializedName("song_id")
        private String songId;
        @SerializedName("title")
        private String title;
        @SerializedName("artist")
        private String artist;
        @SerializedName("link_download")
        private Link linkDownload;
        @SerializedName("source")
        private Link linkPlay;

        public String getSongId() {
            return songId;
        }

        public String getTitle() {
            return title;
        }

        public String getArtist() {
            return artist;
        }

        public Link getLinkDownload() {
            return linkDownload;
        }

        public Link getLinkPlay() {
            return linkPlay;
        }
    }

    class Link{
        @SerializedName("128")
        private String link;

        public String getLink() {
            return link;
        }
    }
}
