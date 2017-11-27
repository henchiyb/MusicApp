package com.example.nhan.hackathonmusicfinal.fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nhan.hackathonmusicfinal.R;
import com.example.nhan.hackathonmusicfinal.utils.Constant;
import com.example.nhan.hackathonmusicfinal.utils.Utils;
import com.example.nhan.hackathonmusicfinal.databases.RealmHandler;
import com.example.nhan.hackathonmusicfinal.events.EventBackPress;
import com.example.nhan.hackathonmusicfinal.events.EventMiniPlayer;
import com.example.nhan.hackathonmusicfinal.events.EventOpenMainPlayer;
import com.example.nhan.hackathonmusicfinal.events.EventSearchSong;
import com.example.nhan.hackathonmusicfinal.events.EventShowToolbar;
import com.example.nhan.hackathonmusicfinal.models.SongRealmObject;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Nhan on 10/31/2016.
 */

public class FragmentMainPlayer extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{
    @BindView(R.id.layout_image_play_big)
    RelativeLayout layoutImageBig;
    @BindView(R.id.image_song_big_player)
    ImageView imageView;
    @BindView(R.id.btn_cancel)
    ImageView btnCancel;
    @BindView(R.id.btn_rewind)
    Button btnRewind;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.btn_play_big_player)
    FloatingActionButton btnPlayMain;
    @BindView(R.id.seek_bar_main_player)
    SeekBar seekBarSmall;
    @BindView(R.id.frameLayout)FrameLayout frameLayout;
    @BindView(R.id.tv_current_time)TextView tvCurrentTime;
    @BindView(R.id.tv_duration)TextView tvDuration;
    @BindView(R.id.tv_name_song_main_player)TextView tvSongName;
    @BindView(R.id.tv_name_artist_main_player)TextView tvArtist;
    @BindView(R.id.btn_download) ImageView btnDownload;
    private DownloadAsyncTask downloadAsyncTask;
    @OnClick(R.id.btn_download)
    public void onClickDownload(){
        if (!songRealmObject.getDownload()) {
            downloadAsyncTask = new DownloadAsyncTask();
            downloadAsyncTask.execute(songRealmObject.getLinkPlaySong());
            songName = songRealmObject.getSongName();
            RealmHandler.getInstance().setPathOfSOngInRealm(
                    songRealmObject,
                    getActivity().getExternalFilesDir("").getPath() +"/"+ songRealmObject.getSongName()+".mp3");
            RealmHandler.getInstance().setIsDownloadSongInRealm(songRealmObject, true);
        } else {
            Toast.makeText(getContext(), "This song has been already downloaded", Toast.LENGTH_SHORT).show();
        }
        btnDownload.setClickable(false);
    }
    @BindView(R.id.btn_background_download)
    View backViewDownload;


    private SongRealmObject songRealmObject;
    private long startTime = 0;
    private Handler myHandler = new Handler();
    private SeekBar seekBarImage;
    private String songName;





    @OnClick(R.id.btn_play_big_player)
    public void onCLickPlay() {
        if (Utils.getExoPlayer().getPlayWhenReady()) {
            Utils.getExoPlayer().setPlayWhenReady(false);
            btnPlayMain.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        } else {
            Utils.getExoPlayer().setPlayWhenReady(true);
            btnPlayMain.setImageResource(R.drawable.ic_pause_black_24dp);
        }
    }
    @OnClick(R.id.btn_cancel)
    public void onClickCancel(){
        EventBus.getDefault().post(new EventBackPress());
    }

    private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_player, container, false);
        ButterKnife.bind(this, view);
        Log.d("create","create");
        EventBus.getDefault().register(this);

        EventBus.getDefault().post(new EventShowToolbar(false));

        btnNext.setOnClickListener(this);
        btnRewind.setOnClickListener(this);

        seekBarSmall.setPadding(0, 0, 0, 0);

        FrameLayout.LayoutParams progressBarHorizontalParams = new FrameLayout.LayoutParams(frameLayout.getWidth(), frameLayout.getHeight());

        progressBarHorizontalParams.gravity = Gravity.FILL;
        progressBarHorizontalParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
        progressBarHorizontalParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
        seekBarImage = new SeekBar(this.getContext(), null, android.R.attr.progressBarStyleHorizontal);
        seekBarImage.setPadding(0, 0, 0, 0);
        seekBarImage.setLayoutParams(progressBarHorizontalParams);
        seekBarImage.setScaleY(1000f);
        seekBarImage.setAlpha(0.3f);

        frameLayout.addView(seekBarImage);
        controlSeekBar();
        
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().post(new EventMiniPlayer(true));
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() == 1)
            EventBus.getDefault().post(new EventShowToolbar(true));
        // cancel if download is running
        if (downloadAsyncTask != null && !downloadAsyncTask.isCancelled())
            downloadAsyncTask.cancel(true);
        EventBus.getDefault().unregister(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new EventMiniPlayer(false));
        if (Utils.getExoPlayer().getPlayWhenReady()){
            btnPlayMain.setImageResource(R.drawable.ic_pause_black_24dp);
        } else {
            btnPlayMain.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        }
        Utils.getExoPlayer().addListener(new ExoPlayer.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == ExoPlayer.STATE_READY) {
                    btnNext.setEnabled(true);
                    btnRewind.setEnabled(true);
                    btnNext.setClickable(true);
                    btnRewind.setClickable(true);
                    controlSeekBar();
                }
                if (playWhenReady){
                    btnPlayMain.setImageResource(R.drawable.ic_pause_black_24dp);
                } else {
                    btnPlayMain.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                }
            }

            @Override
            public void onPlayWhenReadyCommitted() {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }
        });

        seekBarSmall.setOnSeekBarChangeListener(this);
        seekBarImage.setOnSeekBarChangeListener(this);

    }

    @Subscribe(sticky = true)
    public void reaceiveSongRealm(EventOpenMainPlayer event) {
        songRealmObject = event.getSongRealmObject();
        Picasso.with(getContext())
                .load(songRealmObject.getImageBigUrl())
                .fit()
                .centerCrop()
                .into(imageView);
        tvSongName.setText(songRealmObject.getSongName());
        tvArtist.setText(songRealmObject.getSongArtist());

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int heightMax = Math.round(24 * (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        ViewGroup.LayoutParams params = backViewDownload.getLayoutParams();
        backViewDownload.setLayoutParams(params);
        if (songRealmObject.getDownload()) {
            params.height = heightMax;
        } else {
            params.height = 0;
        }
        backViewDownload.setLayoutParams(params);

        btnDownload.setClickable(true);
        Log.d("down",songRealmObject.getDownload() + "");
    }

    private Runnable UpdateSongTime = new Runnable() {
        @SuppressLint("DefaultLocale")
        public void run() {
            startTime = Utils.getExoPlayer().getCurrentPosition();
            tvCurrentTime.setText(String.format("%d :%d ",

                    TimeUnit.MILLISECONDS.toMinutes(startTime),
                    TimeUnit.MILLISECONDS.toSeconds(startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes(startTime)))
            );
            seekBarSmall.setProgress((int) startTime);
            seekBarImage.setProgress((int) startTime);
            myHandler.postDelayed(this, 100);

        }
    };

    @SuppressLint("DefaultLocale")
    private void controlSeekBar() {
        long finalTime = Utils.getExoPlayer().getDuration();
        startTime = Utils.getExoPlayer().getCurrentPosition();
        seekBarSmall.setMax((int) finalTime);
        seekBarImage.setMax((int) finalTime);

        tvDuration.setText(String.format("%d :%d ",
                TimeUnit.MILLISECONDS.toMinutes(finalTime),
                TimeUnit.MILLISECONDS.toSeconds(finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime)))
        );

        tvCurrentTime.setText(String.format("%d :%d ",
                TimeUnit.MILLISECONDS.toMinutes(startTime),
                TimeUnit.MILLISECONDS.toSeconds(startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime)))
        );

        seekBarSmall.setProgress((int) startTime);
        seekBarImage.setProgress((int) startTime);
        myHandler.postDelayed(UpdateSongTime, 100);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_next:
                btnNext.setEnabled(false);
                btnNext.setClickable(false);
                if (Constant.internetConnected) {
                    EventBus.getDefault().post(new EventSearchSong(RealmHandler
                            .getInstance()
                            .getSongNextInGenres(FragmentListSongTop.genres, songRealmObject), false));
                } else {
                    EventBus.getDefault().post(new EventSearchSong(RealmHandler
                            .getInstance()
                            .getSongNextOfflineInGenres(FragmentListSongTop.genres, songRealmObject), false));
                }
                break;
            case R.id.btn_rewind:
                btnRewind.setEnabled(false);
                btnRewind.setClickable(false);
                if (Constant.internetConnected) {
                    EventBus.getDefault().post(new EventSearchSong(RealmHandler
                            .getInstance()
                            .getSongRewindInGenres(FragmentListSongTop.genres, songRealmObject), false));
                } else {
                    EventBus.getDefault().post(new EventSearchSong(RealmHandler
                            .getInstance()
                            .getSongRewindOfflineInGenres(FragmentListSongTop.genres, songRealmObject), false));
                }
                break;
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        startTime = progress;
        switch (seekBar.getId()){
            case R.id.seek_bar_main_player:
                seekBarImage.setProgress(progress);
                break;
            default:
                seekBarSmall.setProgress(progress);
                break;
        }
        tvCurrentTime.setText(String.format("%d :%d ",
                TimeUnit.MILLISECONDS.toMinutes(startTime),
                TimeUnit.MILLISECONDS.toSeconds(startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime)))
        );
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        myHandler.removeCallbacks(UpdateSongTime);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Utils.getExoPlayer().seekTo(seekBar.getProgress());
        controlSeekBar();
    }

    public class DownloadAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String[] param) {
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                URL url = new URL(param[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int fileLength = connection.getContentLength();
                inputStream = connection.getInputStream();
                outputStream = new FileOutputStream(getActivity().getExternalFilesDir("").getPath() + "/" + songName + ".mp3");

                byte data[] = new byte[8192];
                long total = 0;
                int count;
                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) publishProgress("" + (int) (total * 100 / fileLength));

                    //writing data to file
                    outputStream.write(data, 0, count);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            } catch (Exception e) {
                Log.d("test", getContext().getCacheDir().toString() + "/" + songName + ".mp3");
                Log.d("test", "error " + e.toString());
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            if (isAdded()) {
                DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
                int heightMax = Math.round(24 * (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                ViewGroup.LayoutParams params = backViewDownload.getLayoutParams();
                params.height = heightMax * Integer.parseInt(progress[0]) / 100;
                backViewDownload.setLayoutParams(params);
            }
            Log.d("progress", progress[0]);
        }



        @Override
        protected void onPostExecute(String string) {
            Toast.makeText(getActivity().getApplicationContext(), songName + " downloaded", Toast.LENGTH_LONG).show();
        }
    }
}