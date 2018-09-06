package com.wittyfeed.sdk.demo;

import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationReceivedResult;

import org.json.JSONException;

import java.math.BigInteger;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 01-September-2018
 * Time: 12:48
 */
public class NotificationExtenderExample extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult notification) {
        // Read Properties from result
        OverrideSettings overrideSettings = new OverrideSettings();
        overrideSettings.extender = new NotificationCompat.Extender() {
            @Override
            public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
                // Sets the background notification color to Red on Android 5.0+ devices.
                return builder.setColor(new BigInteger("FFFF0000", 16).intValue());
            }
        };

        OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);
        try {
            Log.d("OneSignalExample", "Notification displayed with id: " +
                    notification.payload.additionalData.getJSONObject("custom").getJSONObject("a").get("H"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Return true to stop the notification from displaying
        return true;
    }
}
