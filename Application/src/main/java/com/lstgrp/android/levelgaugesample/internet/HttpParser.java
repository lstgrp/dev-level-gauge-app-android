package com.lstgrp.android.levelgaugesample.internet;

import android.os.Handler;
import android.util.Log;


import com.lstgrp.android.levelgaugesample.internet.repo.CloseLevelGaugeDataRepo;
import com.lstgrp.android.levelgaugesample.internet.repo.DeviceIDRepo;
import com.lstgrp.android.levelgaugesample.internet.repo.GetLevelGaugeDataRepo;
import com.lstgrp.android.levelgaugesample.internet.repo.SaveLevelGaugeDataRepo;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HttpParser {
    public static final String BASE_URL = "http://localhost:5656/";
    public static final int HANDLER_TOAST_MESSAGE = 1;
    public static final int HANDLER_GET_DEVICE_ID = 2;
    public static final int HANDLER_SAVE_DEVICE_DATA = 3;
    public static final int HANDLER_GET_DEVICE_DATA = 4;
    public static final int HANDLER_CLOSE_SESSION = 5;

    public static void connectDevice(final Handler handler, String name, String serial) {
        HashMap<String, Map<String, String>> bodyMap = new HashMap<>();
        HashMap<String, String> objectMap = new HashMap<>();
        objectMap.put("name", name);
        objectMap.put("serial", serial);
        bodyMap.put("device", objectMap);
        DeviceIDRepo.getRetrofitAPI().getDeviceID(bodyMap).enqueue(new Callback<DeviceIDRepo>() {
            @Override
            public void onResponse(Call<DeviceIDRepo> call, Response<DeviceIDRepo> response) {
                if (response.isSuccessful()) {
                    Log.d("MainActivity", response.toString());
                    DeviceIDRepo repo = response.body();
                    handler.obtainMessage(HANDLER_GET_DEVICE_ID, repo).sendToTarget();
                } else {
                    handler.obtainMessage(HANDLER_TOAST_MESSAGE, response.code()).sendToTarget();
                    Log.e("error", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<DeviceIDRepo> call, Throwable t) {
                Log.e("MainActivity", t.getMessage());
            }
        });
    }

    public static void saveLevelGaugeData(final Handler handler, String token, String deviceID, int time, int event, int level) {
        HashMap<String, Object> objectMap = new HashMap<>();
        objectMap.put("deviceid", deviceID);
        objectMap.put("time", time);
        objectMap.put("event", event);
        objectMap.put("level", level);
        SaveLevelGaugeDataRepo.getRetrofitAPI().saveLevelGaugeData(token, objectMap).enqueue(new Callback<SaveLevelGaugeDataRepo>() {
            @Override
            public void onResponse(Call<SaveLevelGaugeDataRepo> call, Response<SaveLevelGaugeDataRepo> response) {
                if (response.isSuccessful()) {
                    Log.d("MainActivity", response.toString());
                    SaveLevelGaugeDataRepo repo = response.body();
                    handler.obtainMessage(HANDLER_SAVE_DEVICE_DATA, repo).sendToTarget();
                } else {
                    handler.obtainMessage(HANDLER_TOAST_MESSAGE, response.code()).sendToTarget();
                    Log.e("error", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<SaveLevelGaugeDataRepo> call, Throwable t) {
                Log.e("MainActivity", t.getMessage());
            }
        });
    }

    public static void getLevelGaugeData(final Handler handler, String token, String deviceID) {
        HashMap<String, Object> objectMap = new HashMap<>();
        objectMap.put("deviceid", deviceID);
        GetLevelGaugeDataRepo.getRetrofitAPI().getLevelGaugeData(token, objectMap).enqueue(new Callback<GetLevelGaugeDataRepo>() {
            @Override
            public void onResponse(Call<GetLevelGaugeDataRepo> call, Response<GetLevelGaugeDataRepo> response) {
                if (response.isSuccessful()) {
                    Log.d("MainActivity", response.toString());
                    GetLevelGaugeDataRepo repo = response.body();
                    handler.obtainMessage(HANDLER_GET_DEVICE_DATA, repo).sendToTarget();
                } else {
                    handler.obtainMessage(HANDLER_TOAST_MESSAGE, response.code()).sendToTarget();
                    Log.e("error", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<GetLevelGaugeDataRepo> call, Throwable t) {
                Log.e("MainActivity", t.getMessage());
            }
        });
    }

    public static void closeDeviceID(final Handler handler, String token) {
        HashMap<String, Object> objectMap = new HashMap<>();
        objectMap.put("token", token);
        CloseLevelGaugeDataRepo.getRetrofitAPI().closeSession(token, objectMap).enqueue(new Callback<CloseLevelGaugeDataRepo>() {
            @Override
            public void onResponse(Call<CloseLevelGaugeDataRepo> call, Response<CloseLevelGaugeDataRepo> response) {
                if (response.isSuccessful()) {
                    Log.d("MainActivity", response.toString());
                    CloseLevelGaugeDataRepo repo = response.body();
                    handler.obtainMessage(HANDLER_CLOSE_SESSION, repo).sendToTarget();
                } else {
                    handler.obtainMessage(HANDLER_TOAST_MESSAGE, response.code()).sendToTarget();
                    Log.e("error", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<CloseLevelGaugeDataRepo> call, Throwable t) {
                Log.e("MainActivity", t.getMessage());
            }
        });
    }
}
