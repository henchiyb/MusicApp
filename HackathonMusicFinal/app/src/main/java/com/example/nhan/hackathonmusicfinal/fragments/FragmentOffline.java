package com.example.nhan.hackathonmusicfinal.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nhan.hackathonmusicfinal.R;
import com.example.nhan.hackathonmusicfinal.adapters.ListSongViewAdapter;
import com.example.nhan.hackathonmusicfinal.databases.RealmHandler;
import com.example.nhan.hackathonmusicfinal.events.EventPlayOfline;
import com.example.nhan.hackathonmusicfinal.models.SongRealmObject;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nhan on 10/31/2016.
 */

public class FragmentOffline extends Fragment implements View.OnClickListener {

    @BindView(R.id.recycle_view_offline)RecyclerView recyclerView;
    private List<SongRealmObject> listSong;
    private ListSongViewAdapter adapter;
    private SongRealmObject song;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offline, container, false);
        ButterKnife.bind(this, view);
//        EventBus.getDefault().post(new EventShowToolbar(false));
        listSong = RealmHandler.getInstance().getListSongDownloadFromRealm();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new ListSongViewAdapter(listSong);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onClick(View v) {
        song = (SongRealmObject) v.getTag();
        EventBus.getDefault().post(new EventPlayOfline(song));
    }
}
