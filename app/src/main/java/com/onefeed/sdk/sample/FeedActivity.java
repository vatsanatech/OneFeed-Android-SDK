package com.onefeed.sdk.sample;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.onefeedsdk.app.OneFeedSdk;
import com.onefeedsdk.job.GetHomeFeedJob;
import com.onefeedsdk.ui.fragment.MainFeedFragment;
import com.onefeedsdk.ui.fragment.OneFeedFragment;

public class FeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new OneFeedFragment())
                .commit();
    }
}
