package com.sdk.wittyfeed.debug.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.sdk.wittyfeed.debug.fragment.CategoryFeedViewPagerFragment;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKSingleton;
import java.util.ArrayList;

/**
 * Created by aishwarydhare on 12/02/18.
 */

public class CategoryFeedViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> category_wise_vp_fragments;
    private String TAG = "WF_SDK";

    public CategoryFeedViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        category_wise_vp_fragments = new ArrayList<>();
        for (int i = 0; i < WittyFeedSDKSingleton.getInstance().categoryData_arr.size(); i++) {
            Fragment fragment = new CategoryFeedViewPagerFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("cat_pos", i);
            bundle.putString("cat_name", WittyFeedSDKSingleton.getInstance().categoryData_arr.get(i).getCatName());
            fragment.setArguments(bundle);
            category_wise_vp_fragments.add(fragment);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return category_wise_vp_fragments.get(position);
    }

    @Override
    public int getCount() {
        return category_wise_vp_fragments.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.d(TAG, "destroyItem: ");
        super.destroyItem(container, position, object);
    }

}
