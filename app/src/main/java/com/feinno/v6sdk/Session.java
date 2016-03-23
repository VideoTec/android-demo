package com.feinno.v6sdk;

/**
 * V6SDK 会话接口
 * Created by wangxiangfx on 2016/3/16.
 */
public class Session {
    private int mSessionPtr;
    private NativeProto mConfiguration;
    private NativeProto mContext;

    static {
        System.loadLibrary("sdk");
        initIDs();
    }

    public Session() {
        nativeNew();
    }
    public int getSessionPtr() {
        return mSessionPtr;
    }
    private void setConfiguration(int ptr) {
        mConfiguration = new NativeProto(ptr);
    }
    private void setContext(int ptr) {
        mContext = new NativeProto(ptr);
    }

    public int configure(IProto configuration) {
        configuration.serializeTo(mConfiguration);
        return nativeConfigure();
    }
    public int destroy() {
        if (mSessionPtr != 0) {
            nativeDestroy();
            mSessionPtr = 0;
        }
        return 0;
    }
    public static int connect(Transaction tx, IProto proto) {
        return tx.connect(proto);
    }
    public static int subscribe(Transaction tx, IProto proto) {
        return tx.suscribe(proto);
    }

    private native static void initIDs();
    private native int nativeNew();
    private native int nativeConfigure();
    private native int nativeDestroy();

    @Override
    protected void finalize() throws Throwable {
        try {
            destroy();
        } finally {
            super.finalize();
        }
    }
}
