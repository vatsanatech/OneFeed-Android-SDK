package com.wittyfeed.sdk.onefeed;

import android.content.Context;

import com.wittyfeed.sdk.onefeed.Utils.Constant;
import com.wittyfeed.sdk.onefeed.Utils.Utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 <p><span style="font-size: 13pt;"><strong>Manages and keeps hold of Credentials provided by HostApp</strong></span> that will be used through the app for Web API requests, analytics and etc</p>
 <p>Architecture is Singleton</p>
 <p>has following responsibilities -</p>
     <ol>
     <li>gathers user credentials, keep them secure</li>
     <li>get App Id</li>
     <li>get Api Key</li>
     <li>get Unique Identifier</li>
     <li>initialises meta_data of inner class MetaData</li>
     <li>initialises device_meta of inner class DeviceMeta</li>
     </ol>
 */

public final class ApiClient {

    private static final ApiClient ourInstance = new ApiClient();

    private String appId;
    private String apiKey;
    private String uniqueIdentifier;
    private UserMeta userMeta;
    private DeviceMeta deviceMeta;

    private Map<String, String> lazyLoadUserMeta;

    private ApiClient() {
    }

    /**
     * @return singleton instance of {@link ApiClient}
     */
    public static ApiClient getInstance() {
        return ourInstance;
    }

    /**
     * initialising instance with credentials of Host App
     * @param context  context to use for fetching package name
     * @param appId    appId for host's app
     * @param apiKey   apiKey for host's app
     */
    final void init(Context context, String appId, String apiKey){
        this.appId = appId;
        this.apiKey = apiKey;
        this.userMeta = new UserMeta();
        this.deviceMeta = new DeviceMeta(context);
        this.uniqueIdentifier = Utils.getPackageName(context);
    }

    /**
     <p><span style="font-size: 13pt;"><strong>Appends the custom user_meta as provided by the Host App
     into the existing user_meta</strong></span>
     where the existing user_meta has the data which was possible to be automatically generated
     such as - user's locale, timezone and etc<br></p>
     <p>It has lazy load implementation such that - <br>
        <ul>
        <li>if Host App provides the custom user_meta before the one which was automatically generated, then it
            will be saved to be appended later at initialisation</li>
        </ul>
     </p>
     @param customUserMeta  the user meta which user will define
     */
    public final void appendCustomUserMetaToUserMeta(Map<String, String> customUserMeta){
        if(userMeta == null){
            lazyLoadUserMeta = customUserMeta;
        } else {
            userMeta.append_more_user_meta_data(customUserMeta);
        }
    }

    String getAppId() {
        return appId;
    }

    String getApiKey() {
        return apiKey;
    }

    String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    Map<String, String> getUserMeta() {
        return userMeta.getUserMeta();
    }

    Map<String, String> getDeviceMeta() {
        return deviceMeta.getDeviceMeta();
    }

    final String getDeviceId(){
        return getDeviceMeta().get("device_id");
    }


    /**
     * prepares and stores information about user's device configurations and
     * unique IDs for analytical usages
     */
    private final class UserMeta {

        private Map<String, String> userMeta;

        /*
         * private constructor prevents object creation of Singleton class
         */
        UserMeta() {
            userMeta = new HashMap<>();
            userMeta.put("client_locale", Locale.getDefault().getISO3Country());
            userMeta.put("client_locale_language", Locale.getDefault().getISO3Language());
            userMeta.put("device_type", Constant.DEVICE_TYPE);
            userMeta.put("onefeed_sdk_version", Constant.ONE_FEED_VERSION);
            if(lazyLoadUserMeta != null){
                userMeta.putAll(lazyLoadUserMeta);
            }
        }

        /**
         *
         * @param userMetaToAppend user meta that needs to be appended to automatically generated one
         */
        final void append_more_user_meta_data(Map<String, String> userMetaToAppend){
            userMeta.putAll(userMetaToAppend);

        }

        /**
         *
         * @return returns the user meta of the host_app's user
         */
        final Map<String, String> getUserMeta() {
            return userMeta;
        }

    }

    /**
     * prepares and stores information about user's locale as predefined user_meta
     * provides capability to host app to set custom user_meta attributes
     * other then the predefined
     */
    private final class DeviceMeta {

        private Map<String, String> deviceMeta;

        /*
         * private constructor prevents object creation of Singleton class
         */
        DeviceMeta(Context context) {
            deviceMeta = new HashMap<>();
            deviceMeta.put("onefeed_sdk_version", Constant.ONE_FEED_VERSION);
            this.deviceMeta.putAll(Utils.getScreenHeightWidth(context));
            this.deviceMeta.putAll(Utils.getDeviceIdMap(context));
        }

        /**
         * @return dictionary having all the details about the host app's device configurations
         */
        final Map<String, String> getDeviceMeta() {
            return deviceMeta;
        }
    }
}
