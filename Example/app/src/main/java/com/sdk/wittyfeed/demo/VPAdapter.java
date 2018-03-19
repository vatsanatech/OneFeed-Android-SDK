package com.sdk.wittyfeed.demo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by aishwarydhare on 19/03/18.
 */

public class VPAdapter extends FragmentStatePagerAdapter {

    private Fragment fragment;
    VPAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        if(position == 0){
            fragment = new HomeFragment();
        } else {
            fragment = new FeedFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    Fragment get_active_fragment(){
        return fragment;
    }
}
