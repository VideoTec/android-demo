package com.ultrapower.umcs;

public class H264FileWriter {
	
	static {
		System.loadLibrary("umcs");
	}
	
	private static final H264FileWriter INSTANCE = new H264FileWriter();

	private H264FileWriter() {
	}
	
	/**
	 * 获取音视频引擎的唯一实例
	 * 
	 * @return 音视频引擎
	 */
	public static H264FileWriter getInstance() {
		return INSTANCE;
	}
	
	/**
     * 开始录制
	 * 
	 * @param filename
	 *            文件名，需要保证有权限打开，如果存在将会清空内容
	 * @param srcW
	 *            输入帧的宽度
	 * @param srcH
	 *            输入的高度
	 * @param dstW
	 *            输出的宽度
	 * @param dstH
	 *            输出的高  
	 * @param startW
	 *            起始点距离左上角的宽度，用于确定图像截取的起始点坐标，便于截取任意区域
	 * @param startH
	 *            起始点距离左上角的高度 
	 * 
	 */
	public int StartRecodeH264VideoFile(String filename,int srcW, int srcH, int dstW, int dstH, int startW, int startH, int bitrate, int fps)
	{
		return nativeStartRecodeH264VideoFile(filename,srcW,srcH,dstW,dstH,startW,startH,bitrate,fps);
	}

	/**
     * 传入采集的原始帧
	 * 
	 * @param data
	 *            帧数据,NV21,NV12格式的帧长度为:width*height*1.5
	 * @param frameType
	 *            帧的类型，如安卓采集使用ImageFormat.NV21，则传入RAW_VIDEO_TYPE_NV21
	 * @return 成功返回0，失败返回-1
	 */
	public int InputRawVideoFrameForH264VideoFile(byte[] data, RawVideoType frameType)
	{
		return nativeInputRawVideoFrameForH264VideoFile(data, frameType);
	}
	
	/**
     * 停止录制，写入文件
	 * @return 成功返回0，失败返回-1
	 */
	public int StopRecodeH264VideoFile()
	{
		return nativeStopRecodeH264VideoFile();
	}
	
	private native int nativeStartRecodeH264VideoFile(String filename,int srcW, int srcH, int dstW, int dstH, int startW, int startH, int bitrate,int fps);
	private native int nativeInputRawVideoFrameForH264VideoFile(byte[] data, RawVideoType frameType);
	private native int nativeStopRecodeH264VideoFile();
}
