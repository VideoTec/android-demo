package com.ultrapower.umcs;

public interface NetworkStateListener {
	/**
	 * 网络状态发生变化时回调此函数,只有视频才有网络状态回调功能
	 * 
	 * @param channelId
	 *            触发事件的视频通道id
	 *            
	 * @param state
	 *            NetworkState中定义的值
	 */
	public void onRemoteNetworkStateChanged(int videohannelId, NetworkState state);
	
	public void onLocalNetworkStateChanged(NetworkState state);
}
