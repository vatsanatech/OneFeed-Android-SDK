package com.wittyfeed.sdk.onefeed;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by aishwarydhare on 27/03/18.
 */

class WittyFeedSDKWebView {

    private OneFeedJavaScriptInterface oneFeedJavaScriptInterface;
    private WittyFeedSDKOneFeedInterface wittyFeedSDKOneFeedInterface;
    private final String TAG = "WF_SDK";

    @SuppressLint("JavascriptInterface")
    void setUpWebView(String url_to_open, WebView web_view, Context context) {

/*
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            WebView.setWebContentsDebuggingEnabled(true);
//        }
*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            web_view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            web_view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        web_view.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        web_view.setHorizontalScrollBarEnabled(true);

        web_view.getSettings().setAppCacheMaxSize(1024*1024*40);
        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        try {
            web_view.getSettings().setAppCachePath(context.getCacheDir().getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if(WittyFeedSDKSingleton.getInstance().is_load_cache_else_network){
                web_view.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            } else {
                web_view.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            }
        } catch (Exception e) {
            web_view.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            Log.d(TAG, "settingUp with default cache settings", e);
        }
        web_view.getSettings().setAppCacheEnabled(true);
        web_view.getSettings().setAllowFileAccess(true);
        web_view.getSettings().setEnableSmoothTransition(true);

        web_view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("market") || url.startsWith("https://play.google.com")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        Activity host = (Activity) view.getContext();
                        host.startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        // Google Play app is not installed, you may want to open the app store link
                        Uri uri = Uri.parse(url);
                        view.loadUrl("http://play.google.com/store/apps/" + uri.getHost() + "?" + uri.getQuery());
                        return false;
                    }

                }
                else if(url.startsWith("whatsapp")){
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        Activity host = (Activity) view.getContext();
                        host.startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }

                }
                return false;
            }

        });


        oneFeedJavaScriptInterface = new OneFeedJavaScriptInterface(context);

        web_view.addJavascriptInterface(oneFeedJavaScriptInterface, "OneFeedAndroidInterface");

        try {
            web_view.loadUrl(url_to_open);
            Log.d(TAG, "onCreate: url_to_open: " + url_to_open);
            if(web_view.getSettings().getCacheMode() == WebSettings.LOAD_CACHE_ELSE_NETWORK){
                Log.d(TAG, "onCreate: cache method: LOAD_CACHE_ELSE_NETWORK");
            } else {
                Log.d(TAG, "onCreate: cache method: LOAD_DEFAULT");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class OneFeedJavaScriptInterface {
        Context context;

        OneFeedJavaScriptInterface(Context para_context){
            this.context = para_context;
        }

        @JavascriptInterface
        public void goBackToHostApp(){
            if(oneFeedJavaScriptInterface != null){
                wittyFeedSDKOneFeedInterface.goBackToHostApp();
            }
        }

        @JavascriptInterface
        public void showToast(String toast){
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void openAndroidDialog(String title, String message, String positive_text){
            AlertDialog.Builder myDialog
                    = new AlertDialog.Builder(context);
            myDialog.setTitle(title);
            myDialog.setMessage(message);
            myDialog.setPositiveButton(positive_text, null);
            myDialog.show();
        }
    }

    void setWittyFeedSDKOneFeedInterface(WittyFeedSDKOneFeedInterface para_wittyFeedSDKOneFeedInterface) {
        if(para_wittyFeedSDKOneFeedInterface == null){
            Log.e(TAG ,"WittyFeedSDKOneFeedInterface can not be null");
            return;
        }
        this.wittyFeedSDKOneFeedInterface = para_wittyFeedSDKOneFeedInterface;
    }
}
