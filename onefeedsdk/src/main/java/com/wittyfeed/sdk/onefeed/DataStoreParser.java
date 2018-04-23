package com.wittyfeed.sdk.onefeed;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * Parses the raw-data JSON string into Model-Data using GSON conversion
 *  and corresponding POJO classes
 *
 */

final class DataStoreParser {

    // TODO: 23/04/18 make the methods below to return boolean

    synchronized static MainDatum parseMainFeedString(String rawDataStr) {
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

    synchronized static MainDatum parseGenericFeedString(String rawDataStr) {
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

    synchronized static MainDatum parseSearchDefaultString(String rawDataStr) {
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

    private synchronized static MainDatum parseDataStr(String rawDataString) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.fromJson(rawDataString, MainDatum.class);
    }

}
