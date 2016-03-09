package com.example.wangxiangfx.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.ultrapower.umcs.AudioCodecType;
import com.ultrapower.umcs.VideoCodecType;

public class ServiceAct extends AppCompatActivity {
    private UMCSUtil mUMCSUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        ViewGroup localPreview = (ViewGroup)findViewById(R.id.local_preview);
        ViewGroup remotePreview = (ViewGroup)findViewById(R.id.remote_preview);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String remoteIp = "182.92.180.230";
        Intent intent = getIntent();
        int audioSSRCId = intent.getIntExtra("audio-ssrc-id", 0);
        int videoSSRCId = intent.getIntExtra("video-ssrc-id", 0);
        setTitle("assrc: " + audioSSRCId + "; vssrc: " + videoSSRCId);
        mUMCSUtil = new UMCSUtil(this);
        localPreview.addView(mUMCSUtil.getLocalSurfaceView());
        remotePreview.addView(mUMCSUtil.getRemoteSurfaceView());
        mUMCSUtil.createAudioSession(audioSSRCId, AudioCodecType.AUDIO_CODEC_SILK);
        mUMCSUtil.createVideoSession(videoSSRCId, VideoCodecType.VIDEO_CODEC_VP8);
        mUMCSUtil.createRemoteAudioChannel(0);
        mUMCSUtil.createRemoteVideoChannel(0);
        mUMCSUtil.startLocalPreview(1);
        mUMCSUtil.startRemotePreview();
        int localPort = (int) (Math.random()*(60000-1025)+1025);
        mUMCSUtil.startAudio(localPort, remoteIp, 8601);
        mUMCSUtil.startVideo(localPort + 2, remoteIp, 8601);
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
