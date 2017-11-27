package com.example.nhan.hackathonmusicfinal.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nhan.hackathonmusicfinal.R;
import com.example.nhan.hackathonmusicfinal.adapters.viewholders.HomeViewHolder;
import com.example.nhan.hackathonmusicfinal.databases.RealmHandler;
import com.example.nhan.hackathonmusicfinal.models.GenresRealmObject;

import java.util.List;

/**
 * Created by Nhan on 10/31/2016.
 */

public class HomeViewAdapter extends RecyclerView.Adapter<HomeViewHolder> {

    private List<GenresRealmObject> genresRealmObjectList = RealmHandler.getInstance().getListGenresInRealm();

    private View.OnClickListener onItemClickListener;

    public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view =inflater.inflate(R.layout.item_recyle_view_home, parent, false);

        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HomeViewHolder holder, int position) {
        holder.setData(genresRealmObjectList.get(position));
        holder.itemView.setOnClickListener(onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return genresRealmObjectList.size();
    }
}
