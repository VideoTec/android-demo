package com.ultrapower.umcs;

/**
 * 传输类型
 */
public enum TransportType {

	/**
	 * 内部传输
	 */
	TRANSPORT_INTERNAL(0),

	/**
	 * 外部传输
	 */
	TRANSPORT_EXTERNAL(1),
	
	/**
	 * ICE传输
	 */
	TRANSPORT_ICE(2);
	private final int value;

	private TransportType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
