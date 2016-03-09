package com.example.wangxiangfx.demo;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.cmcc.sso.sdk.auth.AuthnConstants;
import com.cmcc.sso.sdk.auth.AuthnHelper;
import com.cmcc.sso.sdk.auth.TokenListener;
import com.cmcc.sso.sdk.util.SsoSdkConstants;
import com.feinno.sdk.dapi.LoginManager;
import com.feinno.sdk.dapi.RCSManager;

import org.json.JSONObject;

public class DemoACT extends AppCompatActivity {
    private final static String TAG = DemoACT.class.getSimpleName();
    private EditText mRemoteIP;
    private Context mContext;
    private AuthnHelper mAthnHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.content_demo_act);

        mRemoteIP = (EditText)findViewById(R.id.remote_ip);
        mAthnHelper = new AuthnHelper(this);
        mAthnHelper.setDefaultUI(false);
        mAthnHelper.cleanSSO(new TokenListener() {
            @Override
            public void onGetTokenComplete(JSONObject jsonObject) {

            }
        });
    }

    public void onOpenP2P(View v) {
        String remoteIp = mRemoteIP.getText().toString();
        Intent intent = new Intent(this, P2PCallAct.class);
        intent.putExtra("remote-ip", remoteIp);
        startActivity(intent);
    }
    public void onOpenPhone1ForService(View v) {
        Intent intent = new Intent(this, ServiceAct.class);
        intent.putExtra("audio-ssrc-id", 7);
        intent.putExtra("video-ssrc-id", 8);
        startActivity(intent);
    }
    public void onOpenPhone2ForService(View v) {
        Intent intent = new Intent(this, ServiceAct.class);
        intent.putExtra("audio-ssrc-id", 9);
        intent.putExtra("video-ssrc-id", 10);
        startActivity(intent);
    }
    public void onOpenBroadcastAct(View v) {
        Intent intent = new Intent(this, TestBroadcastReceiver.class);
        intent.putExtra("audio-ssrc-id", 9);
        intent.putExtra("video-ssrc-id", 10);
        startActivity(intent);
    }
    private void showMsg(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
    public void onTest(View v) {
        startActivity(new Intent(this, VoIPUI.class));
    }

    String mOwner;
    public void onLogout(View v) {
        if (mOwner != null) {
            try {
                LoginManager.logout(mOwner, null);
                Toast.makeText(this, "logout success", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "logout ex: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
    public void onLogin(View v) {
        mAthnHelper.getAppPassword("01000167", "ABE85CBF0321F25E", "", "default",
                new TokenListener(){
                    @Override
                    public void onGetTokenComplete(JSONObject jsonObject) {
                        int resultCode = jsonObject.optInt(SsoSdkConstants.VALUES_KEY_RESULT_CODE, -1);
                        if (AuthnConstants.CLIENT_CODE_SUCCESS == resultCode) {
                            String username = jsonObject.optString(SsoSdkConstants.VALUES_KEY_USERNAME, null);
                            String password = jsonObject.optString(SsoSdkConstants.VALUES_KEY_PASSWORD, null);
                            String token = jsonObject.optString(SsoSdkConstants.VALUES_KEY_TOKEN, null);
                            mOwner = username;
                            try {
                                RCSManager.startUser(username, Utils.getDeviceId(mContext), Utils.getImsi(mContext));
                                LoginManager.setDm(username, "221.176.29.129", "80", "443");
                                LoginManager.login(username, password, token, null);
                                showMsg("login success");
                            } catch (Exception e) {
                                showMsg("login fail: " + e.getMessage());
                            }
                        } else {
                            showMsg("fail to get token: " + resultCode);
                        }
                    }
                });
    }
}
