package com.ultrapower.umcs;

/**
 * ICE获取候选地址监听器.<br>
 * 当使用ICE方式传输时，此接口的实现者将负责获取候选地址列表。
 */
public interface OnCandidatesCreatedListener {
	/**
	 * 当收集到ICE候选地址列表时回调此方法
	 * @param candidateBuf
	 * 				候选地址列表
	 */
	void OnCandidatesCreated(int iceId,IceCandidate[] candidateBuf);
}
