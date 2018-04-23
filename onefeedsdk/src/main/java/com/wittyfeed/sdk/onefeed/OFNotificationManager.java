package com.wittyfeed.sdk.onefeed;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
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
 *
 * Handles the OneFeed Notifications which are received by the host app,
 *  and performs following actions -
 *      1) receives a pendingIntent which if set then opens when the user exits from the
 *          notification content or story opened via OneFeed
 *      2) verify that notification is from OneFeed Server
 *      3) execute notification recieved analytics using OFAnalytics
 *      4) create and show the notification in Notification Tray
 *      5) play notification sound
 *
 */

public final class OFNotificationManager {

    private static final OFNotificationManager ourInstance = new OFNotificationManager();
    private Intent homeScreenIntent;

    private OFNotificationManager() {
    }

    public static OFNotificationManager getInstance() {
        return ourInstance;
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

    public void handleNotification(Context applicationContext, String fcm_token, Map<String, String> data, int preferred_notiff_icon){
        try {
            if( data.get("notiff_agent") != null){
                if (data.get("notiff_agent").equals("wittyfeed_sdk")) {
                    /*when notification from wittyfeed is recieved we are calling show notification method*/
                    if (data.get("body") != null && data.get("title") != null){
                        OFLogger.log(OFLogger.DEBUG, "building notification");

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
                            contentViewIntent = new Intent(applicationContext, OFContentViewActivity.class);
                        } else {
                            OFLogger.log(OFLogger.ERROR, "handleNotification: invalid notification action: DetailCardActivity is deprecated");
                            return;
                        }

                        OFAnalytics.getInstance().sendAnalytics(
                                OFAnalytics.AnalyticsCat.WF_Notification,
                                "Received - " +
                                        data_jsonObj.optString("story_id","") +
                                        data_jsonObj.optString("story_title","")
                        );

                        contentViewIntent.putExtra("is_loaded_notification" , true);
                        contentViewIntent.putExtra("fallback", true);
                        contentViewIntent.putExtra("fcm_token" , fcm_token);
                        contentViewIntent.putExtra("app_id" , data_jsonObj.optString("app_id",""));
                        contentViewIntent.putExtra("story_id" , data_jsonObj.optString("story_id",""));
                        contentViewIntent.putExtra("story_title" , data_jsonObj.optString("story_title",""));
                        contentViewIntent.putExtra("url_to_open" , data_jsonObj.optString("story_url",""));

                        String coverImgUrl = null;
                        if(data.get("cover_image")!=null) {
                            coverImgUrl = data.get("cover_image");
                        }
                        OFLogger.log(OFLogger.DEBUG, "Cover Image URL: " + coverImgUrl);

                        showNotification(applicationContext, data.get("title"), data.get("body"), preferred_notiff_icon, contentViewIntent, coverImgUrl);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNotification(final Context applicationContext, final String notification_title, final String notification_body, final int notiff_icon, Intent intent, String coverImgUrl) {
        final NotificationManager notificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
        final String CHANNEL_NAME = "Trending Stories";
        final String NOTIFICATION_CHANNEL_ID = "notification_channel_location";
        final int NOTIFICATION_ID = 100;
        int importance = NotificationManager.IMPORTANCE_HIGH;
        try {
            final PendingIntent pendingIntent = PendingIntent.getActivity(
                    applicationContext,
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
                final Notification.Builder builder = new Notification.Builder(applicationContext, NOTIFICATION_CHANNEL_ID);
                imageRequest = new ImageRequest(coverImgUrl, new Response.Listener<Bitmap>() {
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
                                .setLargeIcon(BitmapFactory.decodeResource(applicationContext.getResources(), notiff_icon))
                                .setStyle(new Notification.BigPictureStyle().bigPicture(response))
                                .build();

                        /*notification flag for cancel notification automatically*/
                        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

                        /*notify the user*/
                        notificationManager.notify(NOTIFICATION_ID, mNotification);


                        OFLogger.log(OFLogger.DEBUG, "done showing notificaiton");
                        if(!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)) {
                            playNotificationSound(applicationContext);
                        }

                    }
                },0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_4444,

                        new Response.ErrorListener() {
                            @TargetApi(Build.VERSION_CODES.O)
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                OFLogger.log(OFLogger.DEBUG, "onErrorResponse: notification image loading error", error);

                                /*create notification*/
                                final Notification mNotification = builder.setSmallIcon(notiff_icon)
                                        .setAutoCancel(true)
                                        .setContentIntent(pendingIntent)
                                        .setContentTitle(notification_title+"")
                                        .setContentText(notification_body)
                                        .setChannelId(NOTIFICATION_CHANNEL_ID)
                                        .setLargeIcon(BitmapFactory.decodeResource(applicationContext.getResources(), notiff_icon))
                                        .build();

                                /*notification flag for cancel notification automatically*/
                                mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

                                /*notify the user*/
                                notificationManager.notify(NOTIFICATION_ID, mNotification);


                                OFLogger.log(OFLogger.DEBUG, "done showing notificaiton");
                                if(!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)) {
                                    playNotificationSound(applicationContext);
                                }
                            }

                        });
                Volley.newRequestQueue(applicationContext).add(imageRequest);

            } else {
                ImageRequest imageRequest = null;
                final Notification.Builder builder = new Notification.Builder(applicationContext);
                imageRequest = new ImageRequest(coverImgUrl, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {

                        /*create notification*/
                        final Notification mNotification = builder.setSmallIcon(notiff_icon)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .setContentTitle(notification_title+"")
                                .setContentText(notification_body)
                                .setLargeIcon(BitmapFactory.decodeResource(applicationContext.getResources(), notiff_icon))
                                .setStyle(new Notification.BigPictureStyle().bigPicture(response))
                                .build();

                        /*notification flag for cancel notification automatically*/
                        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

                        /*notify the user*/
                        notificationManager.notify(NOTIFICATION_ID, mNotification);


                        OFLogger.log(OFLogger.DEBUG, "done showing notificaiton");
                        if(!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)) {
                            playNotificationSound(applicationContext);
                        }

                    }
                },0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_4444,

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                OFLogger.log(OFLogger.DEBUG, "onErrorResponse: notification image loading error", error);

                                /*create notification*/
                                final Notification mNotification = builder.setSmallIcon(notiff_icon)
                                        .setAutoCancel(true)
                                        .setContentIntent(pendingIntent)
                                        .setContentTitle(notification_title+"")
                                        .setContentText(notification_body)
                                        .setLargeIcon(BitmapFactory.decodeResource(applicationContext.getResources(), notiff_icon))
                                        .build();

                                /*notification flag for cancel notification automatically*/
                                mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

                                /*notify the user*/
                                notificationManager.notify(NOTIFICATION_ID, mNotification);


                                OFLogger.log(OFLogger.DEBUG,  "done showing notificaiton");
                                if(!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)) {
                                    playNotificationSound(applicationContext);
                                }
                            }

                        });
                Volley.newRequestQueue(applicationContext).add(imageRequest);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playNotificationSound(Context applicationContext) {
        try {
            final Uri notiffSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(applicationContext, notiffSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Intent getHomeScreenIntent() {
        return homeScreenIntent;
    }

    public void setHomeScreenIntent(Intent homeScreenIntent) {
        homeScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.homeScreenIntent = homeScreenIntent;
    }
}
