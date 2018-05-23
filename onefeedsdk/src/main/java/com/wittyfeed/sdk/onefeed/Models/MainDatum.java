package com.wittyfeed.sdk.onefeed.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public final class MainDatum implements Parcelable
{

    @SerializedName("config")
    @Expose
    private mConfig config;
    @SerializedName("blocks")
    @Expose
    private List<Block> blocks = new ArrayList<Block>();
    public final static Creator<MainDatum> CREATOR = new Creator<MainDatum>() {

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
        this.config = ((mConfig) in.readValue((mConfig.class.getClassLoader())));
        in.readList(this.blocks, (Block.class.getClassLoader()));
    }

    public MainDatum() {
    }

    public mConfig getConfig() {
        return config;
    }

    public void setConfig(mConfig config) {
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
