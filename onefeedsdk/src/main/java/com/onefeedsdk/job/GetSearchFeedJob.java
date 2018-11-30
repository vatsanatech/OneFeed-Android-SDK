package com.onefeedsdk.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.onefeedsdk.app.Constant;
import com.onefeedsdk.app.OneFeedSdk;
import com.onefeedsdk.event.Event;
import com.onefeedsdk.model.FeedModel;
import com.onefeedsdk.util.LogFactory;
import com.onefeedsdk.util.Util;

import retrofit2.Call;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 17-July-2018
 * Time: 15:50
 */
public class GetSearchFeedJob extends BaseJob {

    //Logger
    private static LogFactory.Log log = LogFactory.getLog(GetSearchFeedJob.class);

    private String search;
    private int offset = 0;

    public GetSearchFeedJob(String search) {
        super(new Params(Priority.HIGH).groupBy("home-feed"));
        this.search = search;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {

        try{

            String userId = OneFeedSdk.getInstance().getDefaultAppSharedPreferences()
                    .getString(Constant.USER_ID, "0");

            Call<FeedModel> call = OneFeedSdk.getInstance().getApiFactory().getApi()
                    .searchFeed(search, 0, OneFeedSdk.getInstance().getAppId(),
                            userId, Util.getAndroidUniqueId(), OneFeedSdk.VERSION);
            FeedModel feed = call.execute().body();
            OneFeedSdk.getInstance().getEventBus().postSticky(new Event.SearchFeedEvent(feed, true, false));
        }catch (Exception e){
            log.error(e);
            //Error Tracking
            OneFeedSdk.getInstance().getJobManager().addJobInBackground(new PostErrorTrackingJob("SearchFeed", e.getMessage()));
            OneFeedSdk.getInstance().getEventBus().postSticky(new Event.SearchFeedEvent(null, false, false));
        }
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        OneFeedSdk.getInstance().getEventBus().postSticky(new Event.SearchFeedEvent(null, false, false));
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
