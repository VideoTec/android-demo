package com.ultrapower.umcs;

/**
 * 
 * 日志监听器
 */
public interface TraceLogger {

	/**
	 * 记录日志消息
	 * 
	 * @param level
	 *            日志级别
	 * @param msg
	 *            详细日志
	 */
	public void writeTrace(int level, String msg);
}
