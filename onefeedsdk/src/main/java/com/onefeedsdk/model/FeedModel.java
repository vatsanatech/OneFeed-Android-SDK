package com.onefeedsdk.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 13-September-2018
 * Time: 16:29
 */
public class FeedModel implements Serializable{

    @SerializedName("status")
    private boolean status;

    @SerializedName("response")
    private boolean response;

    @SerializedName("feed_data")
    private FeedData feedData;

    @SerializedName("search_data")
    private FeedData searchData;

    @SerializedName("non_repeating_data")
    private FeedData nonRepData;

    public boolean isStatus() {
        return status;
    }

    public boolean isResponse() {
        return response;
    }

    public FeedData getFeedData() {
        return feedData;
    }

    public FeedData getSearchData() {
        return searchData;
    }

    public FeedData getNonRepData() {
        return nonRepData;
    }

    public class FeedData implements Serializable{
        @SerializedName("config")
        private Config config;

        public Config getConfig() {
            return config;
        }

        @SerializedName("blocks")
        private List<Blocks> blocks;

        public List<Blocks> getBlocks() {
            return blocks;
        }
    }

    public class Blocks implements Serializable{

        @SerializedName("meta")
        private Meta meta;

        @SerializedName("cards")
        private List<Card> cardList;

        public Meta getMeta() {
            return meta;
        }

        public List<Card> getCardList() {
            return cardList;
        }
    }

    public class Config implements Serializable{

        @SerializedName("app_id")
        private String appId;

        @SerializedName("user_id")
        private String userId;

        @SerializedName("data_expire_data")
        private String expireData;

        public String getAppId() {
            return appId;
        }

        public String getUserId() {
            return userId;
        }

        public String getExpireData() {
            return expireData;
        }
    }

    public class Meta implements Serializable{

        @SerializedName("id")
        private String id;

        @SerializedName("type")
        private String type;

        @SerializedName("cards_length")
        private int cardLength;

        @SerializedName("section_order")
        private int sectionOrder;

        @SerializedName("background")
        private String background;

        @SerializedName("background_doodle_url")
        private String backgroundDoodleUrl;

        @SerializedName("title")
        private Title title;

        public String getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public int getCardLength() {
            return cardLength;
        }

        public int getSectionOrder() {
            return sectionOrder;
        }

        public String getBackground() {
            return background;
        }

        public String getBackgroundDoodleUrl() {
            return backgroundDoodleUrl;
        }

        public Title getTitle() {
            return title;
        }
    }

    public class Title implements Serializable{

        @SerializedName("text")
        private String text;

        @SerializedName("background")
        private String background;

        @SerializedName("background_doodle_url")
        private String backgroundDoodleUrl;

        @SerializedName("color")
        private String color;

        public String getText() {
            return text;
        }

        public String getBackground() {
            return background;
        }

        public String getBackgroundDoodleUrl() {
            return backgroundDoodleUrl;
        }

        public String getColor() {
            return color;
        }
    }

    public class Card implements Serializable{

        @SerializedName("story_id")
        private String storyId;

        @SerializedName("story_slug")
        private String storySlug;

        @SerializedName("story_title")
        private String storyTitle;

        @SerializedName("cover_image")
        private String coverImage;

        @SerializedName("user_id")
        private String userId;

        @SerializedName("user_f_name")
        private String fName;

        @SerializedName("user_full_name_alias")
        private String username;

        @SerializedName("date_of_action")
        private String dateOfAction;

        @SerializedName("square_cover_image")
        private String squareImage;

        @SerializedName("property_id")
        private String propertyId;

        @SerializedName("cat_id")
        private String categoryId;

        @SerializedName("sheild_text")
        private String sheildText;

        @SerializedName("google_filtered")
        private String googleFiltered;

        @SerializedName("id")
        private String id;

        @SerializedName("story_url")
        private String storyUrl;

        @SerializedName("doa")
        private String doa;

        @SerializedName("publisher_name")
        private String publisherName;

        @SerializedName("publisher_url")
        private String publisherUrl;

        @SerializedName("publisher_icon_url")
        private String publisherIconUrl;

        @SerializedName("sheild_bg")
        private String sheildBg;

        @SerializedName("badge_text")
        private String badgeText;

        @SerializedName("badge_url")
        private String badgeUrl;

        @SerializedName("card_type")
        private String cardType;

        public String getStoryId() {
            return storyId;
        }

        public String getStorySlug() {
            return storySlug;
        }

        public String getStoryTitle() {
            return storyTitle;
        }

        public String getCoverImage() {
            return coverImage;
        }

        public String getUserId() {
            return userId;
        }

        public String getfName() {
            return fName;
        }

        public String getUsername() {
            return username;
        }

        public String getDateOfAction() {
            return dateOfAction;
        }

        public String getSquareImage() {
            return squareImage;
        }

        public String getPropertyId() {
            return propertyId;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public String getSheildText() {
            return sheildText;
        }

        public String getGoogleFiltered() {
            return googleFiltered;
        }

        public String getId() {
            return id;
        }

        public String getStoryUrl() {
            return storyUrl;
        }

        public String getDoa() {
            return doa;
        }

        public String getPublisherName() {
            return publisherName;
        }

        public String getPublisherUrl() {
            return publisherUrl;
        }

        public String getPublisherIconUrl() {
            return publisherIconUrl;
        }

        public String getSheildBg() {
            return sheildBg;
        }

        public String getBadgeText() {
            return badgeText;
        }

        public String getBadgeUrl() {
            return badgeUrl;
        }

        public String getCardType() {
            return cardType;
        }
    }
}
