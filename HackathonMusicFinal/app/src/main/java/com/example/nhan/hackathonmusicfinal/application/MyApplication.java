package com.example.nhan.hackathonmusicfinal.application;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Nhan on 10/31/2016.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration realmConfiguration = new RealmConfiguration
                .Builder(getApplicationContext())
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
