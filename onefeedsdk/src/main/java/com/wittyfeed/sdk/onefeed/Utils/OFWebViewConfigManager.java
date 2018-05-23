package com.wittyfeed.sdk.onefeed.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public final class OFWebViewConfigManager {

    @SuppressLint("JavascriptInterface")
    public void setUpWebView(String url_to_open, WebView web_view, Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

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
        web_view.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
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

        try {
            web_view.loadUrl(url_to_open);
            OFLogger.log(OFLogger.DEBUG, "onCreate: url_to_open: " + url_to_open);
            if(web_view.getSettings().getCacheMode() == WebSettings.LOAD_CACHE_ELSE_NETWORK){
                OFLogger.log(OFLogger.DEBUG, "onCreate: cache method: LOAD_CACHE_ELSE_NETWORK");
            } else {
                OFLogger.log(OFLogger.DEBUG, "onCreate: cache method: LOAD_DEFAULT");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
