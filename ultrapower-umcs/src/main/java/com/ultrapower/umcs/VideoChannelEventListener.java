package com.ultrapower.umcs;

/**
 * 
 * 视频事件回调接口.<br>
 * 当视频通道有事件发生时回调此接口的相关方法
 */
public interface VideoChannelEventListener {
	/**
	 * 当视频通道有事件发生时回调此方法
	 * 
	 * @param channel
	 *            触发事件的视频通道
	 * @param audioEvent
	 *            注册的视频事件类型，VideoChannel.VIDEO_EVENT_DEAD等。
	 */
	public void onVideoChannelEvent(RemoteVideoChannel channel, int videoEvent);
}
