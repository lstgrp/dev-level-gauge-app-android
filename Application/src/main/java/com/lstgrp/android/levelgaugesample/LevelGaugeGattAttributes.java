package com.lstgrp.android.levelgaugesample;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class LevelGaugeGattAttributes {
    public static HashMap<String, String> attributes = new HashMap();
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    public  static String CLIENT_SERVICE_DEVICE_INFORMATION = "0000180a-0000-1000-8000-00805f9b34fb";
    public  static String CLIENT_CHARACTERISTIC_MANUFACTURER_NAME = "00002a29-0000-1000-8000-00805f9b34fb";
    public  static String CLIENT_CHARACTERISTIC_MODEL_NUMBER = "00002a24-0000-1000-8000-00805f9b34fb";
    public  static String CLIENT_CHARACTERISTIC_SERIAL_NUMBER = "00002a25-0000-1000-8000-00805f9b34fb";
    public  static String CLIENT_CHARACTERISTIC_FIRMWARE_REVISION = "00002a26-0000-1000-8000-00805f9b34fb";
    public  static String CLIENT_CHARACTERISTIC_HARDWARE_REVISION = "00002a27-0000-1000-8000-00805f9b34fb";

    public  static String CLIENT_SERVICE_BATTERY = "0000180a-0000-1000-8000-00805f9b34fb";
    public  static String CLIENT_CHARACTERISTIC_BATTERY_LEVEL = "00002a19-0000-1000-8000-00805f9b34fb";

    public  static String CLIENT_SERVICE_CUSTOM = "a7fe5e12-de71-4020-b2cf-8bf764fb0a8d";
    public  static String CLIENT_CHARACTERISTIC_LEVEL = "a7fe5e14-de71-4020-b2cf-8bf764fb0a8d";
    public  static String CLIENT_CHARACTERISTIC_TIME = "a7fe5e13-de71-4020-b2cf-8bf764fb0a8d";

    static {
        // Device Information Services
        attributes.put(CLIENT_SERVICE_DEVICE_INFORMATION, "Device Information Service");

        // Characteristics
        attributes.put(CLIENT_CHARACTERISTIC_MANUFACTURER_NAME, "Manufacturer Name");
        attributes.put(CLIENT_CHARACTERISTIC_MODEL_NUMBER, "Model Number");
        attributes.put(CLIENT_CHARACTERISTIC_SERIAL_NUMBER, "Serial Number");
        attributes.put(CLIENT_CHARACTERISTIC_FIRMWARE_REVISION, "Firmware Number");
        attributes.put(CLIENT_CHARACTERISTIC_HARDWARE_REVISION, "Hardware Number");

        // Battery Services
        attributes.put(CLIENT_SERVICE_BATTERY, "Device Battery Service");

        // Battery Characteristics
        attributes.put(CLIENT_CHARACTERISTIC_BATTERY_LEVEL, "Device Information Service");

        // Custom Service
        attributes.put(CLIENT_SERVICE_CUSTOM, "Device Information Service");

        // Custom Characteristics
        attributes.put(CLIENT_CHARACTERISTIC_LEVEL, "Device Information Service");
        attributes.put(CLIENT_CHARACTERISTIC_TIME, "Device Information Service");
    }
}

