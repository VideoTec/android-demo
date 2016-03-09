package com.example.wangxiangfx.demo;

import android.content.Context;
import android.view.SurfaceView;

import com.ultrapower.umcs.AudioChannelEventListener;
import com.ultrapower.umcs.AudioCodecType;
import com.ultrapower.umcs.AudioSession;
import com.ultrapower.umcs.EngineConfig;
import com.ultrapower.umcs.MediaEngine;
import com.ultrapower.umcs.NetworkState;
import com.ultrapower.umcs.NetworkStateListener;
import com.ultrapower.umcs.RemoteAudioChannel;
import com.ultrapower.umcs.RemoteVideoChannel;
import com.ultrapower.umcs.SrtpCipherType;
import com.ultrapower.umcs.TraceLevel;
import com.ultrapower.umcs.Transport;
import com.ultrapower.umcs.VideoChannelEventListener;
import com.ultrapower.umcs.VideoCodecType;
import com.ultrapower.umcs.VideoSession;
import com.ultrapower.umcs.video.ViERenderer;

public class UMCSUtil {
    private final static String TAG = UMCSUtil.class.getSimpleName();

    private Context mContext;
    private int mCamId = 1;
    private MediaEngine mMediaEngine;

    private AudioSession mAudioSession;
    private RemoteAudioChannel mRemoteAudioChannel;

    private VideoSession mVideoSession;
    private RemoteVideoChannel mRemoteVideoChannel;
    private SurfaceView mLocalSurfaceView;
    private SurfaceView mRemoteSurfaceView;

    public UMCSUtil(Context context) {
        mContext = context;
        mLocalSurfaceView = ViERenderer.CreateLocalRenderer(mContext);
        mRemoteSurfaceView = ViERenderer.CreateRemoteRenderer(mContext, true);
        getMediaEngine();
    }
    public SurfaceView getLocalSurfaceView() {
        return mLocalSurfaceView;
    }
    public SurfaceView getRemoteSurfaceView() {
        return mRemoteSurfaceView;
    }

    private void getMediaEngine() {
        mMediaEngine = MediaEngine.getInstance();
        if (mMediaEngine.isInitialized()) {
            mMediaEngine.terminate();
        }
        EngineConfig engineConfig = new EngineConfig(false, TraceLevel.TRACE_ALL, mContext,
                false, false, false, false,
                "123456123456123456123456123456", SrtpCipherType.AES_CM_128_HMAC_SHA1_32,
                "123456123456123456123456123456", SrtpCipherType.AES_CM_128_HMAC_SHA1_80,
                new NetworkStateListener() {
                    @Override
                    public void onRemoteNetworkStateChanged(int videoChannelId, NetworkState state) {
                    }
                    @Override
                    public void onLocalNetworkStateChanged(NetworkState state) {
                    }
                });
        mMediaEngine.initialize(engineConfig);
    }

    //AudioCodecType.AUDIO_CODEC_SILK;
    public void createAudioSession(int ssrcId, AudioCodecType audioCodecType) {
        mAudioSession = mMediaEngine.createAudioSession(ssrcId);
        mAudioSession.setSendCodec(audioCodecType);
        mAudioSession.setLoudspeakerOn(false);
    }
    public void createRemoteAudioChannel(int ssrcId) {
        mRemoteAudioChannel = mAudioSession.createRemoteChannel(
                ssrcId, false, false,
                SrtpCipherType.AES_CM_128_HMAC_SHA1_32, "123456123456123456123456123456");
        mRemoteAudioChannel.registerEventListener(
                new AudioChannelEventListener() {
                    @Override
                    public void onAudioChannelEvent(RemoteAudioChannel channel, int audioEvent) {
                    }
                },
                RemoteAudioChannel.AUIDO_EVENT_DEAD);
    }

    //VideoCodecType.VIDEO_CODEC_VP8;
    public void createVideoSession(int ssrcId, VideoCodecType videoCodecType) {
        mVideoSession = mMediaEngine.createVideoSession(ssrcId);
        mVideoSession.setSendCodec(videoCodecType);
    }
    public void createRemoteVideoChannel(int ssrcId) {
        mRemoteVideoChannel = mVideoSession.createRemoteChannel(
                ssrcId, false, false,
                SrtpCipherType.AES_CM_128_HMAC_SHA1_80, "123456123456123456123456123456");
        mRemoteVideoChannel.registerEventListener(
                new VideoChannelEventListener() {
                    @Override
                    public void onVideoChannelEvent(RemoteVideoChannel channel, int audioEvent) {
                    }
                },
                RemoteVideoChannel.VIDEO_EVENT_DEAD);
    }

    public void startLocalPreview(int camIndex) {
        mCamId = camIndex;
        mVideoSession.openCamera(mCamId);
        mVideoSession.startPreview(mLocalSurfaceView);
    }
    public void startRemotePreview() {
        mRemoteVideoChannel.startRender(mRemoteSurfaceView);
    }

    public void startAudio(int localAudioRtpPort, String remoteIp, int remoteAudioRtpPort) {
        Transport transport = Transport.createInternalTransport(
                "0.0.0.0", localAudioRtpPort, localAudioRtpPort + 1,
                remoteIp, remoteAudioRtpPort, remoteAudioRtpPort + 1);
        mAudioSession.configTransport(transport);
        mAudioSession.start();
    }
    public void startVideo(int localVidoRtpPort, String remoteIp, int remoteVideoRtpPort) {
        Transport transport = Transport.createInternalTransport(
                "0.0.0.0", localVidoRtpPort, localVidoRtpPort + 1,
                remoteIp, remoteVideoRtpPort, remoteVideoRtpPort + 1);
        mVideoSession.configTransport(transport);
        mVideoSession.start();
    }
    public void stopAudio() {
        mAudioSession.stop();
    }
    public void stopVideo() {
        mVideoSession.stop();
    }
    public void pauseVideo() {
        if (null == mVideoSession || mVideoSession.isTerminated()) {
            return;
        }
        //todo:不能停止，停止以后不能重新start - from rongtalk code
        //mRemoteVideoChannel.stopRender();
        if (!mVideoSession.isPaused()) {
            mVideoSession.setPause(true);
            mVideoSession.closeCamera();
            mVideoSession.stopPreview();
        }
    }
    public void resumeVideo() {
        if (null == mVideoSession || mVideoSession.isTerminated()) {
            return;
        }
        if(mRemoteSurfaceView != null) {
            mRemoteVideoChannel.startRender(mRemoteSurfaceView);
        }
        if (mVideoSession.isPaused()) {
            mVideoSession.setPause(false);
            mVideoSession.openCamera(mCamId);
            mVideoSession.startPreview(mLocalSurfaceView);
        }
    }
    public void destroyMediaEngine() {
        if (null != mAudioSession) {
            mAudioSession.setLoudspeakerOn(false);
            mAudioSession.terminate();
        }
        if (null != mVideoSession) {
            mVideoSession.closeCamera();
            mVideoSession.terminate();
        }
        if (mMediaEngine != null) {
            mMediaEngine.terminate();
        }
    }
}
