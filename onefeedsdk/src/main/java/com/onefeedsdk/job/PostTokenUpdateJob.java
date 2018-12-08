package com.onefeedsdk.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.onefeedsdk.app.Constant;
import com.onefeedsdk.app.OneFeedSdk;
import com.onefeedsdk.listener.AddResponseListener;
import com.onefeedsdk.model.TokenUpdateModel;
import com.onefeedsdk.model.TokenUpdateRes;
import com.onefeedsdk.util.Util;

import okhttp3.MultipartBody;
import retrofit2.Call;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 06-October-2018
 * Time: 11:30
 */
public class PostTokenUpdateJob extends BaseJob {

    private final String token;
    private TokenUpdateModel model;
    private MultipartBody requestBody;
    private AddResponseListener responseListener;

    public PostTokenUpdateJob(String token, AddResponseListener responseListener) {
        super(new Params(Priority.HIGH).groupBy("token-update"));
        this.token = token;
        this.responseListener = responseListener;
    }

    @Override
    public void onAdded() {

        requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("app_id", ""+OneFeedSdk.getInstance().getAppId())
                .addFormDataPart("device_id", ""+Util.getAndroidUniqueId())
                .addFormDataPart("firebase_token", ""+token)
                .addFormDataPart("old_firebase_token", "")
                .addFormDataPart("onefeed_sdk_version", ""+(OneFeedSdk.getInstance().VERSION))
                .build();
    }

    @Override
    public void onRun() throws Throwable {
        try {
            Call<TokenUpdateRes> call = OneFeedSdk.getInstance().getApiFactory().getApi().userTokenUpdate(requestBody);
            TokenUpdateRes s = call.execute().body();
            if (s.isStatus()) {
                if (responseListener != null) {
                    responseListener.success();
                }
            } else {
                OneFeedSdk.getInstance().getJobManager().addJobInBackground(new PostUserTrackingJob(Constant.SDK_ERROR, s.getResponse()));
            }
        } catch (Exception e) {

            OneFeedSdk.getInstance().getJobManager().addJobInBackground(new PostUserTrackingJob(Constant.SDK_ERROR, e.getMessage()));
            //Error Tracking
            OneFeedSdk.getInstance().getJobManager().addJobInBackground(new PostErrorTrackingJob("TokenUpdate", e.getMessage()));

            if (responseListener != null) {
                responseListener.error();
            }
        }
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
