package com.wittyfeed.sdk.onefeed;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aishwarydhare on 22/03/18.
 */

class WittyFeedSDKOneFeedBuilder {

    final private String TAG = "WF_SDK";
    private WittyFeedSDKWebView wittyFeedSDKWebView;
    private Context context;
    private CustomTabsIntent.Builder builder;
    private CustomTabsClient mCustomTabsClient;
    private CustomTabsSession mCustomTabsSession;
    private CustomTabsIntent customTabsIntent;
    private int type = 0;

    private boolean is_customTab_init_successful = false;

    WittyFeedSDKOneFeedBuilder(@NonNull Context para_context, @NonNull int type){
        context = para_context;
        this.type = type;
        init();
    }


    WittyFeedSDKOneFeedBuilder(@NonNull Context para_context, @NonNull int type, JSONObject jsonObject){
        context = para_context;
        this.type = type;
        init();
        build_notification_GA(jsonObject);
    }


    @SuppressLint("ResourceAsColor")
    private void init(){
        builder = new CustomTabsIntent.Builder();

        BitmapFactory.Options bitmap_options = new BitmapFactory.Options();
        bitmap_options.outWidth = 24;
        bitmap_options.outHeight = 24;
        bitmap_options.inScaled = true;


        CustomTabsServiceConnection mCustomTabsServiceConnection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                mCustomTabsClient = customTabsClient;
                mCustomTabsClient.warmup(0);
                mCustomTabsSession = getSession();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mCustomTabsClient = null;
            }
        };


        String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";
        CustomTabsClient.bindCustomTabsService(context.getApplicationContext(), CUSTOM_TAB_PACKAGE_NAME, mCustomTabsServiceConnection);

        int color = ContextCompat.getColor(context, R.color.witty_color);
        try {
            if(!WittyFeedSDKSingleton.getInstance().onefeed_bg_color_string.equalsIgnoreCase("")){
                color = Color.parseColor(""+WittyFeedSDKSingleton.getInstance().onefeed_bg_color_string);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap back_icon_bitmap = getBitmapFromDrawable(context,R.drawable.ic_back);
        try {
            if(WittyFeedSDKSingleton.getInstance().onefeed_back_icon_bitmap != null){
                back_icon_bitmap = WittyFeedSDKSingleton.getInstance().onefeed_back_icon_bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(mCustomTabsSession);

        customTabsIntent = builder.enableUrlBarHiding()
                .setToolbarColor(color)
                .setShowTitle(true)
                .setCloseButtonIcon(back_icon_bitmap)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            customTabsIntent.intent.putExtra(Intent.EXTRA_REFERRER, Uri.parse("android-app://" + context.getPackageName()));
        }

        is_customTab_init_successful = CustomTabsClient.bindCustomTabsService(context.getApplicationContext(), CUSTOM_TAB_PACKAGE_NAME, mCustomTabsServiceConnection);
    }


    public boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    private CustomTabsSession getSession() {
        return mCustomTabsClient.newSession(new CustomTabsCallback() {
            @Override
            public void onNavigationEvent(int navigationEvent, Bundle extras) {
                super.onNavigationEvent(navigationEvent, extras);
            }
        });
    }


    void launch(@NonNull String url_to_open, WebView webView) {
        wittyFeedSDKWebView.setUpWebView(url_to_open, webView, context);
    }


    void launch(@NonNull String url_to_open) {
        Log.d(TAG, "url_to_open: "+ url_to_open);
        try {
            ApplicationInfo ai =
                    context.getPackageManager().getApplicationInfo("com.android.chrome",0);
            boolean appStatus = ai.enabled;


            if(appStatus && is_customTab_init_successful){
                customTabsIntent.intent.setPackage("com.android.chrome");
                try {
                    customTabsIntent.launchUrl(context, Uri.parse(url_to_open));
                }catch (Exception e){
                    launchContentviewActivity(url_to_open);
                }

            } else {
                launchContentviewActivity(url_to_open);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    void launchContentviewActivity(String url_to_open){
        Intent i = new Intent(context,WittyFeedSDKContentViewActivity.class);
        i.putExtra("url_to_open",url_to_open);
        i.putExtra("fallback",true);
        if(this.type == 3){
            i.putExtra("is_loaded_notification",true);
        }
        context.startActivity(i);
    }


    void mayLaunch(@NonNull String url_to_open){
        try {
            Log.d(TAG, "mayLaunch url: "+ url_to_open);
            mCustomTabsSession.mayLaunchUrl(Uri.parse(url_to_open), null, null);
        } catch (Exception e) {
            // do nothing
        }
    }


    private Bitmap getBitmapFromDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable) {
            return getBitmapFromVectorDrawable((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("Unable to convert to bitmap");
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Bitmap getBitmapFromVectorDrawable(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }


    void build_notification_GA(JSONObject jsonObject) {
        String eventCat = "WF NOTIFICATION";
        String eventAction = "";
        String eventLabel = "";
        String fcm_token = "";
        try {
            eventAction = "" + jsonObject.getString("app_id");
            fcm_token = "" + jsonObject.getString("fcm_token");
            eventLabel = ""
                    + "Detail Card - "
                    + jsonObject.getString("story_id")
                    + " : "
                    + jsonObject.getString("story_title");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        send_GA(fcm_token, eventCat, eventLabel, eventAction, context);
    }


    void build_native_story_GA(Card card) {
        String eventCat = "WF Story";
        String eventAction = "";
        String eventLabel = "";
        String fcm_token = "";
        try {
            eventAction = "" + WittyFeedSDKSingleton.getInstance().witty_sdk.wittyFeedSDKApiClient.getAPP_ID();
            fcm_token = WittyFeedSDKSingleton.getInstance().witty_sdk.wittyFeedSDKApiClient.getFCM_TOKEN();
            eventLabel = ""
                    + card.getStoryTitle()
                    + " : "
                    + card.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        send_GA(fcm_token, eventCat, eventLabel, eventAction, context);
    }


    private void send_GA(String fcm_token, String eventCat, String eventAction, String eventLabel, Context context) {

        String eventVal = "1";

        try{
            if (WittyFeedSDKSingleton.getInstance().wittyFeedSDKGoogleAnalytics == null) {
                WittyFeedSDKSingleton.getInstance().wittyFeedSDKGoogleAnalytics = new WittyFeedSDKGoogleAnalytics(context, WittyFeedSDKSingleton.getInstance().getGA_TRACKING_ID(), fcm_token);
            }

            WittyFeedSDKSingleton.getInstance().wittyFeedSDKGoogleAnalytics.send_event_tracking_GA_request(eventCat, eventAction, eventVal, eventLabel);
            Log.d(TAG, "For " + eventCat + " " + eventLabel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    CustomTabsIntent getCustomTabsIntent() {
        return customTabsIntent;
    }

}
