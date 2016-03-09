package com.ultrapower.umcs;

//摄像头采集到得原始数据格式
public enum RawVideoType {

    RAW_VIDEO_TYPE_I420(0),
    RAW_VIDEO_TYPE_YV12(1),
    RAW_VIDEO_TYPE_YUY2(2),
    RAW_VIDEO_TYPE_UYVY(3),
    RAW_VIDEO_TYPE_IYUV(4),
    RAW_VIDEO_TYPE_ARGB(5),
    RAW_VIDEO_TYPE_RGB24(6),
    RAW_VIDEO_TYPE_RGB565(7),
    RAW_VIDEO_TYPE_ARGB4444(8),
    RAW_VIDEO_TYPE_ARGB1555(9),
    RAW_VIDEO_TYPE_MJPEG(10),
    RAW_VIDEO_TYPE_NV12(11),
    RAW_VIDEO_TYPE_NV21(12),
    RAW_VIDEO_TYPE_BGRA(13),
    RAW_VIDEO_TYPE_Unknown(99);
    
	private final int value;
																								
	RawVideoType(int value) {
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
