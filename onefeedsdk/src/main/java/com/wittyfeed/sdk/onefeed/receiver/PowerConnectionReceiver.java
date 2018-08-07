package com.wittyfeed.sdk.onefeed.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.Toast;

import com.wittyfeed.sdk.onefeed.ApiClient;
import com.wittyfeed.sdk.onefeed.OFAnalytics;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 28-July-2018
 * Time: 18:03
 */
public class PowerConnectionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction() == Intent.ACTION_POWER_CONNECTED){

        }else if(intent.getAction() == Intent.ACTION_POWER_DISCONNECTED) {

        }
    }
}