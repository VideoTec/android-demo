package com.ultrapower.umcs;

import android.content.Context;

/**
 * 引擎配置
 */
public class EngineConfig {

	boolean isMultiMode() {
		return isMultiMode;
	}
	boolean isAudioUseSrtp()
	{
		return isAudioUseSrtp;
	}
	boolean isAudioUseSrtcp()
	{
		return isAudioUseSrtcp;
	}
	boolean isVideoUseSrtp()
	{
		return isVideoUseSrtp;
	}
	boolean isVideoUseSrtcp()
	{
		return isVideoUseSrtcp;
	}
	Context getContext() {
		return context;
	}

	TraceLevel getTraceLevel() {
		return traceLevel;
	}
	public String getAudioKey() {
		return audioKey;
	}

	public String getVideoKey() {
		return videoKey;
	}

	public String getAudioCipher() {
		return audioCipher;
	}

	public String getVideoCipher() {
		return videoCipher;
	}
	
	public NetworkStateListener getNetworkStateListener() {
		return networkStateListener;
	}
	
	private boolean isMultiMode;
	
	private Context context;
	
	private TraceLevel traceLevel;
	
	private boolean isAudioUseSrtp;
	
	private boolean isAudioUseSrtcp;
	
	private boolean isVideoUseSrtp;
	
	private boolean isVideoUseSrtcp;
	
	private String audioKey;
	
	private String videoKey;
	
	private String audioCipher;
	
	private String videoCipher;
	
	private NetworkStateListener networkStateListener;

	/**
	 * 音视频引擎配置信息
	 * 
	 * @param isMutiMode
	 *            是否为多人模式
	 * @param traceLevel
	 *            日志记录级别
	 * @param context
	 *            安卓Context
	 */
	public EngineConfig(boolean isMultiMode, 
			TraceLevel traceLevel,
			Context context, 
			boolean isAudioUseSrtp,
			boolean isAudioUseSrtcp, 
			boolean isVideoUseSrtp, 
			boolean isVideoUseSrtcp,  
			String audioKey, 
			SrtpCipherType audioCipher, 
			String videoKey, 
			SrtpCipherType videoCipher,
			NetworkStateListener networkStateListener) {
		super();
		this.isMultiMode = isMultiMode;
		this.context = context;
		this.traceLevel = traceLevel;
		this.isAudioUseSrtp = isAudioUseSrtp;
		this.isAudioUseSrtcp = isAudioUseSrtcp;
		this.isVideoUseSrtp = isVideoUseSrtp;
		this.isVideoUseSrtcp = isVideoUseSrtcp;
		this.audioKey = audioKey;
		this.networkStateListener = networkStateListener;
		if(audioCipher == SrtpCipherType.AES_CM_128_HMAC_SHA1_32)
		{
			this.audioCipher = "AES_CM_128_HMAC_SHA1_32";
		}else if(audioCipher == SrtpCipherType.AES_CM_128_HMAC_SHA1_80)
		{
			this.audioCipher = "AES_CM_128_HMAC_SHA1_80";
		}else if(audioCipher == SrtpCipherType.NONE)
		{
			this.audioCipher = "";
		}
		
		this.videoKey = videoKey;
		if(videoCipher == SrtpCipherType.AES_CM_128_HMAC_SHA1_32)
		{
			this.videoCipher = "AES_CM_128_HMAC_SHA1_32";
		}else if(videoCipher == SrtpCipherType.AES_CM_128_HMAC_SHA1_80)
		{
			this.videoCipher = "AES_CM_128_HMAC_SHA1_80";
		}else if(videoCipher == SrtpCipherType.NONE)
		{
			this.videoCipher = "";
		}
		//srtp 两种加密方式
		//"AES_CM_128_HMAC_SHA1_80";
		//"AES_CM_128_HMAC_SHA1_32";
	}

}
