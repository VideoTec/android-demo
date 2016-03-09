package com.example.wangxiangfx.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.ultrapower.umcs.AudioCodecType;
import com.ultrapower.umcs.VideoCodecType;

public class P2PCallAct extends AppCompatActivity {
    private UMCSUtil mUMCSUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p2pcall);
        ViewGroup localPreview = (ViewGroup)findViewById(R.id.local_preview);
        ViewGroup remotePreview = (ViewGroup)findViewById(R.id.remote_preview);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = getIntent();
        String remoteIp = intent.getStringExtra("remote-ip");
        setTitle("remote ip: " + remoteIp);
        mUMCSUtil = new UMCSUtil(this);
        localPreview.addView(mUMCSUtil.getLocalSurfaceView());
        remotePreview.addView(mUMCSUtil.getRemoteSurfaceView());
        mUMCSUtil.createAudioSession(0, AudioCodecType.AUDIO_CODEC_SILK);
        mUMCSUtil.createVideoSession(0, VideoCodecType.VIDEO_CODEC_VP8);
        mUMCSUtil.createRemoteAudioChannel(0);
        mUMCSUtil.createRemoteVideoChannel(0);
        mUMCSUtil.startLocalPreview(1);
        mUMCSUtil.startRemotePreview();
        mUMCSUtil.startAudio(8081, remoteIp, 8081);
        mUMCSUtil.startVideo(8083, remoteIp, 8083);
    }

    @Override
    protected void onDestroy() {
        mUMCSUtil.destroyMediaEngine();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mUMCSUtil.pauseVideo();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUMCSUtil.resumeVideo();
    }
}
