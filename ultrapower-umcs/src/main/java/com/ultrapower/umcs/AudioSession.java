package com.ultrapower.umcs;

import java.util.ArrayList;

import android.util.Log;

/**
 * 
 * 音频会话.<br>
 * 当需要进行音频会话时，通过引擎创建此类实例<br>
 * 1. 配置传输信息{@link #configTransport(Transport)}<br>
 * 2. 设置发送编码{@link #setSendCodec(AudioCodecType)}，设置扬声器类型
 * {@link #setLoudspeakerOn(boolean)}，若未设置将采用默认配置<br>
 * 3. 创建音频通道{@link #createRemoteChannel()}，用来接受远端的音频会话信息<br>
 * 4. 使用{@link #start()}方法让音频会话开始工作
 */
public class AudioSession {

	static {
		System.loadLibrary("umcs");
	}

	private int sessionId;

	private boolean isTerminated;

	private ArrayList<RemoteAudioChannel> remoteAudioChannelList = new ArrayList<RemoteAudioChannel>();

	private boolean isStart;

	private AudioCodecType sendCodecType;

	private Transport transportInfo;

	private static String Tag = "AudioSession";

	/**
	 * 获取会话ID
	 * @return 会话ID
	 */
	public int getSessionId() {
		return sessionId;
	}

	/**
	 * 初始化函数
	 * @param sessionId 会话ID
	 */
	public AudioSession(int sessionId) {
		this.sessionId = sessionId;
		isStart = false;
		isTerminated = false;
		transportInfo = null;
		sendCodecType = AudioCodecType.AUDIO_CODEC_SILK;
	}

	/**
	 * 检查会话是否在启动状态
	 * 
	 * @return true 在启动状态，false 在停止状态
	 */
	public boolean isStarted() {
		return isStart;
	}

	/**
	 * 返回当前扬声器状态
	 * 
	 * @return true 扬声器打开状态，false 扬声器关闭状态
	 */
	public boolean isLoudspeakerOn() {
		return nativeIsLoudspeakerOn();
	}

	private native boolean nativeIsLoudspeakerOn();

	/**
	 * 设置扬声器状态
	 * 
	 * @param on
	 *            true 打开扬声器，false 关闭扬声器
	 */
	public void setLoudspeakerOn(boolean on) {
		nativeSetLoudspeakerOn(on);
	}

	private native void nativeSetLoudspeakerOn(boolean on);

	/**
	 * 检查音频是否是静音状态
	 * 
	 * @return true 静音状态，false 非静音状态
	 */
	public boolean isMute() {
		return nativeIsMute();
	}

	private native boolean nativeIsMute();

	/**
	 * 设置音频静音或非静音状态
	 * 
	 * @param mute
	 *            true 设置为静音状态，false 非静音状态
	 */
	public void setMute(boolean mute) {
		nativeSetMute(mute);
	}

	private native void nativeSetMute(boolean mute);

	/**
	 * 设置音频发送编码类型，可在音频会话启动前或启动后配置
	 * 
	 * @param codec
	 *            需要设置的发送编码类型
	 */
	public void setSendCodec(AudioCodecType codec) {
		sendCodecType = codec;
		nativeSetSendCodec(codec);
	}

	private native void nativeSetSendCodec(AudioCodecType codec);

	/**
	 * 获取当前会话是否结束
	 * 
	 * @return true 结束，false 未结束
	 */
	public boolean isTerminated() {
		return isTerminated;
	}

	/**
	 * 获取当前音频发送编码类型
	 * 
	 * @return 当前编码类型
	 */
	public AudioCodecType getSendCodec() {
		return sendCodecType;
	}

	/**
	 * 设置音频传输配置，在调用start方法前执行
	 * 
	 * @param config
	 *            传输配置信息
	 * @see Transport
	 */
	public boolean configTransport(Transport config) {
		if(isStart)
		{
			Log.e(Tag, "Config transport but audio session is started");
			return false;
		}
		if (config == null)
		{
			Log.e(Tag, "Config transport config is null");
			return false;
		}

		if (config.transportType == TransportType.TRANSPORT_INTERNAL) {
			if (config.localIp == null) {
				return false;
			}
			if (config.remoteIp == null) {
				return false;
			}

			this.transportInfo = config;

		} else if (config.transportType == TransportType.TRANSPORT_EXTERNAL) {
			
			this.transportInfo = config;
			
		} else if(config.transportType == TransportType.TRANSPORT_ICE)
		{
			this.transportInfo = config;
		}else {
			Log.i(Tag, "transport type is error");
			return false;
		}
		
		return true;
	}

	/**
	 * 开始音频，本地音频开始发送到远端，同时开始接受远端音频并播放
	 */
	public boolean start() {
		if (this.isStart == true) {
			return false;
		}
		if (this.transportInfo == null) {
			return false;
		}
		int ret = nativeConfigTransport(transportInfo);
		if (ret != 0) {
			return false;
		}
		ret = nativeStartAudioSession();
		if (ret == 0) {
			this.isStart = true;
		} else {
			return false;
		}
		Log.i(Tag, "audio start success");
		return true;
	}

	private native int nativeConfigTransport(Transport transport);

	private native int nativeStartAudioSession();

	/**
	 * 关闭音频，本地音频停止发送到远端，同时不再接受远端音频
	 */
	public void stop() {
		if (!isStart) {
			return;
		}
		isStart = false;
		nativeStopAudioSession();

	}

	private native int nativeStopAudioSession();


