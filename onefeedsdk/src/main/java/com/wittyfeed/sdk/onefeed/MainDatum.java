package com.wittyfeed.sdk.onefeed;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

class MainDatum implements Parcelable
{

    @SerializedName("config")
    @Expose
    private OneFeedConfig config;
    @SerializedName("blocks")
    @Expose
    private List<Block> blocks = new ArrayList<Block>();
    public final static Parcelable.Creator<MainDatum> CREATOR = new Creator<MainDatum>() {

        @SuppressWarnings({
            "unchecked"
        })
        public MainDatum createFromParcel(Parcel in) {
            return new MainDatum(in);
        }

        public MainDatum[] newArray(int size) {
            return (new MainDatum[size]);
        }

    }
    ;

    protected MainDatum(Parcel in) {
        this.config = ((OneFeedConfig) in.readValue((OneFeedConfig.class.getClassLoader())));
        in.readList(this.blocks, (Block.class.getClassLoader()));
    }

    public MainDatum() {
    }

    public OneFeedConfig getConfig() {
        return config;
    }

    public void setConfig(OneFeedConfig config) {
        this.config = config;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(config);
        dest.writeList(blocks);
    }

    public int describeContents() {
        return  0;
    }

}
