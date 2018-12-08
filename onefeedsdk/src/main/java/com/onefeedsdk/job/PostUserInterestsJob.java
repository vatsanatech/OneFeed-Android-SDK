package com.onefeedsdk.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.onefeedsdk.BuildConfig;
import com.onefeedsdk.app.Constant;
import com.onefeedsdk.app.OneFeedSdk;
import com.onefeedsdk.listener.AddResponseListener;
import com.onefeedsdk.model.TrackingModel;
import com.onefeedsdk.util.Util;

import java.util.Locale;

import retrofit2.Call;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 15-November-2018
 * Time: 17:04
 */
public class PostUserInterestsJob extends BaseJob{

    private final String type;
    private final String resource;

    private TrackingModel model;
    private AddResponseListener listener;

    public PostUserInterestsJob(String type, String uAction, String category, String token, @NonNull AddResponseListener listener){
        super(new Params(Priority.HIGH).groupBy("user-tracking"));
        this.type = type;
        this.resource = Constant.RSRC;

        this.listener = listener;

        model = new TrackingModel();
        model.setAction(uAction);
        model.setCategory(category);
        model.setToken(token);
    }

    @Override
    public void onAdded() {

        model.setAppId(OneFeedSdk.getInstance().getAppId());
        model.setDeviceId(Util.getAndroidUniqueId());
        model.setLanguage(Locale.getDefault().getISO3Language());
        //model.setPackageId(getApplicationContext().getPackageName());
        model.setSdkVersion(OneFeedSdk.getInstance().VERSION);
        model.setNetworkType(Util.getNetworkConnectionType(OneFeedSdk.getInstance().getContext()));

        model.setAppUserId(OneFeedSdk.getInstance().getDefaultAppSharedPreferences().getString(Constant.USER_ID, "0"));
        model.setEventType(type);
        model.setResource(resource);
        model.setOs("Android");
        model.setMode(BuildConfig.DEBUG ? "Testing" : "Release");

    }

    @Override
    public void onRun() throws Throwable {
        try{

            Call<String> call = OneFeedSdk.getInstance().getApiFactory().getTrackingApi().userTracking(model);
            String result = call.execute().body();
            Log.e("Tracking - " + type, result);

            //Only Info and App List
            if(listener != null){
                listener.success();
            }
        }catch (Exception e){
            Log.e("Tracking - Error" + type, e.getMessage());
            //Error Tracking
            OneFeedSdk.getInstance().getJobManager().addJobInBackground(new PostErrorTrackingJob("User-Interest", e.getMessage()));

            if(listener != null){
                listener.error();
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
