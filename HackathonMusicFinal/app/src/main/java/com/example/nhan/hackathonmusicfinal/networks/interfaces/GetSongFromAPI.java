package com.example.nhan.hackathonmusicfinal.networks.interfaces;

import com.example.nhan.hackathonmusicfinal.networks.ApiUrl;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Nhan on 10/31/2016.
 */

public interface GetSongFromAPI {
    @GET(ApiUrl.API_GET_SONG)
    Call<Feed> callFeed(@Path("id") String string);

    class Feed{
        @SerializedName("feed")
        private SongEntry songEntry;

        public SongEntry getSongEntry() {
            return songEntry;
        }
    }
    class SongEntry{
        @SerializedName("entry")
        private List<Entry> entryList;

        public List<Entry> getEntryList() {
            return entryList;
        }
    }

    class Entry{
        @SerializedName("im:name")
        private Label songName;
        @SerializedName("im:artist")
        private Label artistName;
        @SerializedName("im:image")
        private List<Label> labelList;

        public Label getSongName() {
            return songName;
        }

        public Label getArtistName() {
            return artistName;
        }

        public List<Label> getLabelList() {
            return labelList;

        }
    }

    class Label{
        @SerializedName("label")
        private String label;

        public String getLabel() {
            return label;
        }
    }
}
