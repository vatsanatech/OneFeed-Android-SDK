package com.onefeedsdk.model;

import com.google.gson.annotations.SerializedName;
import com.onefeedsdk.BuildConfig;

import java.util.Locale;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 16-July-2018
 * Time: 18:30
 */

public class UserInfoModel {

    @SerializedName("device_type")
    private String deviceType = "android";

    @SerializedName("client_gender")
    private String gender;

    @SerializedName("onefeed_sdk_version")
    private String version = BuildConfig.VERSION_NAME;

    @SerializedName("client_locale")
    private String locale = Locale.getDefault().getISO3Country();

    @SerializedName("client_locale_language")
    private String language = Locale.getDefault().getISO3Language();

    @SerializedName("client_interests")
    private String interests;

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getGender() {
        return gender;
    }

    public String getVersion() {
        return version;
    }

    public String getLocale() {
        return locale;
    }

    public String getLanguage() {
        return language;
    }

    public String getInterests() {
        return interests;
    }
}
