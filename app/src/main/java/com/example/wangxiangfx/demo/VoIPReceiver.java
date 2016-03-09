package com.example.wangxiangfx.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class VoIPReceiver extends BroadcastReceiver {
    private final static String TAG = VoIPReceiver.class.getSimpleName();

    public VoIPReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "voip receiver on", Toast.LENGTH_LONG).show();
        Intent intentAct = new Intent(context, VoIPUI.class);
        intentAct.putExtras(intent);
        intentAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentAct);
    }
}
