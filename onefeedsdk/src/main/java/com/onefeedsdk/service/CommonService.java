package com.onefeedsdk.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.onefeedsdk.receiver.CommonReceiver;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 10-October-2018
 * Time: 17:20
 */
public class CommonService extends Service {

    private static String TAG = "MyService";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        startJobService();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("onStartCommand", "onStartCommand");
        startJobService();
        return START_STICKY;
    }

    private void startJobService() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                JobScheduler js = (JobScheduler) this.getSystemService(
                        Context.JOB_SCHEDULER_SERVICE);

                JobInfo job = new JobInfo.Builder(
                        0, new ComponentName(this, CommonJobService.class))
                        .setRequiresCharging(true)
                        .setOverrideDeadline(1)
                        .build();
                js.schedule(job);
            } else {
            }
        }catch (Exception e){
            Log.e("Exception", e.getMessage());
        }
    }
}
