package com.example.nhan.hackathonmusicfinal.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nhan.hackathonmusicfinal.R;
import com.example.nhan.hackathonmusicfinal.utils.Constant;
import com.example.nhan.hackathonmusicfinal.utils.Utils;
import com.example.nhan.hackathonmusicfinal.databases.RealmHandler;
import com.example.nhan.hackathonmusicfinal.events.EventBackPress;
import com.example.nhan.hackathonmusicfinal.events.EventDataReady;
import com.example.nhan.hackathonmusicfinal.events.EventInternetConnected;
import com.example.nhan.hackathonmusicfinal.events.EventMiniPlayer;
import com.example.nhan.hackathonmusicfinal.events.EventOpenMainPlayer;
import com.example.nhan.hackathonmusicfinal.events.EventSearchSong;
import com.example.nhan.hackathonmusicfinal.events.EventShowToolbar;
import com.example.nhan.hackathonmusicfinal.events.EventPlayOfline;
import com.example.nhan.hackathonmusicfinal.fragments.FragmentListSongTop;
import com.example.nhan.hackathonmusicfinal.fragments.FragmentMain;
import com.example.nhan.hackathonmusicfinal.fragments.FragmentMainPlayer;
import com.example.nhan.hackathonmusicfinal.models.SongRealmObject;
import com.example.nhan.hackathonmusicfinal.networks.ApiUrl;
import com.example.nhan.hackathonmusicfinal.networks.ServiceFactory;
import com.example.nhan.hackathonmusicfinal.networks.interfaces.GetLinkPlaySongFromAPI;
import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    @BindView(R.id.tv_name_song_mini)TextView tvSongNameMini;
    @BindView(R.id.tv_name_artist_mini)TextView tvArtistNameMini;
    @BindView(R.id.image_song_mini)CircleImageView imageSongMini;
    @BindView(R.id.btn_play_mini)FloatingActionButton btnPlayMini;
    @BindView(R.id.mini_player)CardView miniPlayer;
    @BindView(R.id.seek_bar_mini_player)SeekBar seekBarMini;
    @OnClick(R.id.btn_play_mini)
    public void onClickPlayMini(){
        if(Utils.getExoPlayer().getPlayWhenReady()){
            Utils.getExoPlayer().setPlayWhenReady(false);
            btnPlayMini.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        } else {
            Utils.getExoPlayer().setPlayWhenReady(true);
            btnPlayMini.setImageResource(R.drawable.ic_pause_black_24dp);
        }
    }
    @OnClick(R.id.mini_player)
    public void onCLickMiniPlayer(){
        EventBus.getDefault().postSticky(new EventOpenMainPlayer(songRealmObject));
        miniPlayer.setVisibility(View.INVISIBLE);
        openFragmentMainPlayer(new FragmentMainPlayer());
    }

    private SongRealmObject songRealmObject;
    private long startTime;
    private long finalTime;
    private Handler myHandler = new Handler();
    @BindView(R.id.toolbar)Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        setSupportActionBar(toolbar);
        ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean isConnectedInternet) {
                        if (isConnectedInternet){
                            Handler handler = new Handler(getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Internet Connected", Toast.LENGTH_LONG).show();
                                }
                            });
                            EventBus.getDefault().postSticky(new EventInternetConnected());
                            Constant.internetConnected = true;
                        } else {
                            Handler handler = new Handler(getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                                }
                            });
