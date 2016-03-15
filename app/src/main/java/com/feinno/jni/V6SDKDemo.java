package com.feinno.jni;

/**
 * Created by wangxiangfx on 2016/3/11.
 */
public class V6SDKDemo {
    static {
        System.loadLibrary("sdk");
    }
    public static native int test(String t);
}
