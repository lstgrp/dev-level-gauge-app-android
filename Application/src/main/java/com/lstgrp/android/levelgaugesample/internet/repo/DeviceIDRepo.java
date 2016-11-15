package com.lstgrp.android.levelgaugesample.internet.repo;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import static com.lstgrp.android.levelgaugesample.internet.HttpParser.BASE_URL;

public class DeviceIDRepo {
    @SerializedName("deviceid")
    String deviceid;
    @SerializedName("token")
    String token;
    @SerializedName("ttl")
    int ttl;

    public String getDeviceID() {
        return deviceid;
    }

    public String getToken() {
        return token;
    }

    public int getTTL() {
        return ttl;
    }

    public interface RetrofitAPI {
        @Headers({"Content-Type : application/json"})
        @POST("/device")
        Call<DeviceIDRepo> getDeviceID(@Body HashMap<String, Map<String, String>> device);
    }

    public static RetrofitAPI getRetrofitAPI() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(RetrofitAPI.class);
    }
}