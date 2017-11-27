package com.example.nhan.hackathonmusicfinal.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.nhan.hackathonmusicfinal.R;
import com.example.nhan.hackathonmusicfinal.utils.Utils;
import com.example.nhan.hackathonmusicfinal.databases.RealmHandler;
import com.example.nhan.hackathonmusicfinal.events.EventSendGenres;
import com.example.nhan.hackathonmusicfinal.models.GenresRealmObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nhan on 10/31/2016.
 */

public class FragmentPlaylist extends Fragment {
    @BindView(R.id.listview_playlist)ListView listView;
    private ArrayList<String> listGenresName;
    private ArrayAdapter<String> adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_playlist, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        listGenresName = stringList(RealmHandler.getInstance().getListGenresFavoriteFromRealm());
        adapter = new ArrayAdapter<>(this.getContext(), R.layout.item_playlist_list_view, listGenresName);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GenresRealmObject genres = RealmHandler.getInstance().getOneGenresFromRealm(listGenresName.get(position));
                EventBus.getDefault().postSticky(new EventSendGenres(genres));
                Utils.openFragment(getActivity().getSupportFragmentManager(), new FragmentListSongTop());
            }
        });
        return view;
    }

    @Subscribe(sticky = true)
    public void saveToPlaylists(GenresRealmObject genresRealmObject){
        EventBus.getDefault().removeStickyEvent(genresRealmObject);
        listGenresName = stringList(RealmHandler.getInstance().getListGenresFavoriteFromRealm());
        adapter = new ArrayAdapter<>(this.getContext(), R.layout.item_playlist_list_view, listGenresName);
        listView.setAdapter(adapter);
    }

    private ArrayList<String> stringList(List<GenresRealmObject> genresRealmObjectList){
        ArrayList<String> stringList = new ArrayList<>();
        for(GenresRealmObject genresRealmObject: genresRealmObjectList){
            String string = genresRealmObject.getName();
            stringList.add(string);
        }
        return stringList;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
