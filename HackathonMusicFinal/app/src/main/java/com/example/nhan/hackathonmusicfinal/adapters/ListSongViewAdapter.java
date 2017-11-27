package com.example.nhan.hackathonmusicfinal.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nhan.hackathonmusicfinal.R;
import com.example.nhan.hackathonmusicfinal.adapters.viewholders.ListSongViewHolder;
import com.example.nhan.hackathonmusicfinal.models.SongRealmObject;

import java.util.List;

/**
 * Created by Nhan on 10/31/2016.
 */

public class ListSongViewAdapter extends RecyclerView.Adapter<ListSongViewHolder> {
    private List<SongRealmObject> songRealmObjects;

    public ListSongViewAdapter(List<SongRealmObject> songRealmObjects) {
        this.songRealmObjects = songRealmObjects;
    }

    private View.OnClickListener onItemClickListener;

    public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    @Override
    public ListSongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_recycle_view_list_song, parent, false);

        return new ListSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListSongViewHolder holder, int position) {
        holder.setData(songRealmObjects.get(position));
        holder.itemView.setOnClickListener(onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return songRealmObjects.size();
    }
}
