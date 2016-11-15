package com.lstgrp.android.levelgaugesample.preference;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by DONGJIN on 2016-11-15.
 */

public class DevicePreference {
    private static final String PREFERENCE_NAME = "Device";
    private static final String DEVICE_ID = "deviceID";
    private static final String TOKEN = "token";

    private static void savePreferences(Context context, String target, String value) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(target, value);
        editor.apply();
    }

    private static String getPreferences(Context context, String target) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return pref.getString(target, "");
    }

    private static void removePreferences(Context context, String target) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(target);
        editor.apply();
    }

    public static void setDeviceID(Context context, String deviceID) {
        savePreferences(context, DEVICE_ID, deviceID);
    }

    public static void setToken(Context context, String token) {
        savePreferences(context, TOKEN, token);
    }

    public static void removeDeviceID(Context context) {
        removePreferences(context, DEVICE_ID);
    }

    public static void removeToken(Context context) {
        removePreferences(context, TOKEN);
    }

    public static String getDeviceID(Context context) {
        return getPreferences(context, DEVICE_ID);
    }

    public static String getToken(Context context) {
        return getPreferences(context, TOKEN);
    }
}
