package com.wittyfeed.sdk.onefeed;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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

/**
 *
 * Server the content into applicable format available, and does -
 *      1) checks if chrome custom tab is available
 *      2) if available then warms it up
 *      3) opens the content or story in chrome custom tab if available
 *      4) else opens the content or story in ContentViewActivity in WebView
 *
 */

final class ContentViewMaker {

    private ChromeInstallationStatus chromeStatus;
    private CustomTabsIntent.Builder builder;
    private CustomTabsClient mCustomTabsClient;
    private CustomTabsIntent customTabsIntent;
    private boolean is_customTab_init_successful;

    ContentViewMaker(Context context){
        checkGoogleChromeStatus(context);
        init(context);
    }

    private void checkGoogleChromeStatus(Context context) {
        if(isPackageInstalled(context.getPackageManager())){
            try {
                ApplicationInfo ai = context.getPackageManager().getApplicationInfo(Constant.CHROME_PACKAGE_NAME,0);
                if(ai.enabled){
                    chromeStatus = ChromeInstallationStatus.ACTIVE;
                } else {
                    chromeStatus = ChromeInstallationStatus.DISABLED;
                }
            } catch (PackageManager.NameNotFoundException e) {
                chromeStatus = ChromeInstallationStatus.UNAVAILABLE;
            }
        } else {
            chromeStatus = ChromeInstallationStatus.UNAVAILABLE;
        }
    }

    private boolean isPackageInstalled(PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(Constant.CHROME_PACKAGE_NAME, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void init(Context context){
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
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mCustomTabsClient = null;
            }
        };

        is_customTab_init_successful = CustomTabsClient.bindCustomTabsService(context.getApplicationContext(), Constant.CHROME_PACKAGE_NAME, mCustomTabsServiceConnection);

        int color = ContextCompat.getColor(context, R.color.witty_color);
        Bitmap back_icon_bitmap = null;
        try {
            back_icon_bitmap = getBitmapFromDrawable(context, R.drawable.ic_back);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO: 17/04/18 custom_tab_customisation options
        /*try {
            if(!WittyFeedSDKSingleton.getInstance().onefeed_bg_color_string.equalsIgnoreCase("")){
                color = Color.parseColor(""+WittyFeedSDKSingleton.getInstance().onefeed_bg_color_string);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if(WittyFeedSDKSingleton.getInstance().onefeed_back_icon_bitmap != null){
                back_icon_bitmap = WittyFeedSDKSingleton.getInstance().onefeed_back_icon_bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        customTabsIntent = builder.enableUrlBarHiding()
                .setToolbarColor(color)
                .setShowTitle(true)
                .setCloseButtonIcon(back_icon_bitmap)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            customTabsIntent.intent.putExtra(Intent.EXTRA_REFERRER, Uri.parse("android-app://" + context.getPackageName()));
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

    private Bitmap getBitmapFromDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (drawable instanceof VectorDrawable) {
                try {
                    return getBitmapFromVectorDrawable((VectorDrawable) drawable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                OFLogger.log(OFLogger.DEBUG,OFLogger.UnableToConvertToBitmap);
                return null;
            }
        }
        return null;
    }

    private Bitmap getBitmapFromVectorDrawable(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    final void launch(Context context, @NonNull String url_to_open) {
        OFLogger.log(OFLogger.DEBUG, OFLogger.URLToOpen + url_to_open);
        if(chromeStatus == ChromeInstallationStatus.ACTIVE && is_customTab_init_successful) {
            customTabsIntent.intent.setPackage(Constant.CHROME_PACKAGE_NAME);
            customTabsIntent.launchUrl(context, Uri.parse(url_to_open));
        } else {
            launchInContentViewActivity(context, url_to_open);
        }
    }

    private void launchInContentViewActivity(Context context, String url_to_open){
        Intent i = new Intent(context, OFContentViewActivity.class);
        i.putExtra("url_to_open",url_to_open);
        i.putExtra("fallback",true);
        context.startActivity(i);
    }

    private enum ChromeInstallationStatus{
        UNAVAILABLE,
        ACTIVE,
        DISABLED
    }

    /*void mayLaunch(@NonNull String url_to_open){
        try {
            OFLogger.log(OFLogger.DEBUG, "mayLaunch url: "+ url_to_open);
            mCustomTabsSession.mayLaunchUrl(Uri.parse(url_to_open), null, null);
        } catch (Exception e) {
            // do nothing
        }
    }*/

}
