package com.onefeedsdk.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.onefeedsdk.job.GetHomeFeedJob;
import com.onefeedsdk.job.GetRepeatingCardJob;
import com.onefeedsdk.job.GetSearchFeedJob;
import com.onefeedsdk.job.PostErrorTrackingJob;
import com.onefeedsdk.job.PostTokenUpdateJob;
import com.onefeedsdk.job.PostUserInterestsJob;
import com.onefeedsdk.job.PostUserTrackingJob;
import com.onefeedsdk.listener.AddResponseListener;
import com.onefeedsdk.listener.CallBackListener;
import com.onefeedsdk.receiver.CommonReceiver;
import com.onefeedsdk.service.CommonJobService;
import com.onefeedsdk.rest.ApiFactory;
import com.onefeedsdk.service.CommonService;
import com.onefeedsdk.util.LogFactory;
import com.onefeedsdk.util.Util;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 17-July-2018
 * Time: 11:13
 */

public class OneFeedSdk {

    private static final String PREF_DEFAULT = "share-app-pref";
    public final String VERSION = "2.3.21";
    public static final String WATER_FALL = "Waterfall";
    public static final String H_List = "H-List";
    public static final String V_List = "V-List";
    public static final String CUSTOM = "Custom";
    public static final String GRID = "Grid";
    private String customTopicParameter = "";

    //Logger
    private static LogFactory.Log log = LogFactory.getLog(OneFeedSdk.class);

    private static OneFeedSdk oneFeedSdk;

    private String appId = "";
    private String cardId = "";
    private String appKey = "";

    private Context context;
    private EventBus eventBus;
    private ApiFactory apiFactory;
    private JobManager jobManager;
    private SharedPreferences defaultAppSharedPreferences;

    //Initialize OneFeed and return object
    public static OneFeedSdk getInstance() {
        if (oneFeedSdk == null) {
            oneFeedSdk = new OneFeedSdk();
        }
        return oneFeedSdk;
    }

    //Initialize other require source
    public void init(Context context) {
        this.context = context;
        fetchAppId();
        fetchApiKey();
        fetchCardId();
        initEventBus();
        initRest();
        initRuntimeStore();
        initJobManager();
        initDefaultSharedPreference();

        //Tracking
        startService();
        getInstallAppInfo();
        Util.getPhoneDetail(context);
        initializeSdk();
    }

    //Card initialize
    public void initNativeCard(int cardId, CallBackListener listener) {
        GetRepeatingCardJob repeatingCardJob = new GetRepeatingCardJob(0, cardId);
        repeatingCardJob.setListener(listener);
        OneFeedSdk.getInstance().getJobManager().addJobInBackground(repeatingCardJob);
    }

    //Card initialize
    public void initNativeCard(int cardId) {
        GetRepeatingCardJob repeatingCardJob = new GetRepeatingCardJob(0, cardId);
        OneFeedSdk.getInstance().getJobManager().addJobInBackground(repeatingCardJob);
    }

