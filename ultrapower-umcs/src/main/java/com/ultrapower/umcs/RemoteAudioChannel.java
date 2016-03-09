package com.ultrapower.umcs;

import android.util.Log;

/**
 * 音频通道，对应一个远端的音频会话.<br>
 * 
 * 
 * 通过<code>AudioSession.createRemoteChannel()</code> <br>
 * 当二人模式时，只存在一个音频通道。当多人模式时，可能存在多个音频通道
 */
public class RemoteAudioChannel {

	static {
		System.loadLibrary("umcs");
	}

	private int channelId;
	
	/**
	 * 获取通道id
	 * @return id
	 */
	public int getChannelId()
	{
		return channelId;
	}
	private int ssrc;
	
	/**
	 * 获取ssrc
	 * @return ssrc
	 */
	int getSsrc()
	{
		return ssrc;
	}
	private boolean isTerminated, isMute = false;

	private AudioChannelEventListener audioEventListener = null;

	private int audioEvent = 0;

	private AudioSession audioSession;

    RemoteAudioChannel(int channelId,int ssrc, AudioSession audioSession) {
		// TODO Auto-generated constructor stub
		this.channelId = channelId;
		isTerminated = false;
		this.audioSession = audioSession;
		this.ssrc = ssrc;
	}

	/**
	 * 音频事件,{@link #registerEventListener(AudioChannelEventListener, int)}方法使用，
	 * 音频通道不活跃事件，当此事件被触发时，表示相关音频通道无法收到远端的音频数据
	 */
	public static final int AUIDO_EVENT_DEAD = 0x0001;

	/**
	 * 音频事件,{@link #registerEventListener(AudioChannelEventListener, int)}方法使用，
	 * 音频通道检测到有活音的事件，当此事件被触发时，表示相关音频通道检查到有活音
	 */
	public static final int AUIDO_EVENT_VAD = 0x0002;

	/**
	 * 检测音频是否设置为静音状态,当为静音状态时，无法听到远端的声音
	 * 
	 * @return true 静音状态，false 非静音状态
	 */
	public boolean isMute() {
		return isMute;
	}

	/**
	 * 设置远端音频的静音状态，当设置远端音频为静音时，将不能听到对方的声音
	 * 
	 * @param mute
	 *            true 静音状态，false 非静音状态
	 */
	public void setMute(boolean mute) {
		if (0 > nativeSetRemoteAudioMute(channelId, mute)) {
			Log.e("remote audio channel", "set mute failed");
		} else {
			isMute = mute;
		}
	}

	private native int nativeSetRemoteAudioMute(int sessionId, boolean mute);

	/**
	 * 注册一个音频通道事件监听器,并设置要注册的音频事件，当音频通道有需要的事件发生时，会调用监听器相应的方法<br>
	 * 当重复注册监听器时，会使用新的audioEvent
	 * 
	 * @param listener
	 *            用来监听远程音频的监听器,
	 * @param audioEvent
	 *            需要监听的音频事件，可以使用组合方式 {@link #AUIDO_EVENT_DEAD}|
	 *            {@link #AUIDO_EVENT_VAD}
	 */
	public void registerEventListener(AudioChannelEventListener listener,
			int audioEvent) {
		audioEventListener = listener;
		this.audioEvent = audioEvent;
		if ((audioEvent & AUIDO_EVENT_DEAD) != 0) {
			nativeRegisterEventListener(this.channelId, AUIDO_EVENT_DEAD);
		}
	}

	private native void nativeRegisterEventListener(int sid, int audioEvent);

	/**
	 * 注销一个已经注册的音频通道事件监听器
	 * 
	 */
	public void unRegisterEventListener() {

		if ((audioEvent & AUIDO_EVENT_DEAD) != 0) {
			nativeUnRegisterEventListener(this.channelId, AUIDO_EVENT_DEAD);
		}
		audioEventListener = null;
		this.audioEvent = 0;
	}

	private native void nativeUnRegisterEventListener(int sid, int audioEnvent);

	/**
	 * 获取接受对方音频的统计数据
	 * 
	 * @return 统计数据  失败返回 null
	 *             无效状态
	 */
	public MediaStatistic collectStatistic(){
		MediaStatistic mediaStatistic = nativeCollectStatistic(this.channelId);
		return mediaStatistic;
	}
	private native MediaStatistic nativeCollectStatistic(int channelId);
	/**
	 * 销毁远端音频通道
	 */
	public void terminate() {
		if (!isTerminated) {
			if (deleteRemoteAudioChannel(channelId) >= 0) {
				isTerminated = true;
			}
		}
	}

	private int deleteRemoteAudioChannel(int channalId) {
		if(0 !=nativeDeleteRemoteAudioChannel(channalId))
		{
			return -1;
		}
		if(audioSession!=null)
		{
			audioSession.deleteRemoteChannel(this);
		}
		return 0;
	}

	private native int nativeDeleteRemoteAudioChannel(int channalId);

	void dispatchEvent(int event) {
		if (audioEventListener==null) {
			return;
		}
		if ((audioEvent&event)!=0) {
			audioEventListener.onAudioChannelEvent(this, event);
		}
	}
}
