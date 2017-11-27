package com.example.nhan.hackathonmusicfinal.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.nhan.hackathonmusicfinal.R;
import com.example.nhan.hackathonmusicfinal.utils.Constant;
import com.example.nhan.hackathonmusicfinal.models.SongRealmObject;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nhan on 10/31/2016.
 */

public class ListSongViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.image_song)CircleImageView imageView;
    @BindView(R.id.tv_name_artist)TextView tvArtist;
    @BindView(R.id.tv_name_song)TextView tvName;
    public ListSongViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setData(SongRealmObject songRealmObject){
        Picasso.with(itemView.getContext())
                .load(songRealmObject.getImageUrl())
                .fit()
                .centerCrop()
                .into(imageView);
        tvName.setText(songRealmObject.getSongName());
        tvArtist.setText(songRealmObject.getSongArtist());
        if (!Constant.internetConnected){
            if (songRealmObject.getDownload()){
                itemView.setAlpha(1.0f);
            } else {
                itemView.setAlpha(0.3f);
            }
        } else {
            itemView.setAlpha(1.0f);
        }
        itemView.setTag(songRealmObject);
    }
}
