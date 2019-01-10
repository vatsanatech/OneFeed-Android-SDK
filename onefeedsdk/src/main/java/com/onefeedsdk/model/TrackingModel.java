package com.onefeedsdk.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 01-October-2018
 * Time: 12:19
 */
public class TrackingModel {

    @SerializedName("sdkvr")
    private String sdkVersion;

    @SerializedName("lng")
    private String language;

    @SerializedName("cc")
    private String countryCode;

    @SerializedName("pckg")
    private String packageId;

    @SerializedName("device_id")
    private String deviceId;

    @SerializedName("ntype")
    private String networkType;

    @SerializedName("etype")
    private String eventType;

    @SerializedName("appid")
    private String appId;

    @SerializedName("sid")
    private String storyId;

    @SerializedName("rsrc")
    private String resource;

    @SerializedName("noid")
    private String notificationId;

    @SerializedName("appuid")
    private String appUserId;

    @SerializedName("srchstr")
    private String searchString;

    @SerializedName("applist")
    private String appList;

    @SerializedName("newsim")
    private String newSim;

    @SerializedName("os")
    private String os;

    @SerializedName("mode")
    private String mode;

    @SerializedName("uaction")
    private String action;

    @SerializedName("category")
    private String category;

    @SerializedName("ftoken")
    private String token;

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getAppList() {
        return appList;
    }

    public void setAppList(String appList) {
        this.appList = appList;
    }

    public String getNewSim() {
        return newSim;
    }

    public void setNewSim(String newSim) {
        this.newSim = newSim;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(String appUserId) {
        this.appUserId = appUserId;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
}