//                            EventBus.getDefault().postSticky(new EventPlayOfline());
                            EventBus.getDefault().postSticky(new EventDataReady());
                            Constant.internetConnected = false;
                        }
                    }
                });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Itunes Music");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Utils.openFragment(getSupportFragmentManager(), new FragmentMain());
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this add items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Subscribe
    public void onShowToolbar(EventShowToolbar event){
        if (event.getShow()){
            toolbar.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onBackPressed(EventBackPress event){
        super.onBackPressed();
    }

    @Subscribe
    public void onSearchSongLinkPlay(EventSearchSong event){
        songRealmObject = event.getSongRealmObject();
        tvSongNameMini.setText(songRealmObject.getSongName());
        tvArtistNameMini.setText(songRealmObject.getSongArtist());
        Picasso.with(this)
                .load(songRealmObject.getImageUrl())
                .fit()
                .centerCrop()
                .into(imageSongMini);
        Utils.getExoPlayer().stop();
        Utils.getExoPlayer().seekTo(0);
        if (!Constant.internetConnected) {
            if (songRealmObject.getDownload()) {
                Utils.setDataOfflineExoPlayer(songRealmObject.getSongPath());
                btnPlayMini.setBackgroundResource(R.drawable.ic_pause_white_24dp);
                miniPlayer.setVisibility(View.INVISIBLE);
                Utils.getExoPlayer().setPlayWhenReady(true);
                EventBus.getDefault().postSticky(new EventOpenMainPlayer(songRealmObject));
                if (getSupportFragmentManager().getBackStackEntryCount() < 3) {
                    openFragmentMainPlayer(new FragmentMainPlayer());
                }
            } else {
                Toast.makeText(getApplicationContext(), "This song is not downloaded", Toast.LENGTH_LONG).show();
            }
        } else {
            String songName;
            if (songRealmObject.getSongName().contains("(")) {
                songName = songRealmObject.getSongName().substring(0, songRealmObject.getSongName().indexOf("(") - 1);
            } else {
                songName = songRealmObject.getSongName();
            }

            ServiceFactory serviceFactory = new ServiceFactory(ApiUrl.BASE_URL_PLAY_SONG);
            GetLinkPlaySongFromAPI service = serviceFactory.createService(GetLinkPlaySongFromAPI.class);
            Call<GetLinkPlaySongFromAPI.Docs> call;
            if (!event.getNotFound()) {
                call = service.callDocs("{\"q\":\""
                        + songName + " "
                        + songRealmObject.getSongArtist()
                        + "\", \"sort\":\"hot\", \"start\":\"0\", \"length\":\"10\"}");
            } else {
                call = service.callDocs("{\"q\":\""
                        + songName
                        + "\", \"sort\":\"hot\", \"start\":\"0\", \"length\":\"10\"}");
            }

            call.enqueue(new Callback<GetLinkPlaySongFromAPI.Docs>() {
                @Override
                public void onResponse(Call<GetLinkPlaySongFromAPI.Docs> call, Response<GetLinkPlaySongFromAPI.Docs> response) {
                    List<GetLinkPlaySongFromAPI.InfoLinkSong> linkSongLink = response.body().getLinkSongList();
                    if (linkSongLink.size() != 0) {
                        List<Integer> listRatio = new ArrayList<Integer>();
                        for (int i = 0; i < linkSongLink.size(); i++) {
                            RealmHandler.getInstance().addLinkToSongRealm(songRealmObject,
                                    linkSongLink.get(i).getLinkPlay().getLink(),
                                    linkSongLink.get(i).getLinkDownload().getLink());
                            int ratio = Utils.getRatio(songRealmObject.getSongName(), linkSongLink.get(i).getTitle(), false)
                                    + Utils.getRatio(songRealmObject.getSongArtist(), linkSongLink.get(i).getArtist(), false);

                            listRatio.add(ratio);
                        }
                        Utils.setDataStreamExoPlayer(MainActivity.this,
                                linkSongLink.get(Utils.getIndexOfMax(listRatio))
                                        .getLinkPlay()
                                        .getLink());
                        btnPlayMini.setImageResource(R.drawable.ic_pause_black_24dp);
                        Utils.getExoPlayer().setPlayWhenReady(true);
                        miniPlayer.setVisibility(View.INVISIBLE);
                        EventBus.getDefault().postSticky(new EventOpenMainPlayer(songRealmObject));
                        if (getSupportFragmentManager().getBackStackEntryCount() < 3) {
                            openFragmentMainPlayer(new FragmentMainPlayer());
                        }
                    } else {
                        Handler handler = new Handler(getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "This is not found", Toast.LENGTH_SHORT).show();
                            }
                        });
                        EventBus.getDefault().post(new EventSearchSong(songRealmObject, true));
                    }
                }

                @Override
                public void onFailure(Call<GetLinkPlaySongFromAPI.Docs> call, Throwable t) {

                }
            });
        }
    }

    private void controlSeekBar(){
        Runnable UpdateSongTime = new Runnable() {
            public void run() {
                startTime = Utils.getExoPlayer().getCurrentPosition();
                seekBarMini.setProgress((int)startTime);
                myHandler.postDelayed(this, 100);
            }
        };
        finalTime = Utils.getExoPlayer().getDuration();
        startTime = Utils.getExoPlayer().getCurrentPosition();
        seekBarMini.setMax((int) finalTime);
        seekBarMini.setProgress((int)startTime);
        myHandler.postDelayed(UpdateSongTime,100);
        if (Utils.getExoPlayer().getPlayWhenReady()){
            btnPlayMini.setImageResource(R.drawable.ic_pause_black_24dp);
        } else {
            btnPlayMini.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (Utils.getExoPlayer().getPlayWhenReady()){
//            miniPlayer.setVisibility(View.VISIBLE);
//        }
        Utils.getExoPlayer().addListener(new ExoPlayer.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(playbackState == ExoPlayer.STATE_ENDED){
                    SongRealmObject song = RealmHandler.getInstance().getSongNextInGenres(FragmentListSongTop.genres, songRealmObject);
                    EventBus.getDefault().post(new EventSearchSong(song, false));
                }
                if (playbackState == ExoPlayer.STATE_READY){
                    controlSeekBar();
                }
            }

            @Override
            public void onPlayWhenReadyCommitted() {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }
        });

        seekBarMini.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Utils.getExoPlayer().seekTo(seekBar.getProgress());
            }
        });
    }

    private void openFragmentMainPlayer(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_out_down, 0, 0, R.anim.slide_in_up);
        fragmentTransaction.add(R.id.container, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Utils.getExoPlayer().stop();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count==0){
            super.onBackPressed();
        }
    }

    @Subscribe
    public void onPlayOfline(EventPlayOfline event){
        songRealmObject = event.getSongRealmObject();
        tvSongNameMini.setText(songRealmObject.getSongName());
        tvArtistNameMini.setText(songRealmObject.getSongArtist());
        Picasso.with(this)
                .load(songRealmObject.getImageUrl())
                .fit()
                .centerCrop()
                .into(imageSongMini);
//        miniPlayer.setVisibility(View.VISIBLE);
        Utils.getExoPlayer().stop();
        Utils.getExoPlayer().seekTo(0);
        Utils.setDataOfflineExoPlayer(songRealmObject.getSongPath());
        btnPlayMini.setImageResource(R.drawable.ic_pause_black_24dp);
        Utils.getExoPlayer().setPlayWhenReady(true);
        miniPlayer.setVisibility(View.INVISIBLE);
        if (getSupportFragmentManager().getBackStackEntryCount() < 3) {
            EventBus.getDefault().postSticky(new EventOpenMainPlayer(songRealmObject));
            openFragmentMainPlayer(new FragmentMainPlayer());
        }
    }
    @Subscribe
    public void showMiniPlayer(EventMiniPlayer event) {
        if (event.getShow()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    miniPlayer.setVisibility(View.VISIBLE);
                }
            }, 500);
        }else {
            miniPlayer.setVisibility(View.INVISIBLE);
        }
    }
}
