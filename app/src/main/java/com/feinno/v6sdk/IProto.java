package com.feinno.v6sdk;

/**
 * 调用 v6sdk 提供的序列化方法
 * Created by wangxiangfx on 2016/3/16.
 */
public interface IProto {
    public void serializeTo(NativeProto nativeProto);
    public void deserializeFrom(NativeProto nativeProto);
}
