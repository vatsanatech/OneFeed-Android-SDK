package com.onefeedsdk.service;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.onefeedsdk.receiver.CommonReceiver;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 10-October-2018
 * Time: 17:21
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CommonJobService extends JobService {

    BroadcastReceiver receiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        try {
            receiver = new CommonReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_POWER_CONNECTED);
            filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            filter.addAction(Intent.ACTION_HEADSET_PLUG);
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            //Working with api version 24 and above
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                filter.addAction(Intent.ACTION_USER_UNLOCKED);
            }

            registerReceiver(receiver, filter);
        }catch (Exception e){
            Log.e("Exception", e.getMessage());
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // Toast.makeText(this, "Job Service stopped", Toast.LENGTH_SHORT).show();
        try {
            unregisterReceiver(receiver);
        }catch (Exception e){
            Log.e("Exception", e.getMessage());
        }
        return true;
    }
}
