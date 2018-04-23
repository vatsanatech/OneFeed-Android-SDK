package com.wittyfeed.sdk.onefeed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aishwarydhare on 27/03/18.
 */

public final class OFContentViewActivity extends AppCompatActivity {

    boolean did_load = false;
    private WebView web_view;
    private String urlToOpen;
    private ProgressBar determinateBar;
    private RelativeLayout loaderView_rl;
    private ImageView loader_iv;
    private boolean isLoadedOnce = false;
    private boolean isFromNotification = false;
    private boolean isBeingLoadedInWebview = false;
    private OnBackFromOFWebViewInterface onBackFromOFWebViewInterface;
    private ChromeInstallationStatus chromeStatus;
    private JSONObject jsonObject;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_content_view_wfsdk);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.containsKey("is_loaded_notification")) {
            isFromNotification = true;
        }

        try {
            this.getActionBar().hide();
        } catch (Exception e) {
            // do nothing
        }
        try {
            this.getSupportActionBar().hide();
        } catch (Exception e) {
            // do nothing
        }

        web_view = findViewById(R.id.wv_data);
        determinateBar = findViewById(R.id.determinateBar);
        loaderView_rl = findViewById(R.id.loaderView_rl);
        loader_iv = findViewById(R.id.loader_iv);

        getValuesFromExtras();

        checkGoogleChromeStatus(this);

        setOnBackFromOFWebViewInterface(new OnBackFromOFWebViewInterface() {
            @Override
            public void performBack() {
                customFinishApp();
            }
        });

        if(isFromNotification){
            OFAnalytics.getInstance().sendAnalytics(
                    this,
                    jsonObject.optString("app_id",""),
                    OFAnalytics.AnalyticsCat.WF_Notification,
                    ""
                            + "Opened - "
                            + jsonObject.optString("story_id","")
                            + jsonObject.optString("story_title","")
            );

            OFAnalytics.getInstance().sendAnalytics(
                    this,
                    jsonObject.optString("app_id",""),
                    OFAnalytics.AnalyticsCat.WF_Story,
                    ""
                            + jsonObject.optString("story_title","")
                            + " : "
                            + jsonObject.optString("story_id","")
            );
        }

        if(chromeStatus == ChromeInstallationStatus.ACTIVE){
            try{
                OneFeedMain.getInstance().getContentViewMaker(this.getApplicationContext()).launch(this, urlToOpen );
                Constant.hasChromeCustomTabLoaded = true;
            }catch (Exception e){
                isBeingLoadedInWebview = true;
                initLoaderView(this);
                initWebViewContent();
            }
        }else {
            isBeingLoadedInWebview = true;
            initLoaderView(this);
            initWebViewContent();
        }


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

    private void getValuesFromExtras(){

        Bundle bundle = getIntent().getExtras();
        jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("app_id",""+ bundle.getString("app_id",""));
            jsonObject.putOpt("story_id",""+ bundle.getString("story_id",""));
            jsonObject.putOpt("story_title",""+ bundle.getString("story_title",""));
            jsonObject.putOpt("fcm_token",""+ bundle.getString("fcm_token",""));
            jsonObject.putOpt("url_to_open",""+ bundle.getString("url_to_open",""));

            urlToOpen = bundle.getString("url_to_open","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initLoaderView(Context context) {
        OFGlide
            .with(this)
            .load(R.drawable.one_feed_loader)
            .error(R.drawable.one_feed_loader)
            .into(loader_iv);
    }

    @SuppressLint("SetJavaScriptEnabled")
    void initWebViewContent(){

        web_view.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress)
            {
                super.onProgressChanged(view, progress);
                OFLogger.log(OFLogger.VERBOSE, "WebView Progress: "+progress);
                determinateBar.setProgress(progress);

                if(progress >= 100){
                    determinateBar.setVisibility(View.GONE);
                }

                if (!isLoadedOnce) {
                    if(progress >= Constant.loaderThresholdInt){
                        web_view.setVisibility(View.VISIBLE);
                        loaderView_rl.setVisibility(View.GONE);
                        isLoadedOnce = true;
                    } else {
                        web_view.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });


        OFWebViewConfigManager webViewConfigManager = new OFWebViewConfigManager();
        webViewConfigManager.setUpWebView(urlToOpen, web_view, this);
    }

    private void customFinishApp(){
        if(OFNotificationManager.getInstance().getHomeScreenIntent() != null) {
            this.finish();
            isFromNotification = false;
            this.startActivity(OFNotificationManager.getInstance().getHomeScreenIntent());
        }else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        if(did_load){
            onBackFromOFWebViewInterface.performBack();
            Constant.hasChromeCustomTabLoaded = false;
        }
        did_load = Constant.hasChromeCustomTabLoaded;
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if(isFromNotification && isBeingLoadedInWebview){
            onBackFromOFWebViewInterface.performBack();
        }
        else{
            super.onBackPressed();
        }
    }

    void setOnBackFromOFWebViewInterface(OnBackFromOFWebViewInterface onBackFromOFWebViewInterface) {
        this.onBackFromOFWebViewInterface = onBackFromOFWebViewInterface;
    }

    private enum ChromeInstallationStatus{
        UNAVAILABLE,
        ACTIVE,
        DISABLED
    }

    interface OnBackFromOFWebViewInterface {
        void performBack();
    }
}
