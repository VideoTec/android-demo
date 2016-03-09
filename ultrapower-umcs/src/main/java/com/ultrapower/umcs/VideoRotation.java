package com.ultrapower.umcs;

/**
 * 本地视频的旋转角度枚举
 */
public enum VideoRotation {
	/**
	 * 摄像头无旋转
	 */
	ROTATION_0(0),
	/**
	 * 顺时针方向旋转90度
	 */
	ROTATION_CLOCKWISE_90(1),
	/**
	 * 旋转180度
	 */
	ROTATION_180(2),
	/**
	 * 顺时针方向旋转270度
	 */
	ROTATION_CLOCKWISE_270(3);
	private final int value;

	private VideoRotation(int value) {
		this.value = value;
	}

	/**
	 * 获得旋转角度的枚举值
	 * @return
	 */
	public int getValue() {
		return value;
	}
}
