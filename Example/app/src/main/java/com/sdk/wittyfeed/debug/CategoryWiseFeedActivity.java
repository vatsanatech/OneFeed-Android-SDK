package com.sdk.wittyfeed.debug;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sdk.wittyfeed.debug.adapter.CategoryFeedViewPagerAdapter;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKSingleton;

/**
 * Created by aishwarydhare on 15/11/17.
 */

public class CategoryWiseFeedActivity extends AppCompatActivity {

    private static final String TAG = "WF_SDK";
    TabLayout categories_tabLayout;
    ViewPager feed_vp;
    CategoryFeedViewPagerAdapter categoryFeedViewPagerAdapter;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_wise_feed);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);


        categories_tabLayout = findViewById(R.id.categories_tabLayout);
        feed_vp = findViewById(R.id.feed_vp);
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


        categoryFeedViewPagerAdapter = new CategoryFeedViewPagerAdapter(getSupportFragmentManager());
        feed_vp.setAdapter(categoryFeedViewPagerAdapter);
        feed_vp.setOffscreenPageLimit(3);
    }

    @Override
    public void onDetachedFromWindow() {
        Log.d(TAG, "onDetachedFromWindow: ");
        try {
            super.onDetachedFromWindow();
        } catch (Exception e) {
            Log.d(TAG, "onDetachedFromWindow: ", e);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        try {
            super.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
    }


}