package com.sdk.wittyfeed.debug;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.sdk.wittyfeed.debug.adapter.CategoryFeedViewPagerAdapter;
import com.sdk.wittyfeed.debug.fragment.CategoryFeedViewPagerFragment;
import com.sdk.wittyfeed.debug.fragment.CategoryWiseFeedFragment;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKSingleton;

/**
 * Created by aishwarydhare on 15/11/17.
 */

public class CategoryWiseFeedActivity extends AppCompatActivity {

    private static final String TAG = "WF_SDK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_wise_feed_activity);
        getSupportFragmentManager().beginTransaction().add(R.id.fl_container, new CategoryWiseFeedFragment()).commit();

    }
}