package com.ultrapower.umcs;
import java.util.ArrayList;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.view.SurfaceView;

/**
 * 
 * 视频会话.<br>
 * 当需要进行视频会话时，通过MediaEngine创建此类实例.<br>
 * 1. 配置传输信息{@link #configTransport(Transport)}<br>
 * 2. 设置发送编码{@link #setSendCodec(VideoCodecType)}若未设置将采用默认配置<br>
 * 3. 创建视频通道{@link #createRemoteChannel()}，用来接受远端的视频会话信息<br>
 * 4. 使用{@link #start()}方法让视频会话开始工作
 */
public class VideoSession {

	static {
		System.loadLibrary("umcs");
	}
	/**
	 * 会话ID
	 */
	private int sessionId;

	private boolean isStart;

	private boolean isPause;

	private int cameraIndex;

	private boolean isCameraOpen;

	private boolean isPreviewOn;

	private boolean isTerminated;

	private VideoRotation rotation;

	private VideoCodecType videocodec;

	private String tag = "VideoSession";

	private Transport transportInfo;

	private ArrayList<RemoteVideoChannel> remoteVideoChannelList = new ArrayList<RemoteVideoChannel>();

	private String Tag = this.getClass().toString();

	/**
	 * 获取会话ID
	 * 
	 * @return 会话ID
	 */
	 int getSessionId() {
		return sessionId;
	}

	/**
	 * 视频会话
	 * @param sessionId 视频会话ID
	 */
	public VideoSession(int sessionId) {
		this.sessionId = sessionId;
		isStart = false;
		isPause = false;
		isCameraOpen = false;
		cameraIndex = -1;
		isPreviewOn = false;
		isTerminated = false;
		transportInfo = null;
		videocodec = VideoCodecType.VIDEO_CODEC_VP8;
	}

	/**
	 * 检查会话是否在启动状态
	 * 
	 * @return true 在启动状态，false 在停止状态
	 */
	public boolean isStarted() {
		return isStart;
	}

	/**
	 * 获取当前视频是否为暂停状态
	 * 
	 * @return true 视频为暂停状态,false 视频为非暂停状态
	 */
	public boolean isPaused() {
		return isPause;
		// return nativeIsVideoPaused();
	}

	/**
	 * 获取当前会话是否结束
	 * 
	 * @return true 结束，false 未结束
	 */
	public boolean isTerminated() {
		return isTerminated;
	}

	private native boolean nativeIsVideoPaused();

	/**
	 * 设置当前视频为暂停或非暂停状态
	 * 
	 * @param pause
	 *            true 设置视频为暂停状态,false 设置视频为非暂停状态
	 */
	public void setPause(boolean pause) {
		if (nativeSetVideoPause(pause) == 0) {
			isPause = pause;
		} else {
			Log.i(tag, "set video pause failed");
		}
	}

	private native int nativeSetVideoPause(boolean pause);

	/**
	 * 获取当前打开的摄像头索引
	 * 
	 * @return 已打开的摄像头索引
	 */
	public int getCameraIndex() {
		return cameraIndex;
	}

	/**
	 * 打开指定索引的摄像头
	 * 
	 * @param cameraIndex
	 *            要打开摄像头的索引值
	 */
	public void openCamera(int cameraIndex) {
		int ret = nativeOpenCamera(cameraIndex);
		if (ret < 0) {
			isCameraOpen = false;
			Log.e(tag, "open camera failed");
			return;
		}
		isCameraOpen = true;

		CameraInfo cameraInfo = new CameraInfo();
		Camera.getCameraInfo(cameraIndex, cameraInfo);
		VideoRotation rotation = VideoRotation.ROTATION_0;
		switch (cameraInfo.orientation) {
		case 0:
			rotation = VideoRotation.ROTATION_0;
			break;
		case 90:
			rotation = VideoRotation.ROTATION_CLOCKWISE_90;
			break;
		case 270:
			rotation = VideoRotation.ROTATION_CLOCKWISE_270;
			break;
		default:
			break;
		}
		setRotation(rotation);
	}

	private native int nativeOpenCamera(int cameraIndex);

	/**
	 * 关闭已打开的摄像头
	 */
	public void closeCamera() {
		if (isCameraOpen) {
			nativeCloseCamera();
			isCameraOpen = false;
		}

	}

	private native int nativeCloseCamera();

	/**
	 * 在指定的SurfaceView上对打开的摄像头进行预览，需要调用openCamera打开摄像头
	 * 
	 * @param view
	 *            指定预览的SurfaceView
	 */
	public void startPreview(SurfaceView view) {
		if (isPreviewOn) {
			Log.e(tag, "already start preview");
			return;
		}
		if (!isCameraOpen) {
			Log.e(tag, "start preview but no start camera");
			return;
		}
		if (nativeSetPreview(view) < 0) {
			Log.e(tag, "set preview failed");
			return;
		}
		if (nativeStartPriview() < 0) {
			Log.e(tag, "start preview failed");
			return;
		}
		isPreviewOn = true;

	}

