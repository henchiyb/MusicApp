package com.example.nhan.hackathonmusicfinal.networks;

/**
 * Created by Nhan on 10/31/2016.
 */

public class ApiUrl {
    public static final String BASE_URL_DATA_TYPE = "https://rss.itunes.apple.com";
    public static final String BASE_URL_SONG = "https://itunes.apple.com";
    public static final String BASE_URL_PLAY_SONG = "http://api.mp3.zing.vn";
    public static final String API_GET_DATA_TYPE = "/data/media-types.json";
    public static final String API_GET_SONG = "/us/rss/topsongs/limit=50/genre={id}/explicit=true/json";
    public static final String API_GET_PLAY_SONG = "/api/mobile/search/song";
}
