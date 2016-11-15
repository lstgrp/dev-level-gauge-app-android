package com.lstgrp.android.levelgaugesample.internet.repo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
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

public class GetLevelGaugeDataRepo {
    @SerializedName("data")
    private ArrayList<DATA> data;

    public int getCount() {
        return data.size();
    }

    public DATA get(int index) {
        return data.get(index);
    }

    public class DATA {
        @SerializedName("time")
        int time;
        @SerializedName("event")
        int event;
        @SerializedName("level")
        int level;
        @SerializedName("deviceid")
        String deviceid;

        public String getDeviceID() {
            return deviceid;
        }

        public int getTime() {
            return time;
        }

        public int getEvent() {
            return event;
        }

        public int getLevel() {
            return level;
        }
    }

    public interface RetrofitAPI {
        @Headers({"Content-Type : application/json"})
        @POST("/retrieve")
        Call<GetLevelGaugeDataRepo> getLevelGaugeData(@Header("x-api-token") String token, @Body HashMap<String, Object> objectMap);
    }

    public static GetLevelGaugeDataRepo.RetrofitAPI getRetrofitAPI() {
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