	public AudioSession() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 销毁音频会话，包括所有的远端音频通道， 释放所有会话的资源
	 */
	public void terminate() {
		if (!isTerminated) {
			if (isStart) {
				stop();
			}
			ArrayList <RemoteAudioChannel> tmpList = new ArrayList<RemoteAudioChannel>(remoteAudioChannelList);
			for (RemoteAudioChannel remoteAudioChannel : tmpList) {
				remoteAudioChannel.terminate();
			}
			remoteAudioChannelList.clear();
			nativeDeleteLocalAudioChannel();
		}
		remoteAudioChannelList = null;
		isTerminated = true;
	}

	/**
	 * 创建远端音频，用来接收远端音频信息并播放，使用随机的ssrc 不可以使用SRTP加密
	 * 
	 * @return 远端音频通道
	 */
	public RemoteAudioChannel createRemoteChannel(boolean useSrtp, boolean useSrtcp, SrtpCipherType audioCipher, String audioKey) {
		return createRemoteChannel(0,useSrtp,useSrtcp,audioCipher,audioKey);
	}

	/**
	 * 创建远端音频，其中远端音频采用指定的ssrc 可以使用SRTP加密
	 * 
	 * @param ssrc
	 *            在远端音频中要使用的ssrc
	 * 		  audioKey 
	 * 			  解码秘钥,在不使用SRTP是可以传入"",但不能穿null
	 *        audioCipher 
	 *           解密方式
	 * @return 远端音频通道  失败返回null
	 */
	public RemoteAudioChannel createRemoteChannel(int ssrc, boolean useSrtp, boolean useSrtcp, SrtpCipherType audioCipher, String audioKey) {
		if(audioKey == null)
		{
			audioKey = "";
		}

		int channelId = nativeCreateRemoteChannel(ssrc,useSrtp,useSrtcp,audioCipher.toString(),audioKey);
		if (channelId < 0) {
			return null;
		}
		RemoteAudioChannel tmpAudioChannel = new RemoteAudioChannel(channelId,ssrc,
				this);

		remoteAudioChannelList.add(tmpAudioChannel);

		return tmpAudioChannel;
	}
    void deleteRemoteChannel(RemoteAudioChannel remoteChannel)
    {
    	for (RemoteAudioChannel remoteAudioChannel : remoteAudioChannelList) {
    		if(remoteAudioChannel.getChannelId() == remoteChannel.getChannelId())
    		{
    			remoteAudioChannelList.remove(remoteAudioChannel);
    			break;
    		}
			
		}
    }
	private native int nativeCreateRemoteChannel(int ssrc, boolean useSrtp, boolean useSrtcp, String audioCipher, String audioKey);

	private native int nativeDeleteLocalAudioChannel();

	/**
	 * 通过ssrc查找远端音频通道
	 * @param ssrc
	 * @return 音频通道
	 */
	public RemoteAudioChannel findRemoteChannelBySsrc(int ssrc)
	{
		for (RemoteAudioChannel remoteAudioChannel : remoteAudioChannelList) {
    		if(remoteAudioChannel.getSsrc() == ssrc)
    		{
    			return remoteAudioChannel;
    		}
			
		}
		return null;
	}
	/**
	 * 当使用外部传输时，收到远端音频RTP后，使用此方法向引擎投递数据
	 * 
	 * @param data
	 *            外部传输收到的音频RTP数据
	 */
	public void rtpDataIncoming(byte[] data) {
		nativeRtpDataIncoming(data);
	}

	private native void nativeRtpDataIncoming(byte[] data);

	/**
	 * 当使用外部传输时，收到远端音频RTCP后，使用此方法向引擎投递数据
	 * 
	 * @param data
	 *            外部传输收到的音频RTCP数据
	 */
	public void rtcpDataIncoming(byte[] data) {
		nativeRtcpDataincoming(data);
	}

	private native void nativeRtcpDataincoming(byte[] data);

	/**
	 * 传入远端的候选地址列表
	 * @param iceCandidates
	 * 			候选传输地址列表
	 */
	public void receiveRemoteCandidates(IceCandidate[] iceCandidates)
	{
		nativeReceiveRemoteCandidates(iceCandidates);
	}
	private native void nativeReceiveRemoteCandidates(IceCandidate[] iceCandidates);
	
	public native void setRemoteAuthenticationInfo(String userName,String password);
	
	void outgoingData(byte[] data, boolean isRtcp) {
		if (isTerminated) {
			return;
		}
		if (transportInfo == null
				|| transportInfo.transportType != TransportType.TRANSPORT_EXTERNAL) {
			return;
		}

		if (transportInfo.getOnOutgoingListener() == null) {
			return;
		}
		if (!isRtcp) {
			transportInfo.getOnOutgoingListener().onRtpOutgoing(data);
		} else {
			transportInfo.getOnOutgoingListener().onRtcpOutgoing(data);
		}
	}

	 void dispatchEvent(int sid, int event) {
		 if (remoteAudioChannelList.size()==0) {
			return;
		}
		 for (RemoteAudioChannel channel : remoteAudioChannelList) {
			 if (channel.getChannelId()==sid) {
				channel.dispatchEvent(event);
			}
		}
		 
	}

	void onCandidatesCreated(IceCandidate[] candidateBuf)
	{
		if (transportInfo == null
				|| transportInfo.transportType != TransportType.TRANSPORT_ICE) {
			return;
		}
		
		transportInfo.onCandidatesCreatedListener.OnCandidatesCreated(transportInfo.iceId,candidateBuf);
	}
};
