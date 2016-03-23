package com.feinno.v6sdk.test;

import com.feinno.v6sdk.IProto;
import com.feinno.v6sdk.NativeProto;

/**
 * 会话 配置参数
 * Created by wangxiangfx on 2016/3/16.
 */
public class ConfigurationProto implements IProto{
    public String mqttHost;
    public int mqttPort;

    @Override
    public void serializeTo(NativeProto nativeProto) {
        nativeProto.putString("mqttHost", mqttHost);
        nativeProto.putInteger("mqttPort", mqttPort);
    }

    @Override
    public void deserializeFrom(NativeProto nativeProto) {

    }
}
