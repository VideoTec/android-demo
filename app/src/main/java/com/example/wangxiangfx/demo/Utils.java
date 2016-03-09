package com.example.wangxiangfx.demo;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

/**
 * Created by wangxiangfx on 2016/1/25.
 */
public class Utils {
    public static String getDeviceId(Context context) {
        String DeviceID = "";
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager.getDeviceId() == null) {
            DeviceID = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
            DeviceID = telephonyManager.getDeviceId(); // 用于获取无IMEI设备的序列号
        }
        return DeviceID;
    }
    public static String getImsi(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
    }
}
