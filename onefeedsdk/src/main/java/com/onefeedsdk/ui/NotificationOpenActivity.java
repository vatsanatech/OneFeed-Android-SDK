package com.onefeedsdk.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.onefeedsdk.app.Constant;
import com.onefeedsdk.app.OneFeedSdk;
import com.onefeedsdk.job.PostUserTrackingJob;
import com.onefeedsdk.model.NotificationModel;
import com.onefeedsdk.util.Util;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 05-October-2018
 * Time: 13:33
 */
public class NotificationOpenActivity extends AppCompatActivity {

    private Class activity;
    private boolean isStoryLoaded = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            boolean isNotification = getIntent().getBooleanExtra("NOTIFICATION", false);

            if(isNotification) {
                activity = (Class) getIntent().getSerializableExtra(Constant.ACTIVITY);
                NotificationModel model = (NotificationModel) getIntent().getSerializableExtra(Constant.MODEL);

                OneFeedSdk.getInstance().getJobManager().addJobInBackground(
                        new PostUserTrackingJob(Constant.STORY_OPENED, Constant.STORY_OPENED_BY_NOTIFICATION, model.getStoryId(), model.getNoId()));
                Util.showCustomTabBrowserByNotification(this, Color.DKGRAY, model.getTitle(), model.getStoryUrl(), model.getStoryId());
            }else{
                int color = getIntent().getIntExtra("COLOR", 0);
                String title = getIntent().getStringExtra("TITLE");
                String url = getIntent().getStringExtra("URL");
                String id = getIntent().getStringExtra("ID");

                Util.showCustomTabBrowserByCard(this, color, title, url, id);
            }
        }catch (Exception e){

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isStoryLoaded) {
            isStoryLoaded = false;
            Intent intent = new Intent(this, activity);
            startActivity(intent);
            finish();
        }else{
            finish();
        }

        isStoryLoaded = true;
    }

    @Override
    public void onBackPressed() {

        if(activity != null) {

            Intent intent = new Intent(this, activity);
            startActivity(intent);
        }
        finish();
    }
}
