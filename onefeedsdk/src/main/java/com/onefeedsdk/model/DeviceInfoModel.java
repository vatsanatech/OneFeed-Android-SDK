package com.onefeedsdk.model;

import com.google.gson.annotations.SerializedName;
import com.onefeedsdk.BuildConfig;
import com.onefeedsdk.util.Util;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 16-July-2018
 * Time: 18:57
 */
public class DeviceInfoModel {

    @SerializedName("screen_width")
    private int width = 0;

    @SerializedName("screen_height")
    private int height = 0;

    @SerializedName("device_id")
    private String deviceId = Util.getAndroidUniqueId();

    @SerializedName("android_id")
    private String androidId = Util.getAndroidUniqueId();

    @SerializedName("onefeed_sdk_version")
    private String version = BuildConfig.VERSION_NAME;



}
