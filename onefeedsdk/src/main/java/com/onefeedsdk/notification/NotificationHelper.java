package com.onefeedsdk.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.onefeedsdk.R;
import com.onefeedsdk.app.Constant;
import com.onefeedsdk.app.OneFeedSdk;
import com.onefeedsdk.job.PostUserTrackingJob;
import com.onefeedsdk.model.NotificationModel;
import com.onefeedsdk.ui.NotificationOpenActivity;

import java.util.Map;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 29-August-2018
 * Time: 13:43
 */
public class NotificationHelper {

    private static final String CHANNEL_ID = "DEFAULT";
    private static NotificationManager notificationManager;

    public static void showNotification(Context context, @NonNull Intent intent) {

        // Create an explicit intent for an Activity in your app
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("Onefeed")
                .setContentText("Onefeed Sdk Initialise")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, mBuilder.build());
    }

    public static void sendNotification(final Context context, final Class activity, final Map<String, String> data) {

        Gson gson = new GsonBuilder().create();
        String s = gson.toJson(data);
        final NotificationModel model = gson.fromJson(s, NotificationModel.class);
        if (model.getAgent() != null) {
            if (model.getAgent().equals("wittyfeed_sdk")) {
                notificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                new AsyncTask<String, String, String>() {

                    @Override
                    protected String doInBackground(String... strings) {
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String aVoid) {
                        Glide.with(context)
                                .asBitmap()
                                .load(model.getCoverImage())
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        sendNotification(context, activity, model, resource);
                                    }
                                });
                    }
                }.execute();
            }
        }
    }

    public static void sendNotification(Context context, Class activity, NotificationModel data, Bitmap myBitmap) {

        Intent intent = new Intent(context, NotificationOpenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constant.ACTIVITY, activity);
        intent.putExtra(Constant.MODEL, data);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        OneFeedSdk.getInstance().getJobManager().addJobInBackground(
                new PostUserTrackingJob(Constant.NOTIFICATION_RECEIVED, Constant.NOTIFICATION, data.getStoryId(), data.getNoId()));

        String channelId = "DEFAULT";
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(android.R.drawable.ic_menu_share)
                        .setContentTitle(data.getTitle())
                        .setContentText(data.getBody())
                        .setLargeIcon(myBitmap)
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(myBitmap))
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setContentIntent(pendingIntent);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int noId = 0;
        try{
            noId = Integer.parseInt(data.getNoId());
        }catch (Exception e){
            noId = (int) SystemClock.currentThreadTimeMillis();
        }
        notificationManager.notify(noId/* ID of notification */, notificationBuilder.build());
    }
}
