package com.feinno.v6sdk;

/**
 * v6sdk 提供的序列化方法
 * Created by wangxiangfx on 2016/3/16.
 */
public class NativeProto {
    static {
        System.loadLibrary("sdk");
        initIDs();
    }

    private int mProtoPtr;

    public NativeProto(int ptr) {
        mProtoPtr = ptr;
    }
    public NativeProto(String typeName) {
        nativeNew(typeName);
    }
    public void freeProto() {
        if (mProtoPtr != 0) {
            nativeDelete();
            mProtoPtr = 0;
        }
    }

    public boolean putInteger(String key, int v) {
        return nativePutInteger(key, v);
    }
    public boolean putLong(String key, long v) {
        return nativePutLong(key, v);
    }
    public boolean putDouble(String key, double v) {
        return nativePutDouble(key, v);
    }
    public boolean putString(String key, String v) {
        return nativePutString(key, v);
    }
    public boolean putBytes(String key, byte[] v) {
        return nativePutBytes(key, v);
    }
    public NativeProto putProto(String key) {
        int ptr = nativePutProto(key);
        if (ptr == 0) {
            return null;
        }
        return new NativeProto(ptr);
    }

    private native static void initIDs();
    private native static String nativeLastError();
    private native static int nativeRegister(String pbFile);

    private native void nativeNew(String typeName);
    private native int nativeDelete();

    private native boolean nativePutInteger(String key, int v);
    private native boolean nativePutLong(String key, long v);
    private native boolean nativePutDouble(String key, double v);
    private native boolean nativePutString(String key, String v);
    private native boolean nativePutBytes(String key, byte[] v);
    private native int nativePutProto(String key);

    @Override
    protected void finalize() throws Throwable {
        try {
            freeProto();
        } finally {
            super.finalize();
        }
    }
}
