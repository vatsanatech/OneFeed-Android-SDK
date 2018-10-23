package com.onefeedsdk.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 06-October-2018
 * Time: 18:05
 */
public class TokenUpdateRes {

    @SerializedName("status")
    private boolean status;

    @SerializedName("response")
    private String response;

    public boolean isStatus() {
        return status;
    }

    public String getResponse() {
        return response;
    }
}
