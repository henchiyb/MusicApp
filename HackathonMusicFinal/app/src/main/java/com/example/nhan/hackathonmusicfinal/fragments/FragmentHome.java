package com.example.nhan.hackathonmusicfinal.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import com.example.nhan.hackathonmusicfinal.R;
import com.example.nhan.hackathonmusicfinal.utils.Utils;
import com.example.nhan.hackathonmusicfinal.adapters.HomeViewAdapter;
import com.example.nhan.hackathonmusicfinal.databases.RealmHandler;
import com.example.nhan.hackathonmusicfinal.events.EventDataReady;
import com.example.nhan.hackathonmusicfinal.events.EventInternetConnected;
import com.example.nhan.hackathonmusicfinal.events.EventSendGenres;
import com.example.nhan.hackathonmusicfinal.events.EventShowToolbar;
import com.example.nhan.hackathonmusicfinal.models.GenresRealmObject;
import com.example.nhan.hackathonmusicfinal.networks.ApiUrl;
import com.example.nhan.hackathonmusicfinal.networks.ServiceFactory;
import com.example.nhan.hackathonmusicfinal.networks.interfaces.GetGenresFromAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nhan on 10/31/2016.
 */

public class FragmentHome extends Fragment implements View.OnClickListener {
    private static final String IMAGE_NAME_PRE = "genre_2x_";
    @BindView(R.id.recycle_view_home)RecyclerView recyclerView;

    private GridLayoutManager layoutManager;
    private HomeViewAdapter adapter;
    private ServiceFactory serviceFactory;
    private GenresRealmObject genresObject;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        EventBus.getDefault().register(this);
        EventBus.getDefault().postSticky(new EventShowToolbar(true));
        layoutManager = new GridLayoutManager(this.getContext(),
                2,
                GridLayout.VERTICAL,
                false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (position % 3 == 0 ? 2 : 1);
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true)
    public void setAdapterView(EventDataReady event){
        adapter = new HomeViewAdapter();
        adapter.setOnItemClickListener(this);
        recyclerView.swapAdapter(adapter, true);
        adapter.notifyDataSetChanged();
    }

    @Subscribe(sticky = true)
    public void loadDataByRetrofit(EventInternetConnected event){
        if (RealmHandler.getInstance().getListGenresInRealm().size() == 0) {
            serviceFactory = new ServiceFactory(ApiUrl.BASE_URL_DATA_TYPE);
            GetGenresFromAPI service = serviceFactory.createService(GetGenresFromAPI.class);
            Call<List<GetGenresFromAPI.Subgenres>> call = service.callListSubgenres();
            call.enqueue(new Callback<List<GetGenresFromAPI.Subgenres>>() {
                @Override
                public void onResponse(Call<List<GetGenresFromAPI.Subgenres>> call, Response<List<GetGenresFromAPI.Subgenres>> response) {
                    RealmHandler.getInstance().clearGenresRealmData();
                    List<GetGenresFromAPI.Subgenres> genresList = response.body();
                    for (int i = 0; i < genresList.size(); i++) {
                        if (genresList.get(i).getTyprOfGenres().equals("music")) {
                            for (int j = 0; j < genresList.get(i).getGenresList().size(); j++) {
                                GenresRealmObject genres = new GenresRealmObject();
                                genres.setName(genresList.get(i).getGenresList().get(j).getGenresName());
                                genres.setIdName(genresList.get(i).getGenresList().get(j).getId());
                                genres.setImageResId(getImageId(genresList.get(i).getGenresList().get(j).getId()));
                                RealmHandler.getInstance().addGenresToRealm(genres);
                            }
                        }
                    }
                    EventBus.getDefault().postSticky(new EventDataReady());
                }

                @Override
                public void onFailure(Call<List<GetGenresFromAPI.Subgenres>> call, Throwable t) {

                }
            });
        } else {
            EventBus.getDefault().postSticky(new EventDataReady());
        }
    }
    private int getImageId(String id){
        int idRes = getContext().getResources().getIdentifier(IMAGE_NAME_PRE + id, "drawable", getContext().getPackageName());
        if (idRes != 0)
            return idRes;
        else return R.drawable.genre_2x_9;
    }

    @Override
    public void onClick(View v) {
        genresObject = (GenresRealmObject) v.getTag();
        EventBus.getDefault().postSticky(new EventSendGenres(genresObject));
        Utils.openFragment(getActivity().getSupportFragmentManager(), new FragmentListSongTop());
    }
}
