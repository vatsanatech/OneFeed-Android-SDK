package com.wittyfeed.sdk.onefeed.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public final class Title implements Parcelable
{

    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("background")
    @Expose
    private String background;
    @SerializedName("background_doodle_url")
    @Expose
    private String backgroundDoodleUrl;
    @SerializedName("color")
    @Expose
    private String color;
    public final static Creator<Title> CREATOR = new Creator<Title>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Title createFromParcel(Parcel in) {
            return new Title(in);
        }

        public Title[] newArray(int size) {
            return (new Title[size]);
        }

    }
    ;

    protected Title(Parcel in) {
        this.text = ((String) in.readValue((String.class.getClassLoader())));
        this.background = ((String) in.readValue((String.class.getClassLoader())));
        this.backgroundDoodleUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.color = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Title() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getBackgroundDoodleUrl() {
        return backgroundDoodleUrl;
    }

    public void setBackgroundDoodleUrl(String backgroundDoodleUrl) {
        this.backgroundDoodleUrl = backgroundDoodleUrl;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(text);
        dest.writeValue(background);
        dest.writeValue(backgroundDoodleUrl);
        dest.writeValue(color);
    }

    public int describeContents() {
        return  0;
    }

}
