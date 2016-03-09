package com.ultrapower.umcs;

/**
 * 音频事件回调接口. 当音频通道有事件发生时回调此接口的相关方法
 */
public interface AudioChannelEventListener {
	/**
	 * 当音频通道有事件发生时回调此方法
	 * 
	 * @param channel
	 *            触发事件的音频通道
	 * @param audioEvent
	 *            注册的音频事件类型，AudioChannel.AUIDO_EVENT_VAD等。
	 */
	public void onAudioChannelEvent(RemoteAudioChannel channel, int audioEvent);

}
