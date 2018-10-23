package com.onefeedsdk.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 06-October-2018
 * Time: 11:22
 */
public class TokenUpdateModel {

    @SerializedName("app_id")
    private String appId;

    @SerializedName("firebase_token")
    private String newToken;

    @SerializedName("old_firebase_token")
    private String oldToken;

    @SerializedName("onefeed_sdk_version")
    private String sdkVersion;

    @SerializedName("device_id")
    private String deviceID;

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setNewToken(String newToken) {
        this.newToken = newToken;
    }

    public void setOldToken(String oldToken) {
        this.oldToken = oldToken;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
}
