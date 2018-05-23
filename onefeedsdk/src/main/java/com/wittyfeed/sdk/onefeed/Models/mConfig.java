package com.wittyfeed.sdk.onefeed.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public final class mConfig implements Parcelable
{

    @SerializedName("app_id")
    @Expose
    private String appId;
    @SerializedName("data_expire_data")
    @Expose
    private String dataExpireData;
    @SerializedName("user_id")
    @Expose
    private String user_id;
    public final static Creator<mConfig> CREATOR = new Creator<mConfig>() {

        @SuppressWarnings({
            "unchecked"
        })
        public mConfig createFromParcel(Parcel in) {
            return new mConfig(in);
        }

        public mConfig[] newArray(int size) {
            return (new mConfig[size]);
        }

    }
    ;

    protected mConfig(Parcel in) {
        this.appId = ((String) in.readValue((String.class.getClassLoader())));
        this.dataExpireData = ((String) in.readValue((String.class.getClassLoader())));
        this.user_id = ((String) in.readValue((String.class.getClassLoader())));
    }

    public mConfig() {
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getDataExpireData() {
        return dataExpireData;
    }

    public void setDataExpireData(String dataExpireData) {
        this.dataExpireData = dataExpireData;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(appId);
        dest.writeValue(dataExpireData);
        dest.writeValue(user_id);
    }

    public int describeContents() {
        return  0;
    }

}
