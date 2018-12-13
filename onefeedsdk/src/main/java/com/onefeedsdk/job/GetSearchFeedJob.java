package com.onefeedsdk.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.onefeedsdk.app.Constant;
import com.onefeedsdk.app.OneFeedSdk;
import com.onefeedsdk.app.RuntimeStore;
import com.onefeedsdk.event.Event;
import com.onefeedsdk.listener.AddResponseListener;
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
    private AddResponseListener listener;

    public GetSearchFeedJob(String search) {
        super(new Params(Priority.HIGH).groupBy("home-feed"));
        this.search = search;
    }

    public GetSearchFeedJob(String search, AddResponseListener listener) {
        super(new Params(Priority.HIGH).groupBy("home-feed"));
        this.search = search;
        this.listener = listener;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {

        try {

            String userId = OneFeedSdk.getInstance().getDefaultAppSharedPreferences()
                    .getString(Constant.USER_ID, "0");
            Call<FeedModel> call = null;
            if (listener == null) {
                call = OneFeedSdk.getInstance().getApiFactory().getApi()
                        .searchFeed(search, 0, OneFeedSdk.getInstance().getAppId(),
                                userId, Util.getAndroidUniqueId(), OneFeedSdk.getInstance().VERSION);
            } else {
                call = OneFeedSdk.getInstance().getApiFactory().getApi()
                        .searchByApp(search, 0, OneFeedSdk.getInstance().getAppId(),
                                userId, Util.getAndroidUniqueId(), OneFeedSdk.getInstance().VERSION);
            }
            FeedModel feed = call.execute().body();

            sendSearchFeedEvent(feed, true);
        } catch (Exception e) {
            Log.e("Search Exception", e.getMessage());
            //Error Tracking
            OneFeedSdk.getInstance().getJobManager().addJobInBackground(new PostErrorTrackingJob("SearchFeed", e.getMessage()));
            //OneFeedSdk.getInstance().getEventBus().postSticky(new Event.SearchFeedEvent(null, false, false));
            sendSearchFeedEvent(null, false);
        }
    }

    private void sendSearchFeedEvent(FeedModel feed, boolean isSuccess) {
        if (listener != null) {
            if (isSuccess) {
                RuntimeStore.getInstance().putKeyValues(Constant.SEARCH_CARD, feed);
                //Tracking
                OneFeedSdk.getInstance().getJobManager().addJobInBackground(
                        new PostUserTrackingJob(Constant.SEARCH_VIEWED, Constant.SEARCH_BY,
                                search));
                listener.success();
            } else {
                listener.error();
            }
        } else {
            OneFeedSdk.getInstance().getEventBus().postSticky(new Event.SearchFeedEvent(feed, isSuccess, false));
        }

    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        //OneFeedSdk.getInstance().getEventBus().postSticky(new Event.SearchFeedEvent(null, false, false));
        sendSearchFeedEvent(null, false);
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