	private native int nativeSetPreview(Object view);

	private native int nativeStartPriview();

	/**
	 * 停止当前摄像头预览
	 */
	public void stopPreview() {
		if (isPreviewOn) {
			if (nativeStopPreview() < 0) {
				Log.e(tag, "stop preview failed");
			} else {
				isPreviewOn = false;
			}

		}
	}

	private native int nativeStopPreview();

	/**
	 * 设置摄像头的翻转角度
	 * 
	 * @param rotation
	 *            摄像头需要翻转的角度
	 */
	public void setRotation(VideoRotation rotation) {
		if (nativeSetRotation(rotation.getValue()) < 0) {
			Log.e(tag, "set rotation failed");
		} else {
			this.rotation = rotation;
		}

	}

	private native int nativeSetRotation(int rotation);

	/**
	 * 获取当前设置的翻转角度
	 * 
	 * @return 翻转角度
	 */
	public VideoRotation getRotation() {
		return this.rotation;
	}

	/**
	 * 设置视频发送编码类型
	 * 
	 * @param codec
	 *            需要设置的发送编码类型
	 */
	public void setSendCodec(VideoCodecType codec) {
		if (nativeSetSendCodec(codec) < 0) {
			Log.e(tag, "set send codec failed");
		} else {
			this.videocodec = codec;
		}
	}

	private native int nativeSetSendCodec(VideoCodecType codec);

	/**
	 * 获取当前视频发送编码类型
	 * 
	 * @return 当前编码类型
	 */
	public VideoCodecType getSendCodec() {
		return this.videocodec;
	}

	/**
	 * 设置本地发出的视频质量
	 * @param quality 设置的参数
	 */
	public void setVideoQuality(VideoQuality quality){
		nativeSetVideoQuality(quality);
	}
	
	private native int nativeSetVideoQuality(VideoQuality quality);
	
	/**
	 * 获取本地发出的视频质量
	 * @return 当前视频参数
	 */
	public VideoQuality getVideoQuality(){
		return nativeGetVideoQuality();
	}
	private native VideoQuality nativeGetVideoQuality();
	
	/**
	 * 设置视频传输配置，在调用start方法前执行
	 * 
	 * @param config
	 *            传输配置信息
	 */
	public boolean configTransport(Transport config) {
		if(isStart)
		{
			Log.e(Tag, "Config transport but video session is started");
			return false;
		}
		if (config == null)
			return false;
		if (config.transportType == TransportType.TRANSPORT_INTERNAL) {
			if (config.localIp == null) {
				return false;
			}
			if (config.remoteIp == null) {
				return false;
			}
			this.transportInfo = config;
		} else if (config.transportType == TransportType.TRANSPORT_EXTERNAL) {
			this.transportInfo = config;
		}else if(config.transportType == TransportType.TRANSPORT_ICE)
		{

			if(config.stunLocal == null)
				return false;
			if(config.stunServer == null)
				return false;
			this.transportInfo = config;
		}else {
			Log.i(Tag, "transport type is error");
		}
		return true;
	}

	private native int nativeConfigTransport(Transport config);
	/**
	 * 开视音频，本地视频开始发送到远端，同时开始接受远端视频
	 */
	public boolean start() {
		if (this.isStart == true) {
			return false;
		}
		if (this.transportInfo == null) {
			return false;
		}
		int ret = nativeConfigTransport(transportInfo);
		if (ret < 0) {
			return false;
		}
		ret = nativeStartVideoSession();
		if (ret < 0) {
			return false;
		}
		this.isStart = true;
		Log.i(Tag, "video start success");
		return true;
	}

	private native int nativeStartVideoSession();

	/**
	 * 关闭视频，本地视频停止发送到远端，同时不再接受远端视频
	 */
	public void stop() {
		if (!isStart) {
			return;
		}
		nativeStopVideoSession();
		isStart = false;
	}

	private native int nativeStopVideoSession();

	/**
	 * 销毁视频会话，包括所有的远端视频通道， 释放所有视频会话资源
	 */
	public void terminate() {
		if (!isTerminated) {
			if (isStart) {
				stop();
			}
			ArrayList <RemoteVideoChannel> tmpList = new ArrayList<RemoteVideoChannel>(remoteVideoChannelList);
			for (RemoteVideoChannel remoteVideoChannel : tmpList) {
				remoteVideoChannel.terminate();
			}
			remoteVideoChannelList.clear();
			nativeDeleteLocalVideoChannel();
		}
		remoteVideoChannelList = null;
		isTerminated = true;
	}

