package com.example.wangxiangfx.demo;

import android.util.Log;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by wangxiangfx on 2016/1/13.
 */
public class RxJavaTest {
    public static void hello(String... names) {
        Observable.from(names).subscribe(new Action1<String>() {

            @Override
            public void call(String s) {
                System.out.print("Hello " + s + "!");
            }

        });
    }
}
