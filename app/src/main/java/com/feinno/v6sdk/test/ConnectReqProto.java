package com.feinno.v6sdk.test;

import com.feinno.v6sdk.IProto;
import com.feinno.v6sdk.NativeProto;

/**
 * 连接事务请求参数
 * Created by wangxiangfx on 2016/3/16.
 */
public class ConnectReqProto implements IProto{
    public long userId;
    public String epid;
    public String token;

    @Override
    public void serializeTo(NativeProto nativeProto) {
        nativeProto.putLong("userId", userId);
        nativeProto.putString("epid", epid);
        nativeProto.putString("token", token);
    }

    @Override
    public void deserializeFrom(NativeProto nativeProto) {

    }
}
