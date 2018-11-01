package com.onefeedsdk.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.google.gson.GsonBuilder;
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
public class GetHomeFeedJob extends BaseJob {

    //Logger
    private static LogFactory.Log log = LogFactory.getLog(GetHomeFeedJob.class);

    private boolean isLoadMoreFeed ;
    private int offset = 0;

    public GetHomeFeedJob(boolean isLoadMoreFeed, int offset) {
        super(new Params(Priority.HIGH).groupBy("home-feed"));
        this.isLoadMoreFeed = isLoadMoreFeed;
        this.offset = offset;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {

        try{
            Call<FeedModel> call = OneFeedSdk.getInstance().getApiFactory().getApi()
                    .initOneFeedInitialise(OneFeedSdk.getInstance().getAppId(),
                            OneFeedSdk.getInstance().getAppKey(),
                            OneFeedSdk.getInstance().getContext().getPackageName(), offset,
                            "", Util.getAndroidUniqueId(), OneFeedSdk.VERSION);
            FeedModel feed = call.execute().body();

            if(!isLoadMoreFeed && offset == 0){

                Event.FeedEvent event = new Event.FeedEvent(feed, true, isLoadMoreFeed);
                try{
                    Util.setPrefValue(Constant.USER_ID, event.getFeed().getFeedData().getConfig().getUserId());
                }catch (Exception e){
                    Log.e("Exceptions:", e.getMessage());
                }
                String feedTemp = new GsonBuilder().create().toJson(event);
                Util.setPrefValue(Constant.FEED_TEMP, feedTemp);
                OneFeedSdk.getInstance().getEventBus().post(event);
            }else {
                OneFeedSdk.getInstance().getEventBus().postSticky(new Event.FeedEvent(feed, true, isLoadMoreFeed));
            }
        }catch (Exception e){
            log.error(e);
            OneFeedSdk.getInstance().getEventBus().postSticky(new Event.FeedEvent(null, false, isLoadMoreFeed));
        }
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        OneFeedSdk.getInstance().getEventBus().postSticky(new Event.FeedEvent(null, false, isLoadMoreFeed));
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
