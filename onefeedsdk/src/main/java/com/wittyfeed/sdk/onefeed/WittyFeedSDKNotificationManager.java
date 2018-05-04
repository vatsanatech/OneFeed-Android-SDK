package com.wittyfeed.sdk.onefeed;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by aishwarydhare on 24/10/17.
 */

public class WittyFeedSDKNotificationManager {

    private Context application;
    private String FCM_TOKEN = "";
    private String TAG = "WF_SDK";
    private Intent homeScreenIntent;
    private String cover_img_url = "";


    public WittyFeedSDKNotificationManager(Context application, String FCM_TOKEN) {
        this.application = application;
        if (FCM_TOKEN != null) {
            this.FCM_TOKEN = FCM_TOKEN;
        } else {
            this.FCM_TOKEN = "";
        }
    }


    public void handleNotification(Map<String, String> data, int preferred_notiff_icon){
        try {
            if( data.get("notiff_agent") != null){
                if (data.get("notiff_agent").equals("wittyfeed_sdk")) {
                    /*when notification from wittyfeed is recieved we are calling show notification method*/
                    if (data.get("body") != null && data.get("title") != null){
                        Log.d(TAG, "building notificaiton");

                        JSONObject data_jsonObj = new JSONObject(data);

                        try {
                            if(data_jsonObj.optString("card_type").equals("card_type_4")){
                                data_jsonObj.put("card_type","card_type_2");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Intent contentViewIntent;

                        if(data.get("action").equals("WittyFeedSDKContentViewActivity")){
                            contentViewIntent = new Intent(application, WittyFeedSDKContentViewActivity.class);
                        } else {
                            Log.d(TAG, "handleNotification: invalid notification action: DetailCardActivity is deprecated");
                            return;
                        }

                        send_GA_notification_recieved(data_jsonObj);

                        contentViewIntent.putExtra("is_loaded_notification" , true);
                        contentViewIntent.putExtra("fallback", true);
                        contentViewIntent.putExtra("fcm_token" , FCM_TOKEN);
                        contentViewIntent.putExtra("app_id" , data_jsonObj.optString("app_id",""));
                        contentViewIntent.putExtra("story_id" , data_jsonObj.optString("story_id",""));
                        contentViewIntent.putExtra("story_title" , data_jsonObj.optString("story_title",""));
                        contentViewIntent.putExtra("url_to_open" , data_jsonObj.optString("story_url",""));

                        if(data.get("cover_image")!=null)
                            cover_img_url = data.get("cover_image");
                        Log.i(TAG, "Cover Image URL: "+cover_img_url);

                        showNotification(data.get("title"), data.get("body"), preferred_notiff_icon, contentViewIntent);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void send_GA_notification_recieved(JSONObject data_jsonObj){
        // TODO: 26/10/17 GA to send event Notification
        try {
            String eventCat = "WF NOTIFICATION";
            String eventAction = "" + data_jsonObj.getString("app_id");
            String eventVal = "1" ;
            String eventLabel = ""
                    + "Received - "
                    + data_jsonObj.getString("story_id")
                    + " : "
                    + data_jsonObj.getString("story_title");

            if(WittyFeedSDKSingleton.getInstance().wittyFeedSDKGoogleAnalytics == null){
                WittyFeedSDKSingleton.getInstance().wittyFeedSDKGoogleAnalytics = new WittyFeedSDKGoogleAnalytics(application, WittyFeedSDKSingleton.getInstance().getGA_TRACKING_ID(), FCM_TOKEN);
            }

            WittyFeedSDKSingleton.getInstance().wittyFeedSDKGoogleAnalytics.send_event_tracking_GA_request(eventCat, eventAction, eventVal, eventLabel);
            Log.d(TAG, "For Received " + eventCat);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*method for building notification*/
    private void showNotification(final String notification_title, final String notification_body, final int notiff_icon, Intent intent) {
        final NotificationManager notificationManager = (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
        final String CHANNEL_NAME = "Trending Stories";
        final String NOTIFICATION_CHANNEL_ID = "notification_channel_location";
        final int NOTIFICATION_ID = 100;
        int importance = NotificationManager.IMPORTANCE_HIGH;
        try {
            final PendingIntent pendingIntent = PendingIntent.getActivity(
                    application,
                    NOTIFICATION_ID,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

            /*build our notification*/
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                ImageRequest imageRequest = null;
                final Notification.Builder builder = new Notification.Builder(application, NOTIFICATION_CHANNEL_ID);
                imageRequest = new ImageRequest(cover_img_url, new Response.Listener<Bitmap>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(Bitmap response) {

                        /*create notification*/
                        final Notification mNotification = builder.setSmallIcon(notiff_icon)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .setContentTitle(notification_title+"")
                                .setContentText(notification_body)
                                .setChannelId(NOTIFICATION_CHANNEL_ID)
                                .setLargeIcon(BitmapFactory.decodeResource(application.getResources(), notiff_icon))
                                .setStyle(new Notification.BigPictureStyle().bigPicture(response))
                                .build();

                        /*notification flag for cancel notification automatically*/
                        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

                        /*notify the user*/
                        notificationManager.notify(NOTIFICATION_ID, mNotification);


                        Log.d(TAG, "done showing notificaiton");
                        if(!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)) {
                            playNotificationSound();
                        }

                    }
                },0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_4444, new Response.ErrorListener() {
                    @TargetApi(Build.VERSION_CODES.O)
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d(TAG, "onErrorResponse: notification image loading error", error);

                        /*create notification*/
                        final Notification mNotification = builder.setSmallIcon(notiff_icon)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .setContentTitle(notification_title+"")
                                .setContentText(notification_body)
                                .setChannelId(NOTIFICATION_CHANNEL_ID)
                                .setLargeIcon(BitmapFactory.decodeResource(application.getResources(), notiff_icon))
                                .build();

                        /*notification flag for cancel notification automatically*/
                        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

                        /*notify the user*/
                        notificationManager.notify(NOTIFICATION_ID, mNotification);


                        Log.d(TAG, "done showing notificaiton");
                        if(!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)) {
                            playNotificationSound();
                        }
                    }

                });
                Volley.newRequestQueue(application).add(imageRequest);

            } else {
                ImageRequest imageRequest = null;
                final Notification.Builder builder = new Notification.Builder(application);
                imageRequest = new ImageRequest(cover_img_url, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {

                        /*create notification*/
                        final Notification mNotification = builder.setSmallIcon(notiff_icon)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .setContentTitle(notification_title+"")
                                .setContentText(notification_body)
                                .setLargeIcon(BitmapFactory.decodeResource(application.getResources(), notiff_icon))
                                .setStyle(new Notification.BigPictureStyle().bigPicture(response))
                                .build();

                        /*notification flag for cancel notification automatically*/
                        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

                        /*notify the user*/
                        notificationManager.notify(NOTIFICATION_ID, mNotification);


                        Log.d(TAG, "done showing notificaiton");
                        if(!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)) {
                            playNotificationSound();
                        }

                    }
                },0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_4444, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d(TAG, "onErrorResponse: notification image loading error", error);

                        /*create notification*/
                        final Notification mNotification = builder.setSmallIcon(notiff_icon)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .setContentTitle(notification_title+"")
                                .setContentText(notification_body)
                                .setLargeIcon(BitmapFactory.decodeResource(application.getResources(), notiff_icon))
                                .build();

                        /*notification flag for cancel notification automatically*/
                        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

                        /*notify the user*/
                        notificationManager.notify(NOTIFICATION_ID, mNotification);


                        Log.d(TAG, "done showing notificaiton");
                        if(!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)) {
                            playNotificationSound();
                        }
                    }

                });
                Volley.newRequestQueue(application).add(imageRequest);

            }




        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Playing notification sound
    private void playNotificationSound() {
        try {
            final Uri notiffSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(application, notiffSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Clears notification tray messages
    private static void clearNotifications(Context context) {
        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setHomeScreenIntent(Intent homeScreenIntent){
        homeScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.homeScreenIntent = homeScreenIntent;
        WittyFeedSDKSingleton.getInstance().homeActivityIntent = homeScreenIntent;
        String classPassedInIntent = homeScreenIntent.getComponent().getClassName();
        SharedPreferences.Editor prefsEditor;
        if(WittyFeedSDKSingleton.getInstance().editor_sharedPref == null){
            prefsEditor = PreferenceManager.getDefaultSharedPreferences(application).edit();
        } else {
            prefsEditor = WittyFeedSDKSingleton.getInstance().editor_sharedPref;
        }
        prefsEditor.putString("homeScreenIntentClassName", classPassedInIntent).apply();
    }


    public Intent getHomeScreenIntent() {
        return homeScreenIntent;
    }
}
