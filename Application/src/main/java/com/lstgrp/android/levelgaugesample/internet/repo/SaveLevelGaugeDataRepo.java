package com.lstgrp.android.levelgaugesample.internet.repo;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import static com.lstgrp.android.levelgaugesample.internet.HttpParser.BASE_URL;

public class SaveLevelGaugeDataRepo {
    @SerializedName("result")
    String result;

    public String getResult() {
        return result;
    }

    public interface RetrofitAPI {
        @Headers({"Content-Type: application/json"})
        @POST("/store")
        Call<SaveLevelGaugeDataRepo> saveLevelGaugeData(@Header("x-api-token") String token, @Body HashMap<String, Object> objectMap);
    }

    public static SaveLevelGaugeDataRepo.RetrofitAPI getRetrofitAPI() {
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