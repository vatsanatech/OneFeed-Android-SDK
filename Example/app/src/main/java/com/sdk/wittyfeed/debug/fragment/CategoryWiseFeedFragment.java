package com.sdk.wittyfeed.debug.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdk.wittyfeed.debug.R;
import com.sdk.wittyfeed.debug.adapter.CategoryFeedViewPagerAdapter;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKSingleton;

/**
 * Created by anujagarwal on 27/02/18.
 */

public class CategoryWiseFeedFragment extends Fragment {

    private static final String TAG = "WF_SDK";
    TabLayout categories_tabLayout;
    ViewPager feed_vp;
    CategoryFeedViewPagerAdapter categoryFeedViewPagerAdapter;
    CoordinatorLayout coordinatorLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_category_wise_feed,container,false);

        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);


        categories_tabLayout = view.findViewById(R.id.categories_tabLayout);
        feed_vp = view.findViewById(R.id.feed_vp);
        categories_tabLayout.setTabTextColors(getResources().getColor(R.color.black_color), getResources().getColor(R.color.colorPrimary));


        for (int i = 0; i < WittyFeedSDKSingleton.getInstance().categoryData_arr.size(); i++) {
            categories_tabLayout.addTab(categories_tabLayout.newTab().setText(WittyFeedSDKSingleton.getInstance().categoryData_arr.get(i).getCatName()));
        }


        categories_tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                feed_vp.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // do nothing
            }
        });


        feed_vp.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(categories_tabLayout));


        categoryFeedViewPagerAdapter = new CategoryFeedViewPagerAdapter(getActivity().getSupportFragmentManager());
        feed_vp.setAdapter(categoryFeedViewPagerAdapter);
        feed_vp.setOffscreenPageLimit(3);


        return view;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        try {
            super.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
    }
}
