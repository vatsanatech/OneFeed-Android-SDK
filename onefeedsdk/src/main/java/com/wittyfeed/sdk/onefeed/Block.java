package com.wittyfeed.sdk.onefeed;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

final class Block implements Parcelable
{

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("cards")
    @Expose
    private List<Card> cards = new ArrayList<Card>();
    public final static Creator<Block> CREATOR = new Creator<Block>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Block createFromParcel(Parcel in) {
            return new Block(in);
        }

        public Block[] newArray(int size) {
            return (new Block[size]);
        }

    }
    ;

    protected Block(Parcel in) {
        this.meta = ((Meta) in.readValue((Meta.class.getClassLoader())));
        in.readList(this.cards, (Card.class.getClassLoader()));
    }

    public Block() {
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(meta);
        dest.writeList(cards);
    }

    public int describeContents() {
        return  0;
    }

}
