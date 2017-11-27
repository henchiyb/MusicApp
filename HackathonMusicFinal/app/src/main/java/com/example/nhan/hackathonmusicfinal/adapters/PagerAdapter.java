package com.example.nhan.hackathonmusicfinal.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.nhan.hackathonmusicfinal.fragments.FragmentHome;
import com.example.nhan.hackathonmusicfinal.fragments.FragmentOffline;
import com.example.nhan.hackathonmusicfinal.fragments.FragmentPlaylist;

/**
 * Created by Nhan on 10/31/2016.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int countPage;
    public PagerAdapter(FragmentManager fm, int countPage) {
        super(fm);
        this.countPage = countPage;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new FragmentHome();
            case 1: return new FragmentPlaylist();
            case 2: return new FragmentOffline();
            default: return new FragmentHome();
        }
    }

    @Override
    public int getCount() {
        return countPage;
    }
}
