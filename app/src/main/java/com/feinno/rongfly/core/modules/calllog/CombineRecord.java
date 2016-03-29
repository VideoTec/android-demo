package com.feinno.rongfly.core.modules.calllog;

import java.util.ArrayList;

/**
 * 一条合并记录
 * Created by wangxiangfx on 2016/3/29.
 */
public class CombineRecord {
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