	/**
	 * 创建远端视频，用来接收远端视频
	 * 
	 * @return 远端视频通道
	 */
	public RemoteVideoChannel createRemoteChannel(boolean useSrtp, boolean useSrtcp, SrtpCipherType videoCipher, String videoKey) {
		return createRemoteChannel(0,useSrtp,useSrtcp,videoCipher,videoKey);
	}

	/**
	 * 创建远端视频，其中远端视频采用指定的ssrc
	 * 
	 * @param ssrc
	 *            在远端视频中要使用的ssrc
	 * 		  videoKey
	 *            解密秘钥，在不使用SRTP时应传入"",不能传入null
	 *        videoCipher
	 *        	  解密方式
	 * @return 远端视频通道
	 */
	public RemoteVideoChannel createRemoteChannel(int ssrc, boolean useSrtp, boolean useSrtcp, SrtpCipherType videoCipher, String videoKey) {
		if(videoKey == null)
		{
			videoKey = "";
		}
		int videoChannelId = nativeCreateRemoteChannel(ssrc,useSrtp,useSrtcp,videoCipher.toString(),videoKey);
		if (videoChannelId < 0) {
			Log.e(tag, "create remote channel failed");
			return null;
		}
		RemoteVideoChannel tmpVideoChannel = new RemoteVideoChannel(
				videoChannelId,ssrc, this);

		remoteVideoChannelList.add(tmpVideoChannel);

		return tmpVideoChannel;
	}
    void deleteRemoteChannel(RemoteVideoChannel remoteChannel)
    {
    	for (RemoteVideoChannel remoteVideoChannel : remoteVideoChannelList) {
    		if(remoteVideoChannel.getChannelId() == remoteChannel.getChannelId())
    		{
    			remoteVideoChannelList.remove(remoteVideoChannel);
    			break;
    		}
			
		}
    }
	/**
	 * 通过ssrc查找远端视频通道
	 * @param ssrc
	 * @return 视频通道
	 */
	public RemoteVideoChannel findRemoteChannelBySsrc(int ssrc)
	{
		for (RemoteVideoChannel remoteVideoChannel : remoteVideoChannelList) {
    		if(remoteVideoChannel.getSsrc() == ssrc)
    		{
    			return remoteVideoChannel;
    		}
			
		}
		return null;
	}
	/**
	 * 当使用外部传输时，收到远端视频RTP后，使用此方法向引擎投递数据
	 * 
	 * @param data
	 *            外部传输收到的视频RTP数据
	 */
	public void rtpDataIncoming(byte[] data) {
		nativeRtpDataIncoming(data);
	}

	private native void nativeRtpDataIncoming(byte[] data);

	/**
	 * 当使用外部传输时，收到远端视频RTCP后，使用此方法向引擎投递数据
	 * 
	 * @param data
	 *            外部传输收到的视频RTCP数据
	 */
	public void rtcpDataIncoming(byte[] data) {
		nativeRtcpDataIncoming(data);
	}

	private native void nativeRtcpDataIncoming(byte[] data);

	private native int nativeCreateRemoteChannel(int ssrc, boolean useSrtp, boolean useSrtcp, String videoCipher, String videoKey);

	private native int nativeDeleteLocalVideoChannel();

	
	/**
	 * 传入远端的候选地址列表
	 * @param iceCandidates
	 * 			候选传输地址列表
	 */
	public void receiveRemoteCandidates(IceCandidate[] iceCandidates)
	{
		nativeReceiveRemoteCandidates(iceCandidates);
	}
	private native void nativeReceiveRemoteCandidates(IceCandidate[] iceCandidates);
	
	public native void setRemoteAuthenticationInfo(String userName,String password);

	
	void outgoingData(byte[] data, boolean isRtcp) {
		if (isTerminated) {
			return;
		}
		if (transportInfo == null
				|| transportInfo.transportType != TransportType.TRANSPORT_EXTERNAL) {
			return;
		}

		if (transportInfo.getOnOutgoingListener() == null) {
			return;
		}
		if (!isRtcp) {
			transportInfo.getOnOutgoingListener().onRtpOutgoing(data);
		} else {
			transportInfo.getOnOutgoingListener().onRtcpOutgoing(data);
		}
	}

	 void dispatchEvent(int channelId, int event) {
			if (remoteVideoChannelList.size()==0) {
				return;
			}
			for (RemoteVideoChannel channel : remoteVideoChannelList) {
				if (channel.getChannelId()==channelId) {
					channel.dispatchEvent(event);
					break;
				}
			}
	}
	 
	void onCandidatesCreated(IceCandidate[] candidateBuf)
	{
		if (transportInfo == null
				|| transportInfo.transportType != TransportType.TRANSPORT_ICE) {
			return;
		}
		
		transportInfo.onCandidatesCreatedListener.OnCandidatesCreated(transportInfo.iceId,candidateBuf);
	}
	 
}