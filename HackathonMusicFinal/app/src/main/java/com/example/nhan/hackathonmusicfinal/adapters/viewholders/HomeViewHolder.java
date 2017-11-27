package com.example.nhan.hackathonmusicfinal.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nhan.hackathonmusicfinal.R;
import com.example.nhan.hackathonmusicfinal.models.GenresRealmObject;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nhan on 10/31/2016.
 */

public class HomeViewHolder extends RecyclerView.ViewHolder{
    @BindView(R.id.image_genres_home)ImageView imageView;
    @BindView(R.id.genres_name_home)TextView textView;
    public HomeViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setData(GenresRealmObject genres){
        Picasso.with(itemView.getContext())
                .load(genres.getImageResId())
                .fit()
                .centerCrop()
                .into(imageView);
        textView.setText(genres.getName());
        itemView.setTag(genres);
    }
}
