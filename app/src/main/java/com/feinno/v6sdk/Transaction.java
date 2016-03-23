package com.feinno.v6sdk;

/**
 * v6sdk 事务类接口
 * Created by wangxiangfx on 2016/3/16.
 */
public class Transaction {
    static {
        System.loadLibrary("sdk");
        initIDs();
    }

    // 事务类型
    // 与 jni_transaction.c 中的定义一致
    public static final int TX_CONNECTION = 1;
    public static final int TX_SUBSCRIBE = 2;
    public static final int TX_SEND_MESSAGE = 3;
    public static final int TX_INCOMING_MESSAGE = 4;

    private int mTXPtr;
    private int mSessionPtr;
    private NativeProto mRequest;
    private NativeProto mResponse;

    private Transaction() {}
    public Transaction(int sessionPtr, int type) {
        mSessionPtr = sessionPtr;
        nativeNew(sessionPtr, type);
    }
    private void setRequestProto(int ptr) {
        mRequest = new NativeProto(ptr);
    }
    private void setResponseProto(int ptr) {
        mResponse = new NativeProto(ptr);
    }

    public int begin() {
        return nativeBegin();
    }
    public int returnServer() {
        return nativeReturn();
    }
    public int delete() {
        if (mTXPtr != 0) {
            nativeDelete();
            mTXPtr = 0;
        }
        return 0;
    }

    // 事务处理动作
    int connect(IProto request) {
        request.serializeTo(mRequest);
        return nativeConnect();
    }
    int suscribe(IProto request) {
        request.serializeTo(mRequest);
        return nativeSubscribe();
    }

    private native static void initIDs();
    private native void nativeNew(int sessionPtr, int type);
    private native int nativeBegin();
    private native int nativeReturn();
    private native int nativeDelete();

    private native int nativeConnect();
    private native int nativeSubscribe();

    @Override
    protected void finalize() throws Throwable {
        try {
            delete();
        } finally {
            super.finalize();
        }
    }
}
