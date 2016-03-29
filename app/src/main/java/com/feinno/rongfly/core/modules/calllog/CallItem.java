package com.feinno.rongfly.core.modules.calllog;

import android.provider.CallLog;

import java.text.SimpleDateFormat;

/**
 * 一条通话记录
 * 注： missed 未接电话的意思
 * Created by wangxiangfx on 2016/3/25.
 */
public class CallItem {
    private static SimpleDateFormat sDf = new SimpleDateFormat("yyyy.MM.dd");

    public enum CallState {
        UNKNOWN,
        INCOMING,
        OUTGOING,
        MISSED
    }
    public static CallState fromSystemType(int sysType) {
        switch(sysType) {
            case CallLog.Calls.INCOMING_TYPE: {
                return CallState.INCOMING;
            }
            case CallLog.Calls.OUTGOING_TYPE: {
                return CallState.OUTGOING;
            }
            case CallLog.Calls.MISSED_TYPE: {
                return CallState.MISSED;
            }
            default: {
                return CallState.UNKNOWN;
            }
        }
    }
    public enum CallType {
        NORMAL,
        VoIP,
        VoIP_MULTI,
        VIDEO
    }

    private String mNumber;
    public String getNumber() { return mNumber; }

    private long mDate;
    public long getDate() { return mDate; }

    private String mDay;
    public String getDay() { return mDay; }

    private int mDuration;
    public int getDuration() { return mDuration; }

    private CallState mState;
    public CallState getState() { return mState; }
    public boolean isMissed() { return mState == CallState.MISSED; }

    private CallType mType;
    public CallType getType() { return mType; }

    private int hashCode;
    private int mCombineHashCode; //通话记录合并的依据
    public int getCombineHashCode() {
        int hash = mCombineHashCode;
        if (hash == 0) {
            boolean isMulti = mType == CallType.VoIP_MULTI;
            String combineKey = mNumber + mDay + isMissed() + isMulti;
            hash = combineKey.hashCode();
            mCombineHashCode = hash;
        }
        return hash;
    }

    private CallItem() {}
    public CallItem(String number,
                       long date,
                       int duration,
                       CallState state,
                       CallType type) {
        mNumber = number;
        mDate = date;
        mDay = sDf.format(date);
        mDuration = duration;
        mState = state;
        mType = type;
    }

    @Override
    public int hashCode() {
        int hash = hashCode;
        if (hash == 0) {
            if (mNumber == null || mNumber.length() == 0
                    || mDate == 0) {
                return 0;
            }
            String numberDate = mNumber + mDate;
            hash = numberDate.hashCode();
            hashCode = hash;
        }
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof CallItem) {
            if (hashCode() != o.hashCode()) {
                return false;
            }
            return true;

        } else {
            return false;
        }
    }
}
