package com.onefeedsdk.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.onefeedsdk.app.Constant;
import com.onefeedsdk.app.OneFeedSdk;
import com.onefeedsdk.model.TokenUpdateModel;
import com.onefeedsdk.model.TokenUpdateRes;
import com.onefeedsdk.util.Util;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

    public PostTokenUpdateJob(String token) {
        super(new Params(Priority.HIGH).groupBy("token-update"));
        this.token = token;
    }

    @Override
    public void onAdded() {

        requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("app_id", OneFeedSdk.getInstance().getAppId())
                .addFormDataPart("device_id", Util.getAndroidUniqueId())
                .addFormDataPart("firebase_token", token)
                .addFormDataPart("old_firebase_token", "")
                .addFormDataPart("onefeed_sdk_version", OneFeedSdk.VERSION)
                .build();
    }

    @Override
    public void onRun() throws Throwable {
        try {
            Call<TokenUpdateRes> call = OneFeedSdk.getInstance().getApiFactory().getApi().userTokenUpdate(requestBody);
            TokenUpdateRes s = call.execute().body();
            if (s.isStatus()) {
                OneFeedSdk.getInstance().saveToken(token);
            }
        } catch (Exception e) {
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
