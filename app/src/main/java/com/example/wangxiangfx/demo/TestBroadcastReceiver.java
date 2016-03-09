package com.example.wangxiangfx.demo;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class TestBroadcastReceiver extends AppCompatActivity {
    BroadcastReceiver mBroadcastReceiver;
    IntentFilter mIFilter1;
    IntentFilter mIFilter2;
    NotificationManager mNotifyMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_broadcast_receiver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        mNotifyMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mIFilter1 = new IntentFilter("com.test.action1");
        mIFilter2 = new IntentFilter("com.test.action2");
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.v("TestBroadcast", intent.toString());
                Notification noti = new Notification.Builder(context)
                        .setContentTitle("Title: New mail from ")
                        .setContentText("Content: balabala...")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .build();
                mNotifyMgr.notify(1, noti);
            }
        };
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.v("TestBroadcast", intent.toString());
                    }
                }, new IntentFilter("com.test.localbroadcast"));
    }

    public void onRegAction1(View v) {
        registerReceiver(mBroadcastReceiver, mIFilter1);
    }
    public void onRegAction2(View v) {
        registerReceiver(mBroadcastReceiver, mIFilter2);
    }
    public void onUnregAction1(View v) {
        try {
            unregisterReceiver(mBroadcastReceiver);
        } catch (Exception ex) {
            Log.e("TestBroadcast", ex.getMessage());
        }
    }
    public void onSendLocalBroacast(View v) {
        Intent intent = new Intent("com.test.localbroadcast");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    public void onSendAction1(View v) {
        Intent intent = new Intent("com.test.action1");
        sendBroadcast(intent);
    }

    public static class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("TestBroadcast", intent.toString());
            NotificationManager mNotifyMgr =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification noti = new Notification.Builder(context)
                    .setContentTitle("Title: New mail from ")
                    .setContentText("Content: balabala...")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();
            mNotifyMgr.notify(1, noti);
        }
    }
}
