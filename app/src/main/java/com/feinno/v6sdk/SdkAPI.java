package com.feinno.v6sdk;

/**
 * v6sdk 初始操作接口
 * Created by wangxiangfx on 2016/3/16.
 */
public class SdkAPI {
    static {
        System.loadLibrary("sdk");
    }
    public static int init(String scriptPath) {
        return nativeInitSDK(3000, scriptPath);
    }
    public static int start() {
        return nativeStartSDK();
    }
    private static native int nativeInitSDK(int log_level, String scriptPath);
    private static native int nativeStartSDK();
}
