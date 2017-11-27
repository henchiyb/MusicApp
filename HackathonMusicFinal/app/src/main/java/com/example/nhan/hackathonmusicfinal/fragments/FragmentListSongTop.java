package com.example.nhan.hackathonmusicfinal.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nhan.hackathonmusicfinal.R;
import com.example.nhan.hackathonmusicfinal.adapters.ListSongViewAdapter;
import com.example.nhan.hackathonmusicfinal.databases.RealmHandler;
import com.example.nhan.hackathonmusicfinal.events.EventBackPress;
import com.example.nhan.hackathonmusicfinal.events.EventDataReady;
import com.example.nhan.hackathonmusicfinal.events.EventSearchSong;
import com.example.nhan.hackathonmusicfinal.events.EventSendGenres;
import com.example.nhan.hackathonmusicfinal.events.EventShowToolbar;
import com.example.nhan.hackathonmusicfinal.models.GenresRealmObject;
import com.example.nhan.hackathonmusicfinal.models.SongRealmObject;
import com.example.nhan.hackathonmusicfinal.networks.ApiUrl;
import com.example.nhan.hackathonmusicfinal.networks.ServiceFactory;
import com.example.nhan.hackathonmusicfinal.networks.interfaces.GetSongFromAPI;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nhan on 10/31/2016.
 */

public class FragmentListSongTop extends Fragment implements View.OnClickListener {

    @BindView(R.id.recycle_view_list_song)RecyclerView recyclerView;
    @BindView(R.id.image_view_list_song)ImageView imageViewListSong;
    @BindView(R.id.tv_genres_name_list_song)TextView tvGenresName;
    @BindView(R.id.tv_nums_song)TextView tvNumsSong;
    @BindView(R.id.btn_play_first)FloatingActionButton btnPlayFirst;
    @BindView(R.id.layout_list_song)LinearLayout layoutListSong;

    @BindView(R.id.btn_back_arrow)ImageView btnBack;
    @BindView(R.id.btn_search)ImageView btnSearch;
    @BindView(R.id.btn_farvorite)ImageView btnFavorite;

    @BindView(R.id.btn_share)ImageView btnShare;
    @OnClick(R.id.btn_back_arrow)
    public void onCLick(){
        EventBus.getDefault().postSticky(new EventBackPress());
    }

    @OnClick(R.id.btn_farvorite)
    public void onClickFavor(){
        if (!genres.getFavorite()) {
            RealmHandler.getInstance().setFavoriteOfGenresInRealm(genres, true);
            btnFavorite.setBackgroundResource(R.drawable.ic_favorite_white_24dp);
        } else {
            RealmHandler.getInstance().setFavoriteOfGenresInRealm(genres, false);
            btnFavorite.setBackgroundResource(R.drawable.ic_favorite_border_white_24dp);
        }
    }

    @OnClick(R.id.btn_play_first)
    public void onClickPlayBig(){
        btnPlayFirst.setClickable(false);
        btnPlayFirst.setEnabled(false);
        EventBus.getDefault().post(new EventSearchSong(genres.getListSongInGenres().get(0), false));
    }
    private ListSongViewAdapter adapter;
    public static GenresRealmObject genres = RealmHandler.getInstance().getOneGenresFromRealm("all");
    private SongRealmObject song;

    @Subscribe(sticky = true)
    public void onReceiveGenres(EventSendGenres event){
        genres = event.getGenres();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_songs, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new EventShowToolbar(false));
        if (genres.getFavorite()) {
            btnFavorite.setBackgroundResource(R.drawable.ic_favorite_white_24dp);
        } else {
            btnFavorite.setBackgroundResource(R.drawable.ic_favorite_border_white_24dp);
        }
        btnPlayFirst.setClickable(false);
        btnPlayFirst.setEnabled(false);
        loadSongDataFromRetrofit();
        Picasso.with(view.getContext())
                .load(genres.getImageResId())
                .fit()
                .centerCrop()
                .into(imageViewListSong);
        tvGenresName.setText(genres.getName());
        tvNumsSong.setText(R.string._50_songs);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ListSongViewAdapter(genres.getListSongInGenres());
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return view;
    }

    @Subscribe
    public void initAdapterView(EventDataReady event){
        checkSongDownloaded();
        adapter = new ListSongViewAdapter(genres.getListSongInGenres());
        adapter.setOnItemClickListener(this);
        recyclerView.swapAdapter(adapter, true);
        adapter.notifyDataSetChanged();
        btnPlayFirst.setEnabled(true);
        btnPlayFirst.setClickable(true);
    }

    private void checkSongDownloaded(){
        List<SongRealmObject> listSong = genres.getListSongInGenres();
        for (int i =0; i < listSong.size(); i ++) {
            File internalFile = new File(getActivity().getExternalFilesDir("").getPath());
            File files[] = internalFile.listFiles();
            String contentText = "";
            if (files.length == 0) {
                contentText = "No Files Found";
            }
            Boolean isDownloaded = false;
            for (File file : files) {
                contentText = file.getName();
                if ((listSong.get(i).getSongName() + ".mp3").equals(contentText)) {
                    isDownloaded = true;
                    RealmHandler.getInstance().setIsDownloadSongInRealm(listSong.get(i), true);
                    RealmHandler.getInstance().setPathOfSOngInRealm(listSong.get(i), getActivity().getExternalFilesDir("").getPath()
                            +"/"
                            + listSong.get(i).getSongName() + ".mp3");
                }

            }
            RealmHandler.getInstance().setIsDownloadSongInRealm(listSong.get(i), isDownloaded);
        }
    }

    private void loadSongDataFromRetrofit(){
        ServiceFactory serviceFactory = new ServiceFactory(ApiUrl.BASE_URL_SONG);
        GetSongFromAPI service = serviceFactory.createService(GetSongFromAPI.class);
        Call<GetSongFromAPI.Feed> call = service.callFeed(genres.getIdName());
        call.enqueue(new Callback<GetSongFromAPI.Feed>() {
            @Override
            public void onResponse(Call<GetSongFromAPI.Feed> call, Response<GetSongFromAPI.Feed> response) {
                RealmHandler.getInstance().clearListSongObject(genres);
                List<GetSongFromAPI.Entry> entryList = response.body().getSongEntry().getEntryList();
                for (int i = 0; i < entryList.size(); i++){
                    SongRealmObject song = new SongRealmObject();
                    song.setSongName(entryList.get(i).getSongName().getLabel());
                    song.setSongArtist(entryList.get(i).getArtistName().getLabel());
                    song.setImageUrl(entryList.get(i).getLabelList().get(1).getLabel());
                    song.setImageBigUrl(entryList.get(i).getLabelList().get(2).getLabel());
                    RealmHandler.getInstance().addSongToGenres(genres, song);
                }

                EventBus.getDefault().post(new EventDataReady());
            }

            @Override
            public void onFailure(Call<GetSongFromAPI.Feed> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        song = (SongRealmObject) v.getTag();
        EventBus.getDefault().postSticky(new EventSearchSong(song, false));
    }
}
