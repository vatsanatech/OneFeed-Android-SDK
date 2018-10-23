package com.onefeedsdk.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 05-October-2018
 * Time: 15:27
 */
public class NotificationModel implements Serializable{

    @SerializedName("action")
    private String action;

    @SerializedName("app_id")
    private String appId;

    @SerializedName("story_title")
    private String storyTitle;

    @SerializedName("amp_url")
    private String ampUrl;

    @SerializedName("id")
    private String id;

    @SerializedName("body")
    private String body;

    @SerializedName("noid")
    private String noId;

    @SerializedName("title")
    private String title;

    @SerializedName("cover_image")
    private String coverImage;

    @SerializedName("story_url")
    private String storyUrl;

    @SerializedName("story_id")
    private String storyId;

    @SerializedName("notiff_agent")
    private String agent;

    public String getAction() {
        return action;
    }

    public String getAppId() {
        return appId;
    }

    public String getStoryTitle() {
        return storyTitle;
    }

    public String getAmpUrl() {
        return ampUrl;
    }

    public String getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public String getNoId() {
        return noId;
    }

    public String getTitle() {
        return title;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public String getStoryUrl() {
        return storyUrl;
    }

    public String getStoryId() {
        return storyId;
    }

    public String getAgent() {
        return agent;
    }
}
