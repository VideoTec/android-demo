package com.feinno.v6sdk;

/**
 * Created by wangxiangfx on 2016/3/11.
 */
public class Demo {
    static {
        System.loadLibrary("sdk");
    }
    public static native int test(String t);
}
