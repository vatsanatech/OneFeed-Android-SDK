package com.wittyfeed.sdk.demo;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;
import com.wittyfeed.sdk.onefeed.OFNotificationManager;

import org.w3c.dom.Text;


/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 01-September-2018
 * Time: 12:43
 */
public class Root extends Application {

    public static final String notificationProvider = "F";

    @Override
    public void onCreate() {
        super.onCreate();
        // OneSignal Initialization
        if(!notificationProvider.equalsIgnoreCase("F")) {
            OneSignal.startInit(this)
                    .setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
                    .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                    .unsubscribeWhenNotificationsAreDisabled(true)
                    .init();
        }
    }

    private class ExampleNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
        @Override
        public void notificationReceived(OSNotification notification) {
            String notificationID = notification.payload.notificationID;

            Log.i("OneSignalExample", "NotificationID received: " + notificationID);
            String agent = notification.payload.additionalData.optString("notiff_agent", null);

            if(!TextUtils.isEmpty(agent) && agent.equalsIgnoreCase("wittyfeed_sdk")) {
                OFNotificationManager
                        .getInstance()
                        .handleOneSignalNotification(Root.this, "",
                                notification.payload.additionalData, R.mipmap.ic_launcher, "108");
            }else {
                // Handle by user
            }
        }
    }
}
