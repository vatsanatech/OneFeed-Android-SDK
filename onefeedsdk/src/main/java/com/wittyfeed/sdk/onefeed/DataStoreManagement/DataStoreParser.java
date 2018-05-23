package com.wittyfeed.sdk.onefeed.DataStoreManagement;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wittyfeed.sdk.onefeed.Utils.OFLogger;
import com.wittyfeed.sdk.onefeed.Models.MainDatum;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * Parses the raw-data JSON string into Model-Data using GSON conversion
 *  and corresponding POJO classes
 *
 */

public final class DataStoreParser {

    /**
     * Parses main Feed JSON
     */
    public synchronized static MainDatum parseMainFeedString(@NonNull String rawDataStr) {
        MainDatum temp_mainDatum = null;
        try {
            JSONObject jsonObject = new JSONObject(rawDataStr);
            if(!jsonObject.optBoolean("status")){
                return null;
            } else {
                temp_mainDatum = parseDataStr(jsonObject.optJSONObject("feed_data").toString());
            }
        } catch (JSONException e) {
            OFLogger.log(OFLogger.ERROR, OFLogger.DataParseError);
        }
        return temp_mainDatum;
    }

    /**
     * Parses Generic feed string
     */
    public synchronized static MainDatum parseGenericFeedString(@NonNull String rawDataStr) {
        MainDatum temp_mainDatum = null;
        try {
            JSONObject jsonObject = new JSONObject(rawDataStr);
            if(!jsonObject.optBoolean("status")){
                return null;
            } else {
                temp_mainDatum = parseDataStr(jsonObject.optJSONObject("feed_data").toString());
            }
        } catch (JSONException e) {
            OFLogger.log(OFLogger.ERROR, OFLogger.DataParseError);
        }
        return temp_mainDatum;
    }

    /**
     * Parses Default Search String
     */

    public synchronized static MainDatum parseSearchDefaultString(@NonNull String rawDataStr) {
        MainDatum temp_mainDatum = null;
        try {
            JSONObject jsonObject = new JSONObject(rawDataStr);
            if(!jsonObject.optBoolean("status")){
                return null;
            } else {
                temp_mainDatum = parseDataStr(jsonObject.optJSONObject("search_data").toString());
            }
        } catch (JSONException e) {
            OFLogger.log(OFLogger.ERROR, OFLogger.DataParseError);
        }
        return temp_mainDatum;
    }

    /**
     * Parses the string, maps to the corresponding GSON model object and returns it
     */
    private synchronized static MainDatum parseDataStr(@NonNull String rawDataString) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.fromJson(rawDataString, MainDatum.class);
    }

}
