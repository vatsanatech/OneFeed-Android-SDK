package com.onefeedsdk.rest;

import com.onefeedsdk.BuildConfig;
import com.onefeedsdk.app.Constant;
import com.onefeedsdk.rest.api.Api;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 07-August-2018
 * Time: 15:54
 */
public class ApiFactory {

    private static final String contentType = "application/json";
    private static final String accept = "application/json";

    private OkHttpClient okHttpClient;
    private Api api;
    private Api trackingApi;

    public ApiFactory() {
        init();
    }

    private void init() {

        // set your desired log level
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BASIC : HttpLoggingInterceptor.Level.NONE);

        //okHttpClient
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        request.newBuilder()
                                .header("Content-Type", contentType)
                                .header("Content-Type", "application/text")
                                .header("Accept", accept)
                                .method(request.method(), request.body());
                        Response response = chain.proceed(request);

                        return response;
                    }
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(Api.class);

        Retrofit retrofitTracking = new Retrofit.Builder()
                .baseUrl(Constant.TRACKING_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        trackingApi = retrofitTracking.create(Api.class);
    }

    public Api getApi() {
        return api;
    }

    public Api getTrackingApi() {
        return trackingApi;
    }
}
