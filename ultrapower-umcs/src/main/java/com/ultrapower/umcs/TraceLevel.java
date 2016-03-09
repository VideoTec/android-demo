package com.ultrapower.umcs;

/**
 * 日志级别枚举
 * 
 */
public enum TraceLevel {
	/**
	 * 无日志
	 */
	TRACE_NONE(0x0000),
	/**
	 * 状态变化日志
	 */
	TRACE_STATEINFO(0x0001),
	/**
	 * 警告日志
	 */
	TRACE_WARNING(0x0002),
	/**
	 * 错误日志
	 */
	TRACE_ERROR(0x0004),
	/**
	 * 调试日志
	 */
	TRACE_DEBUG(0x0800),
	/**
	 * 信息日志
	 */
	TRACE_INFO(0x1000),
	/**
	 * 所有日志
	 */
	TRACE_ALL(0xffff);
	private final int value;

	private TraceLevel(int value) {
		this.value = value;
	}

	/**
	 * 获得日志级别值
	 * @return 日志级别枚举值
	 */
	public int getValue() {
		return value;
	}
}