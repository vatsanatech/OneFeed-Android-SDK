package com.wittyfeed.sdk.demo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class VPAdapter extends FragmentStatePagerAdapter {

    Fragment OneFeedFragment;

    private Fragment fragment;

    public VPAdapter(FragmentManager fm, Fragment OneFeedFragment){
        super(fm);
        this.OneFeedFragment = OneFeedFragment;
    }

    public VPAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        if(position == 0){
            fragment = new HomeFragment();
        } else {
            fragment = this.OneFeedFragment;
            }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    public Fragment get_active_fragment(){
        return fragment;
    }
}