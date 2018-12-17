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
    private boolean isNotification;
    private boolean isFeed;
    private boolean isCard;
    private boolean isSearchCard;
    private String storyId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            isNotification = getIntent().getBooleanExtra(Constant.NOTIFICATION, false);
            isFeed = getIntent().getBooleanExtra(Constant.ONE_FEED, false);
            isCard = getIntent().getBooleanExtra(Constant.CARD_VIEWED, false);
            isSearchCard = getIntent().getBooleanExtra(Constant.SEARCH_CARD_VIEWED, false);

            if (isNotification) {
                activity = (Class) getIntent().getSerializableExtra(Constant.ACTIVITY);
                NotificationModel model = (NotificationModel) getIntent().getSerializableExtra(Constant.MODEL);
                storyId = model.getStoryId();
                OneFeedSdk.getInstance().getJobManager().addJobInBackground(
                        new PostUserTrackingJob(Constant.STORY_OPENED, Constant.STORY_OPENED_BY_NOTIFICATION, model.getStoryId(), model.getNoId()));
                Util.showCustomTabBrowserByNotification(this, Color.DKGRAY, model.getTitle(), model.getStoryUrl(), model.getStoryId());
            } else {
                int color = getIntent().getIntExtra(Constant.COLOR, 0);
                String title = getIntent().getStringExtra(Constant.TITLE);
                String url = getIntent().getStringExtra(Constant.URL);
                storyId = getIntent().getStringExtra(Constant.ID);
                String resc = "";
                boolean isFeed = true;
                if(isSearchCard) {
                    resc = Constant.STORY_OPENED_BY_SEARCH_CARD;
                    isFeed = false;
                }else if(isCard){
                    isFeed = false;
                    resc = Constant.STORY_OPENED_BY_CARD;
                }
                Util.showCustomTabBrowserByCard(this, color, title, url, storyId, resc, isFeed);
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isStoryLoaded) {
            isStoryLoaded = false;
            if (isNotification) {
                Intent intent = new Intent(this, activity);
                startActivity(intent);
            }

            String type = "";
            String res = "";

            if (isNotification) {
                type = Constant.APP_VIEWED;
                res = Constant.STORY_BACK;
            } else if (isCard) {

                type = Constant.CARD_VIEWED;
                res = Constant.STORY_BACK;
            } else if (isFeed) {

                type = Constant.ONE_FEED;
                res = Constant.STORY_BACK;
            } else  if (isSearchCard) {

                type = Constant.SEARCH_CARD_VIEWED;
                res = Constant.STORY_BACK;

            }

            //Tracking
            OneFeedSdk.getInstance().getJobManager().addJobInBackground(
                    new PostUserTrackingJob(type, res, storyId));
            finish();
        }
        isStoryLoaded = true;
    }

    @Override
    public void onBackPressed() {

        if (activity != null) {

            Intent intent = new Intent(this, activity);
            startActivity(intent);
        }
        finish();
    }
}
