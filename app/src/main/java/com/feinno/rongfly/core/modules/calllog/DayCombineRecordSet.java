package com.feinno.rongfly.core.modules.calllog;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * 所有“日期的合并记录”的集合
 * Created by wangxiangfx on 2016/3/29.
 */
public class DayCombineRecordSet {
    private int mCombineRecordCount = 0;
    private int mMissedCombineRecordCount = 0;
    private LinkedList<String> mDaysList = new LinkedList<>(); //按时间降序排列
    private HashMap<String, DayCombineRecord> mCombineRecordsInDays = new HashMap<>();

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
    private void addADay(String day, AddItemMode mode) {
/*            if (mDaysList.contains(day)) {
                return;
            }*/
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

        if (dayCombineRecord.addCallItem(item, mode)) {
            mCombineRecordCount ++;
            if (item.isMissed()) {
                mMissedCombineRecordCount ++;
            }
        }
    }
}
