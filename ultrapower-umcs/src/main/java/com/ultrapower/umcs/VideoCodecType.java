package com.ultrapower.umcs;

/**
 * 视频编码类型
 * 
 */
public enum VideoCodecType {
	/**
	 * VP8类型
	 */
	VIDEO_CODEC_VP8(0),
	/**
	 * H264类型
	 */
	VIDEO_CODEC_H264(1);
	private final int value;

	private VideoCodecType(int value) {
		this.value = value;
	}

	/**
	 * Get the value.
	 * 
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
}
