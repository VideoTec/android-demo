package com.ultrapower.umcs;

import android.util.Log;
import android.view.SurfaceView;

/**
 * 
 * 视频通道，对应一个远端的视频会话.<br>
 * 
 * 
 * 当二人视频时，只存在一个视频通道。当多人视频时，可能存在多个视频通道
 */
public class RemoteVideoChannel {

	static {
		System.loadLibrary("umcs");
	}

	/**
	 * 视频通道不活跃事件，当此事件被触发时，表示相关视频通道无法收到远端的视频数据
	 */
	public static final int VIDEO_EVENT_DEAD = 0x0001;
	/**
	 * 视频绘制第一帧事件，当此事件被触发时，表示相关视频通道获取到第一帧数据并将进行绘制
	 */
	public static final int VIDEO_EVENT_FIRSTRENDER = 0x0002;

	private int channelId;

	/**
	 * 获取通道ID
	 * @return 通道ID
	 */
	int getChannelId()
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
	
	private boolean isTerminated;

	private VideoSession videoSession = null;

	private VideoChannelEventListener videoEventListener = null;

	private int videoEvent = 0;

	RemoteVideoChannel(int videoChannelId,int ssrc, VideoSession videoSession) {
		// TODO Auto-generated constructor stub
		this.channelId = videoChannelId;
		this.isTerminated = false;
		this.videoSession = videoSession;
		this.ssrc = ssrc;
	}

	/**
	 * 停止视频渲染
	 */
	public void stopRender() {
		nativeStopRemoteRender(channelId);
	}

	private native int nativeStopRemoteRender(int channelId);

	/**
	 * 开始视频渲染
	 * 
	 * @param view
	 *            渲染View
	 */
	public void startRender(SurfaceView view) {
		int ret = nativeStartRemoteRender(channelId, view);
		if(ret != 0)
		{
			Log.e("RemoteVideoChannel", "start remote render failed");
		}
	}

	private native int nativeStartRemoteRender(int channelId, SurfaceView view);

	/**
	 * 注册一个视频通道事件监听器,并设置要注册的视频事件，当视频通道有需要的事件发生时，会调用监听器相应的方法<br>
	 * 当重复注册监听器时，会使用新的videoEvent
	 * 
	 * @param listener
	 *            视频通道监听器
	 * @param videoEvent
	 *            需要监听的事件
	 */
	public void registerEventListener(VideoChannelEventListener listener,
			int videoEvent) {
		if ((videoEvent & VIDEO_EVENT_DEAD) != 0) {
			nativeRegisterEventListener(this.channelId, VIDEO_EVENT_DEAD);
		}
		this.videoEventListener = listener;
		this.videoEvent = videoEvent;
	}

	private native void nativeRegisterEventListener(int sid, int videoEnvent);

	/**
	 * 注销一个已经注册的视频通道时间监听器
	 * 
	 */
	public void unRegisterEventListener() {
		if ((videoEvent & VIDEO_EVENT_DEAD) != 0) {
			nativeUnRegisterEventListener(this.channelId, VIDEO_EVENT_DEAD);
		}
		this.videoEvent = 0;
		this.videoEventListener = null;
	}

	private native void nativeUnRegisterEventListener(int sid, int videoEvent);

	/**
	 * 获取接受对方视频的统计数据
	 * 
	 * @return 统计数据 失败返回 null
	 *             无效状态
	 */
	public MediaStatistic collectStatistic(){
		return nativeCollectStatistic(this.channelId);
	}
	private native MediaStatistic nativeCollectStatistic(int channelId);
	/**
	 * 销毁远端视频通道
	 */
	public void terminate() {
		if (!isTerminated) {
			if (deleteRemoteVideoChannel() >= 0) {
				isTerminated = true;
			}

		}

	}

	private int deleteRemoteVideoChannel() {
		if(0 !=nativeDeleteRemoteVideoChannel(channelId))
		{
			return -1;
		}
		if(videoSession!=null)
		{
			videoSession.deleteRemoteChannel(this);
		}
		return 0;
	}

	private native int nativeDeleteRemoteVideoChannel(int channalId);
	
	void dispatchEvent(int event) {
		if (videoEventListener==null) {
			return;
		}
		if ((videoEvent&event)!=0) {
			videoEventListener.onVideoChannelEvent(this, event);
		}
	}
}
