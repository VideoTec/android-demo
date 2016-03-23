package com.feinno.v6sdk.test;

import com.feinno.v6sdk.SdkAPI;
import com.feinno.v6sdk.Session;
import com.feinno.v6sdk.Transaction;

/**
 * 测试 v6sdk 事务操作
 * Created by wangxiangfx on 2016/3/16.
 */
public class TXTest {
    public static void Test() {
        SdkAPI.init("/sdcard/v6sdk-script");
        Session session = new Session();
        ConfigurationProto configuration = new ConfigurationProto();
        configuration.mqttHost = "10.10.206.137";
        configuration.mqttPort = 8888;
        session.configure(configuration);
        SdkAPI.start();

        Transaction txConnect = new Transaction(
                session.getSessionPtr(), Transaction.TX_CONNECTION);
        ConnectReqProto connectReqProto = new ConnectReqProto();
        connectReqProto.userId = 13810112345L;
        connectReqProto.epid = "app";
        connectReqProto.token = "asdf89234opjiafsd90871234ja89sdf";
        Session.connect(txConnect, connectReqProto);
    }
}
