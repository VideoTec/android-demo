package com.ultrapower.umcs;

/**
 * 视频编码信息
 */
public class VideoCodecInfo {
	/**
	 * @return 视频编码类型
	 */
	public VideoCodecType getCodecType() {
		return codecType;
	}

	/**
	 * @return 视频载荷类型
	 */
	public int getPlTpye() {
		return plTpye;
	}

	/**
	 * @return the 视频编码的字符串表达
	 */
	public String getCodecName() {
		return codecName;
	}

	/**
	 * 视频编码信息
	 */
	private VideoCodecType codecType;
	/**
	 * 载荷类型
	 */
	private int plTpye;
	/**
	 * 编码名字
	 */
	private String codecName;

	VideoCodecInfo(VideoCodecType codecType, int plTpye, String codecName) {
		super();
		this.codecType = codecType;
		this.plTpye = plTpye;
		this.codecName = codecName;
	}

}