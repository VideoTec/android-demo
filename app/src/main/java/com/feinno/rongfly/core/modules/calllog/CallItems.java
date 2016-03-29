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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * 通话记录
 * 注： missed 未接电话的意思
 * Created by wangxiangfx on 2016/3/26.
 */
public class CallItems {
    private static final String TAG = "CallItems";
    private DayCombineRecordSet mDayCombineRecordSet = new DayCombineRecordSet();

    public enum AddItemMode {
        UNKNOWN,
        BEGINE,
        END,
        OTHER
    }

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
        mDayCombineRecordSet.addItem(item, AddItemMode.END);
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
    }

    //一条合并记录
    static public class CombineRecord {
        private int mCombineHashCode;
        public int getCombineHashCode() { return mCombineHashCode; }

        private ArrayList<CallItem> mCallItems = new ArrayList<>(); //按时间降序排列
        public ArrayList<CallItem> getCallItems() {
            return mCallItems;
        }

        private CombineRecord() {}
        public CombineRecord(int hash) {
            mCombineHashCode = hash;
        }
    }
    static public class CombineRecordSet {
        private HashSet<CombineRecord> mCombineRecords = new HashSet<>();
        private CombineRecord mRecentRecord;
        public boolean addCallItem(CallItem item, AddItemMode mode) {
            //这样处理，需要查询全部的合并记录
            return true;
        }
    }

    //一天的合并记录集合
    static public class DayCombineRecord {
        private String mDay;
        private int mCombineRecordCount;
        private int mMissedCombineRecordCount;

        //按合并记录的第一条通话时间降序排列
        private LinkedList<CombineRecord> mCombineRecords = new LinkedList<>();
        private LinkedList<CombineRecord> mMissedCombineRecords = new LinkedList<>();

        private DayCombineRecord() {}
        public DayCombineRecord(String day) {
            mDay = day;
        }

        public String getDay() {
            return mDay;
        }
        public int getCombineRecordCount(boolean bMissed) {
            if (bMissed) {
                return mMissedCombineRecordCount;
            }
            return mCombineRecordCount;
        }
        public LinkedList<CombineRecord> getCombineRecords(boolean bMissed) {
            if (bMissed) {
                return mMissedCombineRecords;
            }
            return mCombineRecords;
        }

        //返回 true 表示新增了一条合并记录
        public boolean addCallItem(CallItem item, AddItemMode mode) {
            int combineHashCode = item.getCombineHashCode();
            boolean isNewRecord = false;
            CombineRecord record = removeRecordByCombineHashCode(combineHashCode, item.isMissed());
            if (record == null) {
                record = new CombineRecord(combineHashCode);
                isNewRecord = true;
            }
            if (mode == AddItemMode.BEGINE) {
                record.mCallItems.add(0, item);
                mCombineRecords.add(0, record);
                if (item.isMissed()) {
                    mMissedCombineRecords.add(0, record);
                }

            } else if (mode == AddItemMode.END) {
                record.mCallItems.add(item);
                mCombineRecords.add(record);
                if (item.isMissed()) {
                    mMissedCombineRecords.add(record);
                }

            } else {
                //TODO:
            }
            mCombineRecordCount = mCombineRecords.size();
            mMissedCombineRecordCount = mMissedCombineRecords.size();
            return isNewRecord;
        }

        private CombineRecord removeRecordByCombineHashCode(int combineHashCode, boolean isMissed) {
            CombineRecord combineRecord = null;
            //查询所有的通话记录
            for (int i = 0; i < mCombineRecords.size(); i ++) {
                CombineRecord r = mCombineRecords.get(i);
                if (r.getCombineHashCode() == combineHashCode) {
                    combineRecord = mCombineRecords.remove(i);
                    if (isMissed) {
                        //查询未接通话记录
                        for (int j = 0; j < mMissedCombineRecords.size(); j ++) {
                            CombineRecord r1 = mMissedCombineRecords.get(j);
                            if (r1.getCombineHashCode() == combineHashCode) {
                                combineRecord = mMissedCombineRecords.remove(j);
                                break;
                            }
                        }
                    }
                    break;
                }
            }
            return combineRecord;
        }
    }

    public static class DayCombineRecordSet {
        private int mCombineRecordCount = 0;
        private int mMissedCombineRecordCount = 0;
        private LinkedList<String> mDaysList = new LinkedList<>(); //按时间降序排列
        private HashMap<String, DayCombineRecord> mCombineRecordsInDays = new HashMap<>();

        public DayCombineRecordSet() {

        }

        //返回总的合并记录数量
        public int getCombineRecordCount(boolean isMissed) {
            if (isMissed) {
                return mMissedCombineRecordCount;
            }
            return mCombineRecordCount;
        }
        //返回指定位置的合并记录 - 按时间降序
        public CombineRecord getCombineRecordByPosition(int position, boolean isMissed) {
            int combineRecordCount = 0;

            //查找指定位置的合并记录
            for (int dayIndex = 0; dayIndex < mDaysList.size(); dayIndex ++) {
                String day = mDaysList.get(dayIndex);
                DayCombineRecord dayCombineRecord = mCombineRecordsInDays.get(day);
                if (dayCombineRecord == null) {
                    //TODO: 异常处理
                    return null;
                }
                int dayCombineRecordCount = dayCombineRecord.getCombineRecordCount(isMissed);
                combineRecordCount += dayCombineRecordCount;
                if (position < combineRecordCount) {
                    int combineRecordIndex = dayCombineRecordCount - (combineRecordCount - position);
                    return dayCombineRecord.getCombineRecords(isMissed).get(combineRecordIndex);
                }
            }
            return null;
        }

        //添加一个日期
        public void addADay(String day, AddItemMode mode) {
/*            if (mDaysList.contains(day)) {
                return;
            }*/
            //添加日期
            if (mode == AddItemMode.BEGINE) {
                mDaysList.add(0, day);
            } else if (mode == AddItemMode.END) {
                mDaysList.add(day);
            } else {
                //TODO:
            }
        }
        //添加一条通话
        public void addItem(CallItem item, AddItemMode mode) {
            String day = item.getDay();

            //查找指定日期的记录集，如果找不到，增加当天的记录集
            DayCombineRecord dayCombineRecord = mCombineRecordsInDays.get(day);
            if(dayCombineRecord == null) {
                dayCombineRecord = new DayCombineRecord(day);
                mCombineRecordsInDays.put(day, dayCombineRecord);
                addADay(day, mode);
            }

            //向日期中，添加一条通话
            if (dayCombineRecord.addCallItem(item, mode)) {
                mCombineRecordCount ++;
                if (item.isMissed()) {
                    mMissedCombineRecordCount ++;
                }
            }
        }
    }
}
