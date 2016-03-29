package com.feinno.rongfly.core.modules.calllog;

import java.util.LinkedList;

/**
 * 一天的合并记录集合
 * Created by wangxiangfx on 2016/3/29.
 */

public class DayCombineRecord {
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

    //向日期中，添加一条通话
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
            record.getCallItems().add(0, item);
            mCombineRecords.add(0, record);
            if (item.isMissed()) {
                mMissedCombineRecords.add(0, record);
            }

        } else if (mode == AddItemMode.END) {
            record.getCallItems().add(item);
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
