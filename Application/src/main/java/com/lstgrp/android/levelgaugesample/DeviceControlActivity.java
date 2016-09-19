package com.lstgrp.android.levelgaugesample;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class DeviceControlActivity extends Activity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCustomValueCharacteristic;
    private BluetoothGattCharacteristic mNotifyBatteryCharacteristic;
    private BluetoothGattCharacteristic tempCustomValueCharacteristic;
    private BluetoothGattCharacteristic tempBatteryCharacteristic;

    private BluetoothGattCharacteristic mReadTimeCharacteristic;
    private BluetoothGattCharacteristic mReadManufacturerNameCharacteristic;
    private BluetoothGattCharacteristic mReadModelNumberCharacteristic;
    private BluetoothGattCharacteristic mReadSerialNumberCharacteristic;
    private BluetoothGattCharacteristic mReadFirmwareRevisionCharacteristic;
    private BluetoothGattCharacteristic mReadHardwareRevisionCharacteristic;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                checkGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String uuidString = intent.getStringExtra(BluetoothLeService.EXTRA_DATA_TYPE);

                String value;

                if (!uuidString.equals("")) {
                    value = setText(intent, uuidString);

                } else {
                    value = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                }
                displayData(value);
            }
        }
    };

    private String setText(Intent intent, String uuidString) {
        String value = "";
        if (uuidString.equals(BluetoothLeService.UUID_CLIENT_CHARACTERISTIC_MANUFACTURER_NAME.toString())) {
            String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
            ((TextView) findViewById(R.id.manufacturerNameTextView)).setText(data);
        } else if (uuidString.equals(BluetoothLeService.UUID_CLIENT_CHARACTERISTIC_MODEL_NUMBER.toString())) {
            String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
            ((TextView) findViewById(R.id.modelNumberTextView)).setText(data);
        } else if (uuidString.equals(BluetoothLeService.UUID_CLIENT_CHARACTERISTIC_SERIAL_NUMBER.toString())) {
            String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
            ((TextView) findViewById(R.id.serialNumberTextView)).setText(data);
        } else if (uuidString.equals(BluetoothLeService.UUID_CLIENT_CHARACTERISTIC_FIRMWARE_REVISION.toString())) {
            String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
            ((TextView) findViewById(R.id.firmwareRevisionTextView)).setText(data);
        } else if (uuidString.equals(BluetoothLeService.UUID_CLIENT_CHARACTERISTIC_HARDWARE_REVISION.toString())) {
            String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
            ((TextView) findViewById(R.id.hardwareRevisionTextView)).setText(data);
        } else if (uuidString.equals(BluetoothLeService.UUID_CLIENT_CHARACTERISTIC_BATTERY_LEVEL.toString())) {
            String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
            value = data + " %";
        } else if (uuidString.equals(BluetoothLeService.UUID_CLIENT_CHARACTERISTIC_LEVEL.toString())) {
            byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);

            value = mLevelRevNotificationHandler(data);

        } else if (uuidString.equals(BluetoothLeService.UUID_CLIENT_CHARACTERISTIC_TIME.toString())) {
            byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
            int[] times = getTime(data);
            value = times[0] + "/" + times[1] + "/" + times[2] + " " + times[3] + ":" + times[4] + ":" + times[5];
        }
        return value;
    }

    public String mLevelRevNotificationHandler(byte[] value) {
        boolean isRealTime = false;
        boolean isSuddenData = false;
        if (value.length == 8) {
            /*
            * 8 byte structyre.
            * ex) if data structure is "01 12 0c 1e 05 06 07 08",
            * data type is real, sudden type,
            * year is 0x0201(513 year)
            * 0x0c(12) month, 0x1e(30) day, 0x05(5) hour, 0x06(6) minute, 0x07(7) second
            * gauge is 0x08(8)
            * */

            int year;
            int month;
            int day;
            int hour;
            int minute;
            int second;
            String type = "";

            char c = String.format("%02X ", value[1]).trim().charAt(0);
            switch (c) {
                case '0'://real, normal
                    isRealTime = true;
                    type = "real, normal";
                    break;
                case '1'://real, sudden
                    isRealTime = true;
                    isSuddenData = true;
                    type = "real, sudden";
                    break;
                case '8'://saved, normal
                    isRealTime = false;
                    type = "saved, normal";
                    break;
                case '9'://saved, sudden
                    isRealTime = false;
                    isSuddenData = true;
                    type = "saved, sudden";
                    break;
            }

            int[] times = getTime(value);

            year = times[0];
            month = times[1];
            day = times[2];
            hour = times[3];
            minute = times[4];
            second = times[5];
            int levelNum = value[7];

            return year + "/" + month + "/" + day + " " + hour + ":" + minute + ":" + second
                    + "\ntype : " + type + ", level : " + levelNum;
        } else return "";
    }

    public int[] getTime(byte[] value) {
        int year;
        int month;
        int day;
        int hour;
        int minute;
        int second;

        year = ((0x0f & value[1]) << 8) | (0x00ff & value[0]);
        month = value[2];
        day = value[3];
        hour = value[4];
        minute = value[5];
        second = value[6];

        return new int[]{year, month, day, hour, minute, second};
    }

    private View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.notify_value_button:
                    setmNotifyCustomValueCharacteristic(tempCustomValueCharacteristic);
                    break;
                case R.id.notify_battery_button:
                    setmNotifyBatteryCharacteristic(tempBatteryCharacteristic);
                    break;
                case R.id.read_time_button:
                    mBluetoothLeService.readCharacteristic(mReadTimeCharacteristic);
                    break;
                case R.id.read_manufacturer_name_button:
                    mBluetoothLeService.readCharacteristic(mReadManufacturerNameCharacteristic);
                    break;
                case R.id.read_model_number_button:
                    mBluetoothLeService.readCharacteristic(mReadModelNumberCharacteristic);
                    break;
                case R.id.read_serial_number_button:
                    mBluetoothLeService.readCharacteristic(mReadSerialNumberCharacteristic);
                    break;
                case R.id.read_firmware_revision_button:
                    mBluetoothLeService.readCharacteristic(mReadFirmwareRevisionCharacteristic);
                    break;
                case R.id.read_hardware_revision_button:
                    mBluetoothLeService.readCharacteristic(mReadHardwareRevisionCharacteristic);
                    break;
                case R.id.send_time_button:
                    EditText tv = (EditText) findViewById(R.id.time_textView);
                    byte[] timeByteArray;
                    String value = tv.getText().toString().trim();
                    if (value.equals("")) {
                        timeByteArray = hexToByteArray(setDate());
                    } else {
                        timeByteArray = hexToByteArray(setDate(value));//2000/03/04 05:06:07
                    }

                    mBluetoothLeService.writeCharacteristic(mReadTimeCharacteristic, timeByteArray);
                    break;
            }
        }
    };

    public static String setDate(String value) {
        String[] a = value.split(" ");
        String[] dates = a[0].split("/");
        String[] times = a[1].split(":");

        StringBuilder sb = new StringBuilder();
        for (String date : dates) {
            String f = String.format("%02X", Integer.parseInt(date));
            if (f.length() == 3) {
                f = "0" + f;
                String s1 = f.substring(0, 2);
                String s2 = f.substring(2, 4);
                sb.append(s2);
                sb.append(s1);
            } else
                sb.append(f);
        }

        for (String time : times) {
            sb.append(String.format("%02X", Integer.parseInt(time)));
        }

        sb.append("01");//temp data

        return sb.toString();
    }

    public static String setDate() {
        Calendar mCalendar = Calendar.getInstance();
        String[] formats = {"yyyy", "MM", "dd", "HH", "mm", "ss"};
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat mDateFormat;
        for (String format : formats) {
            mDateFormat = new SimpleDateFormat(format, Locale.getDefault());
            String dates = mDateFormat.format(mCalendar.getTime());
            String datesHex = String.format("%02X", Integer.parseInt(dates));
            if (datesHex.length() == 3) {
                sb.append("0");
                sb.append(datesHex);
                String year1 = sb.toString().substring(0, 2);
                String year2 = sb.toString().substring(2, 4);
                sb.delete(0, sb.length());
                sb.append(year2);
                sb.append(year1);

            } else if (datesHex.length() == 2) {
                sb.append(datesHex);
            }

        }
        sb.append(findDayOfWeek(mCalendar.get(Calendar.DAY_OF_WEEK)));
        return sb.toString();

    }

    private static String findDayOfWeek(int day) {
        String setday = null;
        switch (day) {
            case 1:
                setday = "00";
                break;
            case 2:
                setday = "01";
                break;
            case 3:
                setday = "02";
                break;
            case 4:
                setday = "03";
                break;
            case 5:
                setday = "04";
                break;
            case 6:
                setday = "05";
                break;
            case 7:
                setday = "06";
                break;

            default:
                break;
        }
        return setday;
    }

    public static byte[] hexToByteArray(String hex) {
        if (hex == null || hex.length() == 0) {
            return null;
        }

        byte[] ba = new byte[hex.length() / 2];
        for (int i = 0; i < ba.length; i++) {
            ba[i] = (byte) Integer
                    .parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (byte byteChar : ba)
            stringBuilder.append(String.format("%02X ", byteChar));
        Log.e("hexToByteArray ", stringBuilder.toString());
        return ba;
    }

    private void setmNotifyCustomValueCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mNotifyCustomValueCharacteristic != null) {
            mBluetoothLeService.setCharacteristicNotification(mNotifyCustomValueCharacteristic, false);
            mNotifyCustomValueCharacteristic = null;
        }

        mNotifyCustomValueCharacteristic = characteristic;
        mBluetoothLeService.setCharacteristicNotification(mNotifyCustomValueCharacteristic, true);
    }

    private void setmNotifyBatteryCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mNotifyBatteryCharacteristic != null) {
            mBluetoothLeService.setCharacteristicNotification(mNotifyBatteryCharacteristic, false);
            mNotifyBatteryCharacteristic = null;
        }

        mNotifyBatteryCharacteristic = characteristic;
        mBluetoothLeService.readCharacteristic(mNotifyBatteryCharacteristic);
        mBluetoothLeService.setCharacteristicNotification(mNotifyBatteryCharacteristic, true);
    }

    private void clearUI() {
        mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value);

        Button mNotifyValueButton = (Button) findViewById(R.id.notify_value_button);
        mNotifyValueButton.setOnClickListener(mButtonListener);
        Button mNotifyBatteryButton = (Button) findViewById(R.id.notify_battery_button);
        mNotifyBatteryButton.setOnClickListener(mButtonListener);

        Button mTimeButton = (Button) findViewById(R.id.read_time_button);
        mTimeButton.setOnClickListener(mButtonListener);

        Button mManufacturerNameButton = (Button) findViewById(R.id.read_manufacturer_name_button);
        mManufacturerNameButton.setOnClickListener(mButtonListener);
        Button mModelNumberButton = (Button) findViewById(R.id.read_model_number_button);
        mModelNumberButton.setOnClickListener(mButtonListener);
        Button mSerialNumberNameButton = (Button) findViewById(R.id.read_serial_number_button);
        mSerialNumberNameButton.setOnClickListener(mButtonListener);
        Button mFirmwareRevisionNameButton = (Button) findViewById(R.id.read_firmware_revision_button);
        mFirmwareRevisionNameButton.setOnClickListener(mButtonListener);
        Button mHardwareRevisionButton = (Button) findViewById(R.id.read_hardware_revision_button);
        mHardwareRevisionButton.setOnClickListener(mButtonListener);

        Button mSendTimeButton = (Button) findViewById(R.id.send_time_button);
        mSendTimeButton.setOnClickListener(mButtonListener);


        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
    }

    private void checkGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        mGattCharacteristics = new ArrayList<>();

        for (BluetoothGattService gattService : gattServices) {
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<>();

            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                UUID uuid = gattCharacteristic.getUuid();
                try {
                    if (uuid.equals(BluetoothLeService.UUID_CLIENT_CHARACTERISTIC_LEVEL)) {
                        tempCustomValueCharacteristic = gattCharacteristic;
                    } else if (uuid.equals(BluetoothLeService.UUID_CLIENT_CHARACTERISTIC_BATTERY_LEVEL)) {
                        tempBatteryCharacteristic = gattCharacteristic;
                    }

                    if (uuid.equals(BluetoothLeService.UUID_CLIENT_CHARACTERISTIC_TIME)) {
                        mReadTimeCharacteristic = gattCharacteristic;
                    }

                    if (uuid.equals(BluetoothLeService.UUID_CLIENT_CHARACTERISTIC_MANUFACTURER_NAME)) {
                        mReadManufacturerNameCharacteristic = gattCharacteristic;
                    } else if (uuid.equals(BluetoothLeService.UUID_CLIENT_CHARACTERISTIC_MODEL_NUMBER)) {
                        mReadModelNumberCharacteristic = gattCharacteristic;
                    } else if (uuid.equals(BluetoothLeService.UUID_CLIENT_CHARACTERISTIC_SERIAL_NUMBER)) {
                        mReadSerialNumberCharacteristic = gattCharacteristic;
                    } else if (uuid.equals(BluetoothLeService.UUID_CLIENT_CHARACTERISTIC_FIRMWARE_REVISION)) {
                        mReadFirmwareRevisionCharacteristic = gattCharacteristic;
                    } else if (uuid.equals(BluetoothLeService.UUID_CLIENT_CHARACTERISTIC_HARDWARE_REVISION)) {
                        mReadHardwareRevisionCharacteristic = gattCharacteristic;
                    }

                } catch (Exception ee) {
                    Log.e("uuid exception", ee.toString());
                }
            }
            mGattCharacteristics.add(charas);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
