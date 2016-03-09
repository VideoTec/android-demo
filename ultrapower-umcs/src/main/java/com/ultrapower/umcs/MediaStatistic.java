package com.ultrapower.umcs;

/**
 * 媒体统计信息
 */
public class MediaStatistic {


	/**
	 * 获得往返时延
	 * @return 往返时延
	 */
	public int getRtt() {
		return rtt;
	}

	/**
	 * 获得接收的包数
	 * @return 接收的包数
	 */
	public int getIncomingPacketCount() {
		return incomingPacketCount;
	}

	/**
	 * 获得发送的包数
	 * @return 发送的包数
	 */
	public int getOutgoingPacketCount() {
		return outgoingPacketCount;
	}

	/**
	 * 获得累计接收丢包数
	 * @return 累计接收丢包数
	 */
	public int getIncomingPacketCumulateLost() {
		return incomingPacketCumulateLost;
	}

	/**
	 * 获得累计发送丢包数
	 * @return 累计发送丢包数
	 */
	public int getOutgoingPacketCumulateLost() {
		return outgoingPacketCumulateLost;
	}

	/**
	 * 获得接收丢包率
	 * @return 接收丢包率
	 */
	public int getIncomingPacketLostRate() {
		return incomingPacketLostRate;
	}

	/**
	 * 获得发送丢包率
	 * @return 发送丢包率
	 */
	public int getOutgoingPacketLostRate() {
		return outgoingPacketLostRate;
	}

	/**
	 * 往返时延
	 */
	int rtt;
    /**
     * 接收的包数
     */
    int incomingPacketCount;
    /**
     * 发送的包数
     */
    int outgoingPacketCount;
    
    
    /**
     * 累计接收丢包数
     */
    int incomingPacketCumulateLost;
    /**
     * 累计发送丢包数
     */
    int outgoingPacketCumulateLost;
    
    /**
     * 接收丢包率
     */
    int incomingPacketLostRate;
    /**
     * 发送丢包率
     */
    int outgoingPacketLostRate;

	MediaStatistic(int rtt, int incomingPacketCount, int outgoingPacketCount,
			int incomingPacketCumulateLost, int outgoingPacketCumulateLost,
			int incomingPacketLostRate, int outgoingPacketLostRate) {
		super();
		this.rtt = rtt;
		this.incomingPacketCount = incomingPacketCount;
		this.outgoingPacketCount = outgoingPacketCount;
		this.incomingPacketCumulateLost = incomingPacketCumulateLost;
		this.outgoingPacketCumulateLost = outgoingPacketCumulateLost;
		this.incomingPacketLostRate = incomingPacketLostRate;
		this.outgoingPacketLostRate = outgoingPacketLostRate;
		
	}

}
