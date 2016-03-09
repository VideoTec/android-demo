package com.ultrapower.umcs;

/**
 * 音频编码信息
 */
public class AudioCodecInfo {

	/**
	 * 获得音频编码类型
	 * 
	 * @return 音频编码类型
	 */
	public AudioCodecType getCodecType() {
		return codecType;
	}

	/**
	 * 获得音频编码类型的字符串表达
	 * 
	 * @return 音频编码类型的字符串
	 */
	public String getName() {
		return name;
	}

	/**
	 * 获得音频编码的采样率
	 * 
	 * @return 采样率
	 */
	public int getFreq() {
		return freq;
	}

	/**
	 * 获得音频编码的通道数
	 * 
	 * @return 通道数
	 */
	public int getChannels() {
		return channels;
	}

	/**
	 * 获得音频编码的载荷类型
	 * 
	 * @return 载荷类型
	 */
	public int getPlType() {
		return plType;
	}

	/**
	 * 音频编码类型
	 */
	private AudioCodecType codecType;
	/**
	 * 编码的名字
	 */
	private String name;
	/**
	 * 音频采样频率
	 */
	private int freq;
	/**
	 * 音频通道数量
	 */
	private int channels;
	/**
	 * 载荷类型
	 */
	private int plType;

	AudioCodecInfo(Object codecType, String name, int freq, int channels,
			int plType) {
		super();
		this.codecType = (AudioCodecType) codecType;
		this.name = name;
		this.freq = freq;
		this.channels = channels;
		this.plType = plType;
	}

	AudioCodecInfo() {
	}

	public AudioCodecInfo clone() {
		return new AudioCodecInfo(codecType, name, freq, channels, plType);
	}

}