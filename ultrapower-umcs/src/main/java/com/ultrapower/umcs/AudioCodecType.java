package com.ultrapower.umcs;

/**
 * 音频编码类型
 */
public enum AudioCodecType {
	AUDIO_CODEC_ISAC(0),
	AUDIO_CODEC_ILBC(1), 
	AUDIO_CODEC_PCMU(2), 
	AUDIO_CODEC_AMRWB(3), 
	AUDIO_CODEC_SILK(4),
	AUDIO_CODEC_EVS(5),
	AUDIO_CODEC_OPUS(6);

	private final int value;

	AudioCodecType(int value) {
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