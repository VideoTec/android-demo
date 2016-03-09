package com.ultrapower.umcs;

public enum NetworkType {
    NETWORK_TYPE_UNKNOW(0),
    NETWORK_TYPE_WWAN(1),
    NETWORK_TYPE_WIFI(2);
    
	private final int value;
	
	NetworkType(int value) {
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
