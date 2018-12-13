package com.onefeedsdk.job;

import android.content.Context;
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
 * Date: 01-October-2018
 * Time: 13:04
 */
public class PostUserTrackingJob extends BaseJob {

    private final String type;
    private final String resource;

    private TrackingModel model;
    private AddResponseListener listener;

    // Common tracking
    public PostUserTrackingJob(String type, String resource) {
        super(new Params(Priority.HIGH).groupBy("user-tracking"));
        this.type = type;
        this.resource = resource;
        model = new TrackingModel();

        if (Constant.SDK_INITIALISED.equalsIgnoreCase(type)) {
            String oldToken = OneFeedSdk.getInstance().getDefaultAppSharedPreferences().getString(Constant.TOKEN, "");
            model.setToken(oldToken);
        }else if(Constant.SDK_ERROR.equalsIgnoreCase(type)){
            model.setAction("FCM Update");
        }
    }

    //Tracking with story id or Search String
    public PostUserTrackingJob(String type, String resource, String s) {
        super(new Params(Priority.HIGH).groupBy("user-tracking"));
        this.type = type;
        this.resource = resource;

        model = new TrackingModel();
        if (Constant.SEARCH_VIEWED.equalsIgnoreCase(type)) {
            model.setSearchString(s);
        }else {
            model.setStoryId(s);
        }
    }

    //For Notification
    public PostUserTrackingJob(String type, String resource, String storyId, String noId) {
        super(new Params(Priority.HIGH).groupBy("user-tracking"));
        this.type = type;
        this.resource = resource;

        model = new TrackingModel();
        model.setStoryId(storyId);
        model.setNotificationId(noId);
    }

    //Set app list and sim info
    public PostUserTrackingJob(String type, String resource, String data, int id, AddResponseListener listener) {
        super(new Params(Priority.HIGH).groupBy("user-tracking"));
        this.type = type;
        this.resource = resource;

        this.listener = listener;

        //0 for app list and operator
        model = new TrackingModel();
        if (id == 0) {
            model.setAppList(data);
        } else {
            model.setNewSim(data);
        }
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
        try {

            Call<String> call = OneFeedSdk.getInstance().getApiFactory().getTrackingApi().userTracking(model);
            String result = call.execute().body();
            Log.e("Tracking - " + type, result);

            //Only Info and App List
            if (listener != null) {
                listener.success();
            }
        } catch (Exception e) {
            Log.e("Tracking - Error" + type, e.getMessage());
            //Error Tracking
            OneFeedSdk.getInstance().getJobManager().addJobInBackground(new PostErrorTrackingJob(type, e.getMessage()));

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
