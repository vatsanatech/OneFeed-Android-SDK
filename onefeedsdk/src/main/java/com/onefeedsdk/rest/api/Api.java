package com.onefeedsdk.rest.api;


import com.onefeedsdk.app.Constant;
import com.onefeedsdk.model.FeedModel;
import com.onefeedsdk.model.RepeatingCardModel;
import com.onefeedsdk.model.TokenUpdateModel;
import com.onefeedsdk.model.TokenUpdateRes;
import com.onefeedsdk.model.TrackingModel;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 07-August-2018
 * Time: 14:00
 */

public interface Api {

    @GET(Constant.HOME_FEED)
    Call<FeedModel> initOneFeedInitialise(@Query("app_id") String appId,
                                          @Query("api_key") String apiKey,
                                          @Query("unique_identifier") String applicationId,
                                          @Query("offset") int offset,
                                          @Query("firebase_token") String token,
                                          @Query("device_id") String deviceId,
                                          @Query("onefeed_sdk_version") String sdkVersion);

    @GET(Constant.HOME_FEED)
    Call<RepeatingCardModel> initOneFeedRepeatingCard(@Query("app_id") String appId,
                                                      @Query("api_key") String apiKey,
                                                      @Query("unique_identifier") String applicationId,
                                                      @Query("offset") int offset,
                                                      @Query("firebase_token") String token,
                                                      @Query("repeatingCard") int repeatingCard,
                                                      @Query("card_id") int cardId,
                                                      @Query("device_id") String deviceId,
                                                      @Query("onefeed_sdk_version") String sdkVersion);

    @GET(Constant.SEARCH_FEED)
    Call<FeedModel> searchFeed(@Query("keyword") String keyword,
                               @Query("offset") int offset,
                               @Query("app_id") String appId,
                               @Query("user_id") String userId,
                               @Query("device_id") String deviceId,
                               @Query("onefeed_sdk_version") String sdkVersion);

    @POST(Constant.TOKEN_UPDATE)
    Call<TokenUpdateRes> userTokenUpdate(@Body RequestBody model);

    @POST(Constant.TRACKING)
    Call<String> userTracking(@Body TrackingModel model);

    @POST(Constant.ERROR_TRACKING)
    Call<String> errorTracking(@Body TrackingModel model);
}
