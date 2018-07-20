package com.wittyfeed.sdk.onefeed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.wittyfeed.sdk.onefeed.Utils.Constant;
import com.wittyfeed.sdk.onefeed.Utils.OFGlide;
import com.wittyfeed.sdk.onefeed.Utils.OFLogger;
import com.wittyfeed.sdk.onefeed.Utils.OFWebViewConfigManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aishwarydhare on 27/03/18.
 */

/**
 <p><span style="font-size: 13pt;"><strong>FallBack class which uses WebView incase Chrome is not installed or disabled</strong></span></p>
 <p>Checks for the availability of chrome</p>
 <p>Creates and Configures webview and loads it</p>
 */
public final class OFContentViewActivity extends AppCompatActivity {

    boolean did_load = false;
    private WebView web_view;
    private String urlToOpen, notificationId;
    private ProgressBar determinateBar;
    private RelativeLayout loaderView_rl;
    private ImageView loader_iv;
    private boolean isLoadedOnce = false;
    private boolean isFromNotification = false;
    private boolean isBeingLoadedInWebview = false;
    private OnBackFromOFWebViewInterface onBackFromOFWebViewInterface;
    private ChromeInstallationStatus chromeStatus;
    private JSONObject jsonObject;

    @Override
    protected void onResume() {
        if(did_load){
            onBackFromOFWebViewInterface.performBack();
            Constant.hasChromeCustomTabLoaded = false;
        }
        did_load = Constant.hasChromeCustomTabLoaded;
        super.onResume();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_content_view_wfsdk);
        Bundle bundle = getIntent().getExtras();
        init(bundle);
     }

     private void init(Bundle bundle){


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
                     OFAnalytics.AnalyticsType.NotificationOpened,
                     ""
                             + ""
                             + jsonObject.optString("story_id","")
                             + ":"
                             + jsonObject.optString("notification_id","")
             );

//            OFAnalytics.getInstance().sendAnalytics(
//                    this,
//                    jsonObject.optString("app_id",""),
//                    OFAnalytics.AnalyticsType.Story,
//                    ""
//                            + jsonObject.optString("story_id","")
//                            + ":"
//                            + "notification"
//            );
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

    /**
     * Checks if chrome is disabled
     */

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

    /**
     * Checks if chrome is installed in the device
     * @param packageManager Object of class PackageManager used for retrieving various kinds of information related to the application packages that are currently installed on the device
     */

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

            notificationId = bundle.containsKey("notification_id")? bundle.getString("notification_id") : bundle.getString("id", "");
            jsonObject.putOpt("notification_id",notificationId );

            urlToOpen = bundle.containsKey("url_to_open")? bundle.getString("url_to_open") : bundle.getString("story_url", "");
            jsonObject.putOpt("url_to_open",urlToOpen);

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

    private void customFinishApp(){
        if(OFNotificationManager.getInstance().getHomeScreenIntent() != null) {
            this.finish();
            isFromNotification = false;
            this.startActivity(OFNotificationManager.getInstance().getHomeScreenIntent());
        }else {
            if(!PreferenceManager.getDefaultSharedPreferences(this).getString(Constant.SAVED_HOME_SCREEN_CLASS,"").isEmpty()){
                try {
                    Class<?> classNameToOpen = Class.forName(PreferenceManager.getDefaultSharedPreferences(this).getString(Constant.SAVED_HOME_SCREEN_CLASS,""));
                    this.startActivity(new Intent(this, classNameToOpen));
                    finish();
                } catch (ClassNotFoundException e) {
                    OFLogger.log(OFLogger.DEBUG, OFLogger.invalidClassInHomeActivityIntent);
                    finish();
                }
            }
        }
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

    void setOnBackFromOFWebViewInterface(OnBackFromOFWebViewInterface onBackFromOFWebViewInterface) {
        this.onBackFromOFWebViewInterface = onBackFromOFWebViewInterface;
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

    private enum ChromeInstallationStatus{
        UNAVAILABLE,
        ACTIVE,
        DISABLED
    }

    interface OnBackFromOFWebViewInterface {
        void performBack();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle bundle = intent.getExtras();
        init(bundle);
        finish();

    }
}
