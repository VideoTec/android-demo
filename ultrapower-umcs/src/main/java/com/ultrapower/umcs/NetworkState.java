package com.ultrapower.umcs;

public enum NetworkState {
	NETWORK_STATE_UNKNOWN(0),
	//BAD表示卡顿严重
    NETWORK_STATE_BAD(1),
    //LOW表示低清，但是流畅
    NETWORK_STATE_LOW(2),
    //NORMAL表示一般，清晰流畅
    NETWORK_STATE_NORMAL(3),
    //GOOD表示很好，高清流畅
    NETWORK_STATE_GOOD(4);
    
	private final int value;
	
	NetworkState(int value) {
		this.value = value;
	}

	/**
	 * 获取当前的枚举类型的值
	 * @return  类型值
	 */
	public int getValue() {
		return value;
	}
}
