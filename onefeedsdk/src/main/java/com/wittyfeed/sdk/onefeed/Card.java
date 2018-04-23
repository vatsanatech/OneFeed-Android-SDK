package com.wittyfeed.sdk.onefeed;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

final class Card implements Parcelable
{

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("story_title")
    @Expose
    private String storyTitle;
    @SerializedName("story_url")
    @Expose
    private String storyUrl;
    @SerializedName("cover_image")
    @Expose
    private String coverImage;
    @SerializedName("user_url")
    @Expose
    private String userUrl;
    @SerializedName("user_f_name")
    @Expose
    private String userFullName = "";
    @SerializedName("publisher_name")
    @Expose
    private String publisherName = "";
    @SerializedName("publisher_url")
    @Expose
    private String publisherUrl;
    @SerializedName("publisher_icon_url")
    @Expose
    private String publisherIconUrl;
    @SerializedName("doa")
    @Expose
    private String doa;
    @SerializedName("square_cover_image")
    @Expose
    private String squareCoverImage;
    @SerializedName("property_id")
    @Expose
    private String propertyId;
    @SerializedName("sheild_text")
    @Expose
    private String sheildText;
    @SerializedName("sheild_bg")
    @Expose
    private String sheildBg;
    @SerializedName("badge_text")
    @Expose
    private String badgeText;
    @SerializedName("badge_url")
    @Expose
    private String badgeUrl;
    @SerializedName("card_type")
    @Expose
    private String cardType;
    public final static Creator<Card> CREATOR = new Creator<Card>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        public Card[] newArray(int size) {
            return (new Card[size]);
        }

    }
    ;

    protected Card(Parcel in) {
        this.id = ((int) in.readValue((int.class.getClassLoader())));
        this.storyTitle = ((String) in.readValue((String.class.getClassLoader())));
        this.storyUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.coverImage = ((String) in.readValue((String.class.getClassLoader())));
        this.userUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.userFullName = ((String) in.readValue((String.class.getClassLoader())));
        this.publisherName = ((String) in.readValue((String.class.getClassLoader())));
        this.publisherUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.publisherIconUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.doa = ((String) in.readValue((String.class.getClassLoader())));
        this.squareCoverImage = ((String) in.readValue((String.class.getClassLoader())));
        this.propertyId = ((String) in.readValue((String.class.getClassLoader())));
        this.sheildText = ((String) in.readValue((String.class.getClassLoader())));
        this.sheildBg = ((String) in.readValue((String.class.getClassLoader())));
        this.badgeText = ((String) in.readValue((String.class.getClassLoader())));
        this.badgeUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.cardType = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Card() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStoryTitle() {
        return storyTitle;
    }

    public void setStoryTitle(String storyTitle) {
        this.storyTitle = storyTitle;
    }

    public String getStoryUrl() {
        return storyUrl;
    }

    public void setStoryUrl(String storyUrl) {
        this.storyUrl = storyUrl;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getPublisherUrl() {
        return publisherUrl;
    }

    public void setPublisherUrl(String publisherUrl) {
        this.publisherUrl = publisherUrl;
    }

    public String getPublisherIconUrl() {
        return publisherIconUrl;
    }

    public void setPublisherIconUrl(String publisherIconUrl) {
        this.publisherIconUrl = publisherIconUrl;
    }

    public String getDoa() {
        return doa;
    }

    public void setDoa(String doa) {
        this.doa = doa;
    }

    public String getSquareCoverImage() {
        return squareCoverImage;
    }

    public void setSquareCoverImage(String squareCoverImage) {
        this.squareCoverImage = squareCoverImage;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getSheildText() {
        return sheildText;
    }

    public void setSheildText(String sheildText) {
        this.sheildText = sheildText;
    }

    public String getSheildBg() {
        return sheildBg;
    }

    public void setSheildBg(String sheildBg) {
        this.sheildBg = sheildBg;
    }

    public String getBadgeText() {
        return badgeText;
    }

    public void setBadgeText(String badgeText) {
        this.badgeText = badgeText;
    }

    public String getBadgeUrl() {
        return badgeUrl;
    }

    public void setBadgeUrl(String badgeUrl) {
        this.badgeUrl = badgeUrl;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(storyTitle);
        dest.writeValue(storyUrl);
        dest.writeValue(coverImage);
        dest.writeValue(userUrl);
        dest.writeValue(userFullName);
        dest.writeValue(publisherName);
        dest.writeValue(publisherUrl);
        dest.writeValue(publisherIconUrl);
        dest.writeValue(doa);
        dest.writeValue(squareCoverImage);
        dest.writeValue(propertyId);
        dest.writeValue(sheildText);
        dest.writeValue(sheildBg);
        dest.writeValue(badgeText);
        dest.writeValue(badgeUrl);
        dest.writeValue(cardType);
    }

    public int describeContents() {
        return  0;
    }

}
