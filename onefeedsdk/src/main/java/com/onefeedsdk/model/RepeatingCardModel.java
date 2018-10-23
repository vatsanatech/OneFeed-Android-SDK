package com.onefeedsdk.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 26-September-2018
 * Time: 12:28
 */
public class RepeatingCardModel {

    @SerializedName("status")
    private boolean status;

    @SerializedName("repeating_data")
    private RepeatingCard repeatingCard;

    public boolean isStatus() {
        return status;
    }

    public RepeatingCard getRepeatingCard() {
        return repeatingCard;
    }

    public class RepeatingCard{
        @SerializedName("cards")
        private List<FeedModel.Card> cardList;

        public List<FeedModel.Card> getCardList() {
            return cardList;
        }
    }

}
