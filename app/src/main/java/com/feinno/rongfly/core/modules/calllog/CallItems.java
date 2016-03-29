package com.feinno.rongfly.core.modules.calllog;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.SystemClock;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * 通话记录
 * 注： missed 未接电话的意思
 * Created by wangxiangfx on 2016/3/26.
 */
public class CallItems {
    private static final String TAG = "CallItems";
    private DayCombineRecordSet mDayCombineRecordSet = new DayCombineRecordSet();

    public int getItemCount(boolean isMissed) {
        return mDayCombineRecordSet.getCombineRecordCount(isMissed);
    }
    public CombineRecord getItem(int position, boolean isMissed) {
        return mDayCombineRecordSet.getCombineRecordByPosition(position, isMissed);
    }
    public void addItem(String number, long date, int duration,
                                    CallItem.CallState state,
                                    CallItem.CallType type, AddItemMode mode) {
        //新建一条通话
        CallItem item = new CallItem(number, date, duration, state, type);
        mDayCombineRecordSet.addItem(item, mode);
    }
    //从系统通话记录里，提取数据
    public void getCallLogFromSys(Activity act) {
        ///*
        int r = ContextCompat.checkSelfPermission(act, android.Manifest.permission.READ_CALL_LOG);
        if (PackageManager.PERMISSION_GRANTED != r) {
            ActivityCompat.requestPermissions(act,
                    new String[]{android.Manifest.permission.READ_CALL_LOG}, 0);
            return;
        }
        //*/

        ContentResolver resolver = act.getContentResolver();
        Cursor cursor = resolver.query(
                CallLog.Calls.CONTENT_URI,
                new String[] {
                        CallLog.Calls.NUMBER,
                        CallLog.Calls.DATE,
                        CallLog.Calls.DURATION,
                        CallLog.Calls.TYPE },
                null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        if (cursor == null) {
            return;
        }
        int count = cursor.getCount();
        Log.v(TAG, "通话记录条数：" + count);
        if(!cursor.moveToFirst())
            return;
        long bT = SystemClock.currentThreadTimeMillis();
        do {
            String number = cursor.getString(0);
            Long date = cursor.getLong(1);
            int duration = cursor.getInt(2);
            int type = cursor.getInt(3);
            addItem(number, date, duration,
                    CallItem.fromSystemType(type),
                    CallItem.CallType.NORMAL,
                    AddItemMode.END);
            Log.v(TAG, "number: " + number
                    + "; Date: " + date
                    + "; Duration: " + duration
                    + "; Type: " + type);
        } while(cursor.moveToNext());
        Log.v(TAG, "耗时: " + (SystemClock.currentThreadTimeMillis() - bT));
        cursor.close();
    }
}
