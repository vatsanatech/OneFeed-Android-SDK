package com.wittyfeed.sdk.onefeed.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public final class Meta implements Parcelable
{

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("cards_length")
    @Expose
    private int cardsLength;
    @SerializedName("section_order")
    @Expose
    private int sectionOrder;
    @SerializedName("background")
    @Expose
    private String background;
    @SerializedName("background_doodle_url")
    @Expose
    private String backgroundDoodleUrl;
    @SerializedName("title")
    @Expose
    private Title title;

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    @SerializedName("card_id")
    @Expose
    private int cardId;

    public final static Creator<Meta> CREATOR = new Creator<Meta>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Meta createFromParcel(Parcel in) {
            return new Meta(in);
        }

        public Meta[] newArray(int size) {
            return (new Meta[size]);
        }

    }
    ;

    protected Meta(Parcel in) {
        this.id = ((int) in.readValue((int.class.getClassLoader())));
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.cardsLength = ((int) in.readValue((int.class.getClassLoader())));
        this.sectionOrder = ((int) in.readValue((int.class.getClassLoader())));
        this.background = ((String) in.readValue((String.class.getClassLoader())));
        this.backgroundDoodleUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.title = ((Title) in.readValue((Title.class.getClassLoader())));
    }

    public Meta() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCardsLength() {
        return cardsLength;
    }

    public void setCardsLength(int cardsLength) {
        this.cardsLength = cardsLength;
    }

    public int getSectionOrder() {
        return sectionOrder;
    }

    public void setSectionOrder(int sectionOrder) {
        this.sectionOrder = sectionOrder;
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

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(type);
        dest.writeValue(cardsLength);
        dest.writeValue(sectionOrder);
        dest.writeValue(background);
        dest.writeValue(backgroundDoodleUrl);
        dest.writeValue(title);
    }

    public int describeContents() {
        return  0;
    }

}
