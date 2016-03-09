package com.example.wangxiangfx.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.feinno.sdk.BroadcastActions;
import com.feinno.sdk.dapi.AVCallManager;
import com.feinno.sdk.dapi.RCSManager;
import com.feinno.sdk.session.AvSession;
import com.feinno.sdk.session.AvSessionStates;
import com.ultrapower.umcs.AudioCodecType;
import com.ultrapower.umcs.VideoCodecType;

public class VoIPUI extends AppCompatActivity {
    private AvSession mAvSession;
    private UMCSUtil mUMCSUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vo_ipui);
        ViewGroup localPreview = (ViewGroup)findViewById(R.id.local_preview);
        ViewGroup remotePreview = (ViewGroup)findViewById(R.id.remote_preview);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mUMCSUtil = new UMCSUtil(this);
        localPreview.addView(mUMCSUtil.getLocalSurfaceView());
        remotePreview.addView(mUMCSUtil.getRemoteSurfaceView());
        mUMCSUtil.createAudioSession(0, AudioCodecType.AUDIO_CODEC_SILK);
        mUMCSUtil.createVideoSession(0, VideoCodecType.VIDEO_CODEC_VP8);
        mUMCSUtil.createRemoteAudioChannel(0);
        mUMCSUtil.createRemoteVideoChannel(0);
        mUMCSUtil.startLocalPreview(1);
        mUMCSUtil.startRemotePreview();

        processIntent(getIntent());
    }

    @Override
    protected void onDestroy() {
        mUMCSUtil.destroyMediaEngine();
        super.onDestroy();
    }

    public void onAccept(View v) {
        if (mAvSession == null) {
            return;
        }
        if (AvSessionStates.isIncomingOrOutgoingState(
                AvSessionStates.fromInt(mAvSession.statenum))) {
            try {
                AVCallManager.answer(mAvSession.selfnumber, mAvSession.id, false);
                Toast.makeText(this, "成功接口", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void onHungUp(View v) {
        if (mAvSession == null) {
            return;
        }
        if (AvSessionStates.canHungUpState(
                AvSessionStates.fromInt(mAvSession.statenum))) {
            try {
                AVCallManager.hungUp(mAvSession.selfnumber, mAvSession.id);
                Toast.makeText(this, "成功挂断", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                finish();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }

    private void processIntent(Intent intent) {
        AvSession session = intent.getExtras().getParcelable(BroadcastActions.EXTRA_SESSION);
        processAvSession(session);
    }
    private void processAvSession(AvSession session) {
        if (null == session) {
            return;
        }
        Log.d("av_session", session.toString());
        if (mAvSession != null && mAvSession.id != session.id) {
            Toast.makeText(this, "正在处理会话时，接收到新会话", Toast.LENGTH_LONG).show();
            return;
        }
        if (mAvSession != null && mAvSession.statenum == session.statenum) {
            Toast.makeText(this, "接收到新消息，但是状态没变化", Toast.LENGTH_LONG).show();
            return;
        }
        mAvSession = session;
        int stateNum = mAvSession.statenum;
        if (AvSessionStates.isNewSession(stateNum)) {
            try {
                AVCallManager.ring(mAvSession.selfnumber, mAvSession.id);
            } catch (Exception e) {
                Toast.makeText(this, "响应来电-invite-失败" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else if (AvSessionStates.isEndSessionState(stateNum)) {
            try {
                mAvSession = null;
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "响应来电-hungUp-失败" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else if (AvSessionStates.fromInt(stateNum) == AvSessionStates.Connected) {
            int localPort = (int) (Math.random()*(60000-1025)+1025);
            mUMCSUtil.startAudio(localPort, mAvSession.audioIp, Integer.valueOf(mAvSession.audioPort));
            mUMCSUtil.startVideo(localPort + 2, mAvSession.videoIp, Integer.valueOf(mAvSession.videoPort));

        } else {
            Toast.makeText(this, "未处理的被叫消息：" + mAvSession.statenum, Toast.LENGTH_LONG).show();
        }
    }

    public class iner {

    }
}
