package com.onefeedsdk.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.google.gson.GsonBuilder;
import com.onefeedsdk.app.Constant;
import com.onefeedsdk.app.OneFeedSdk;
import com.onefeedsdk.app.RuntimeStore;
import com.onefeedsdk.listener.AddResponseListener;
import com.onefeedsdk.listener.CallBackListener;
import com.onefeedsdk.model.RepeatingCardModel;
import com.onefeedsdk.util.LogFactory;
import com.onefeedsdk.util.Util;

import retrofit2.Call;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 25-Sep-2018
 * Time: 16:47
 */
public class GetRepeatingCardJob extends BaseJob {

    //Logger
    private static LogFactory.Log log = LogFactory.getLog(GetRepeatingCardJob.class);

    private CallBackListener listener;

    private boolean isLoadMoreFeed = false;
    private int offset = 0;
    private int cardId = 0;

    public void setListener(CallBackListener listener) {
        this.listener = listener;
    }

    public GetRepeatingCardJob(int offset, int cardId) {
        super(new Params(Priority.HIGH).groupBy("home-feed"));
        this.isLoadMoreFeed = isLoadMoreFeed;
        this.offset = offset;
        this.cardId = cardId;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {

        try{
            Call<RepeatingCardModel> call = OneFeedSdk.getInstance().getApiFactory().getApi()
                    .initOneFeedRepeatingCard(OneFeedSdk.getInstance().getAppId(),
                            OneFeedSdk.getInstance().getAppKey(),
                            OneFeedSdk.getInstance().getContext().getPackageName(), offset, "", 1
                            , cardId, Util.getAndroidUniqueId(), OneFeedSdk.getInstance().VERSION);
            RepeatingCardModel newFeed = call.execute().body();

            RepeatingCardModel feedRepeatingCard = (RepeatingCardModel) RuntimeStore.getInstance().getValueFor(String.valueOf(cardId));
            if(feedRepeatingCard != null){
                feedRepeatingCard.getRepeatingCard().getCardList().addAll(newFeed.getRepeatingCard().getCardList());
                RuntimeStore.getInstance().putKeyValues(String.valueOf(cardId), feedRepeatingCard);
            }else{

                if(offset == 0){
                    String feedTemp = new GsonBuilder().create().toJson(newFeed);
                    Util.setPrefValue(String.valueOf(cardId), feedTemp);
                }
            }
            if(listener != null) {
                listener.success();
            }
        }catch (Exception e){
            log.error(e);
            //Error Tracking
            OneFeedSdk.getInstance().getJobManager().addJobInBackground(new PostErrorTrackingJob("Repeating Card", e.getMessage()));
            if(listener != null) {
                listener.error();
            }
        }
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        if(listener != null) {
            listener.error();
        }
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }

}
