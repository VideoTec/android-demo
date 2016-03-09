package com.ultrapower.umcs;

/**
 * 引擎发送数据监听器.<br>
 * 当使用外部传输时，此接口的实现者将负责引擎内数据的发送。
 * 
 */
public interface OnDataOutgoingListener {
	/**
	 * 当会话有RTP数据需要发送时回调此方法
	 * 
	 * @param data
	 *            数据内容
	 */
	void onRtpOutgoing(byte[] data);

	/**
	 * 当会话有RTCP数据需要发送时回调此方法
	 * 
	 * @param data
	 *            数据内容
	 */
	void onRtcpOutgoing(byte[] data);
}
