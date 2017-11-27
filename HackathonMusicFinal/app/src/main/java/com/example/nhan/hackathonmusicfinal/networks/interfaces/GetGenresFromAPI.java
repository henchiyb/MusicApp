package com.example.nhan.hackathonmusicfinal.networks.interfaces;

import com.example.nhan.hackathonmusicfinal.networks.ApiUrl;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Nhan on 10/31/2016.
 */

public interface GetGenresFromAPI {
    @GET(ApiUrl.API_GET_DATA_TYPE)
    Call<List<Subgenres>> callListSubgenres();

    class Subgenres{
        @SerializedName("subgenres")
        private List<Genres> genresList;
        @SerializedName("translation_key")
        private String typrOfGenres;

        public List<Genres> getGenresList() {
            return genresList;
        }

        public String getTyprOfGenres() {
            return typrOfGenres;
        }
    }
    class Genres{
        @SerializedName("id")
        private String id;
        @SerializedName("translation_key")
        private String genresName;

        public String getId() {
            return id;
        }

        public String getGenresName() {
            return genresName;
        }
    }

}
