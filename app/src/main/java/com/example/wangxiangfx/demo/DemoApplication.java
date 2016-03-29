package com.example.wangxiangfx.demo;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTest;

import com.feinno.v6sdk.Demo;
import com.feinno.sdk.dapi.RCSManager;
import com.feinno.v6sdk.test.TXTest;

/**
 * Created by wangxiangfx on 2016/1/25.
 */
public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Demo.test("test v6sdk demo");
        //TXTest.Test();
        String processName = getCurProcessName(android.os.Process.myPid());
        Log.d("DemoApplication", processName);
        if ("com.feinno.rongfly".equals(processName)) {
            RCSManager.startSvc(this, null, false);
        }
        processName = JSONTest.say();
        try {
            JSONObject object = new JSONObject("this is a test json");
        } catch (JSONException ex) {
            Log.e("Demo", ex.getMessage());
        }
    }

    public String getCurProcessName(int pid) {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