    private void fetchAppId() {
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            appId = bundle.getString("com.onefeed.sdk.AppId");
        } catch (PackageManager.NameNotFoundException e) {
            log.debug("Unable to load meta-data, App Id not found : " + e.getMessage());
        } catch (NullPointerException e) {
            log.debug("Unable to load meta-data, App Id not found : " + e.getMessage());
        }
    }

    private void fetchCardId() {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            cardId = bundle.getString("com.onefeed.sdk.CardId");
        } catch (PackageManager.NameNotFoundException e) {
            log.debug("Unable to load meta-data, Card Id not found : " + e.getMessage());
        } catch (NullPointerException e) {
            log.debug("Unable to load meta-data, Card Id not found : " + e.getMessage());
        }
    }

    private void fetchApiKey() {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            appKey = bundle.getString("com.onefeed.sdk.ApiKey");
        } catch (PackageManager.NameNotFoundException e) {
            log.debug("Unable to load meta-data, App key not found : " + e.getMessage());
        } catch (NullPointerException e) {
            log.debug("Unable to load meta-data, App key not found : " + e.getMessage());
        }
    }

    public Context getContext() {
        return context;
    }

    private void initRest() {
        this.apiFactory = new ApiFactory();
    }

    private void initJobManager() {
        Configuration configuration = new Configuration.Builder(context)
                .minConsumerCount(1)
                .maxConsumerCount(3)
                .loadFactor(3)
                .consumerKeepAlive(120)
                .build();
        jobManager = new JobManager(configuration);
    }

    private void initRuntimeStore() {
        //Runtime Storage Initialise
        RuntimeStore.init();
    }

    private void initDefaultSharedPreference() {
        defaultAppSharedPreferences = context.getSharedPreferences(PREF_DEFAULT, Context.MODE_PRIVATE);
    }

    private void initEventBus() {
        this.eventBus = EventBus.builder()
                .logNoSubscriberMessages(false)
                .sendNoSubscriberEvent(false)
                .build();
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public SharedPreferences getDefaultAppSharedPreferences() {
        return defaultAppSharedPreferences;
    }

    public ApiFactory getApiFactory() {
        return apiFactory;
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getAppId() {
        return appId;
    }

    public int getCardId() {
        try {
            return Integer.parseInt(cardId);
        } catch (Exception e) {
            return 0;
        }
    }

    //Token Update
    public void setToken(final String newToken) {
        try {

            //Topic subscribe if successful api run
            AddResponseListener responseListener = new AddResponseListener() {
                @Override
                public void success() {
                    OneFeedSdk.getInstance().saveToken(newToken);
                    Util.setPrefValue(Constant.TOPIC, getSubscribeTopic());
                }

                @Override
                public void error() {
                }
            };

            String oldToken = OneFeedSdk.getInstance().getDefaultAppSharedPreferences().getString(Constant.TOKEN, "");
            if (!oldToken.equalsIgnoreCase(newToken)) {
                OneFeedSdk.getInstance().getJobManager().addJobInBackground(new PostTokenUpdateJob(newToken, responseListener));
            } else if (!getSubscribeTopic().equalsIgnoreCase(getOldTopicSubscribe())) {
                OneFeedSdk.getInstance().getJobManager().addJobInBackground(new PostTokenUpdateJob(newToken, responseListener));
            }
        } catch (Exception e) {
        }
    }

    public void saveToken(String newToken) {
        Util.setPrefValue(Constant.TOKEN, newToken);
    }

    public String getSubscribeTopic() {
        return "OneFeed_" + appId + "_" + getCustomTopic() + VERSION;
    }

    public void setTopicSubscription() {
        //Util.setPrefValue(Constant.TOPIC, getSubscribeTopic());
    }

    public String getOldTopicSubscribe() {
        return OneFeedSdk.getInstance().getDefaultAppSharedPreferences().getString(Constant.TOPIC, "");
    }

    public String getCustomTopic() {
        return customTopicParameter;
    }

    public void setCustomTopic(String customTopic) {
        this.customTopicParameter = customTopic;
    }

    private void initializeSdk() {

        //Initialize feed
        OneFeedSdk.getInstance().getJobManager().addJobInBackground(new GetHomeFeedJob(false, 0));

        //Tracking
        OneFeedSdk.getInstance().getJobManager().addJobInBackground(
                new PostUserTrackingJob(Constant.SDK_INITIALISED, Constant.APP_INIT));
    }

    private void startService() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            try {
                context.startService(new Intent(context, CommonService.class));
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }
        } else {
            context.sendBroadcast(new Intent(context, CommonReceiver.class));
        }
    }

    //Search Api for user
    public void searchStoriesByKeyword(@NonNull String keyword, @NonNull AddResponseListener listener) {
        OneFeedSdk.getInstance().jobManager
                .addJobInBackground(new GetSearchFeedJob(keyword, listener));
    }


    //Call Api for taking User Interest
    public void setUserInterests(String category, String userAction, String token, @NonNull AddResponseListener listener) {

        OneFeedSdk.getInstance().jobManager
                .addJobInBackground(new PostUserInterestsJob(Constant.USER_INTERESTS, userAction, category, token, listener));
    }

    private void getInstallAppInfo() {
        try {
            final List<ApplicationInfo> list = context.getPackageManager().getInstalledApplications
                    (PackageManager.GET_META_DATA);

            ArrayList<String> installAppInfoList = new ArrayList<String>();
            StringBuilder appList = new StringBuilder();
            for (ApplicationInfo info : list) {
                try {
                    if (null != context.getPackageManager().getLaunchIntentForPackage(info.packageName)) {

                        String appName = (String) info.loadLabel(context.getPackageManager());
                        // String packageName = (String) info.loadLabel(context.getPackageManager());
                        installAppInfoList.add(appName /*+ " - " + packageName*/);
                        if (installAppInfoList.size() == 1) {
                            appList = new StringBuilder(appName);
                        } else {
                            appList.append(",").append(appName);
                        }
                    }
                } catch (Exception e) {
                    Log.e("Exception", e.getMessage());
                }
            }
            try {
                final int count = OneFeedSdk.getInstance().defaultAppSharedPreferences
                        .getInt(Constant.APP_COUNT, 0);

                if (count >= 0 && count != list.size()) {
                    appList = new StringBuilder(appList.toString().replace("'", ""));
                    OneFeedSdk.getInstance().getJobManager().addJobInBackground(
                            new PostUserTrackingJob(Constant.APP_LIST, Constant.RSRC, appList.toString(), 0, new AddResponseListener() {
                                @Override
                                public void success() {
                                    SharedPreferences.Editor editor = OneFeedSdk.getInstance().getDefaultAppSharedPreferences()
                                            .edit();
                                    editor.putInt(Constant.APP_COUNT, list.size()).apply();
                                    editor.commit();
                                }

                                @Override
                                public void error() {

                                }
                            })
                    );
                }
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }
        } catch (Exception e) {
            //Error Tracking
            OneFeedSdk.getInstance().getJobManager()
                    .addJobInBackground(new PostErrorTrackingJob("OneFeedSdk-InstallAppInfo", e.getMessage()));
        }
    }
}
