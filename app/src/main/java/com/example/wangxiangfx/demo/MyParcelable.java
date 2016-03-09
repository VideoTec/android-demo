package com.example.wangxiangfx.demo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangxiangfx on 2016/1/29.
 */
public class MyParcelable implements Parcelable {
    int mData;
    String mString;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mData);
        dest.writeString(this.mString);
    }

    public MyParcelable() {
    }

    protected MyParcelable(Parcel in) {
        this.mData = in.readInt();
        this.mString = in.readString();
    }

    public static final Parcelable.Creator<MyParcelable> CREATOR = new Parcelable.Creator<MyParcelable>() {
        public MyParcelable createFromParcel(Parcel source) {
            return new MyParcelable(source);
        }

        public MyParcelable[] newArray(int size) {
            return new MyParcelable[size];
        }
    };
}
