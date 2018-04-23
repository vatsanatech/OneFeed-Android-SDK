package com.wittyfeed.sdk.onefeed;

import android.content.Context;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**

 * Manages and keeps hold of Credentials provided by HostApp that will be used through the app for
 *  Web API requests, analytics and etc
 *
 * Architecture is Singleton with Lazy-loading capability
 *
 * has 2 inner classes -
 *      1) UserMeta: prepares and stores information about user's locale as predefined user_meta
 *          provides capability to host app to set custom user_meta attributes
 *           other then the predefined
 *      2) DeviceMeta: prepares and stores information about user's device configurations and
 *          unique IDs for analytical usages
 *
 * performs following actions -
 *      1) gathers user credentials, keep them secure
 *      2) get App Id
 *      3) get Api Key
 *      4) get Unique Identifier
 *      5) initialises meta_data of inner class MetaData
 *      6) initialises device_meta of inner class DeviceMeta
 *
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

    public static ApiClient getInstance() {
        return ourInstance;
    }

    /**
     *
     * @param context  context to use for fetching package name
     * @param appId    appId for host's app
     * @param apiKey   apiKey for host's app
     */
    final synchronized void init(Context context, String appId, String apiKey){
        this.appId = appId;
        this.apiKey = apiKey;
        this.userMeta = new UserMeta();
        this.deviceMeta = new DeviceMeta(context);
        this.uniqueIdentifier = Constant.getPackageName(context);
    }

    /**
     *
     * @param customUserMeta  the user meta which user will define
     */
    public final synchronized void appendCustomUserMetaToUserMeta(Map<String, String> customUserMeta){
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

    private final class UserMeta {

        private Map<String, String> userMeta;

        /**
         *
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

    private final class DeviceMeta {

        private Map<String, String> deviceMeta;

        /**
         *
         * private constructor prevents object creation of Singleton class
         */
        DeviceMeta(Context context) {
            deviceMeta = new HashMap<>();
            deviceMeta.put("onefeed_sdk_version", Constant.ONE_FEED_VERSION);
            this.deviceMeta.putAll(Constant.getScreenHeightWidth(context));
            this.deviceMeta.putAll(Constant.getDeviceIdMap(context));
        }

        /**
         *
         * @return dictionary having all the details about the host app's device configurations
         */
        final Map<String, String> getDeviceMeta() {
            return deviceMeta;
        }
    }
}
