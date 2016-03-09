package com.ultrapower.umcs;
import com.ultrapower.umcs.NetworkState;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 音视频引擎类.<br>
 * 1. 初始化引擎 {@link #initialize(EngineConfig)}<br>
 * 2. 音频会话创建音频通道 {@link #createAudioSession()}，视频会话创建视频通道
 * {@link #createVideoSession()}<br>
 * 3. 会话完成销毁引擎 {@link #terminate()}<br>
 */
public class MediaEngine {

	static {
		System.loadLibrary("umcs");
	}

	private static final int MSG_AUDIO_EVENT = 0;
	private static final int MSG_VIDEO_EVENT = 1;
	private static final int MSG_OUTGOING_VIDEO_RTP = 2;
	private static final int MSG_OUTGOING_VIDEO_RTCP = 3;
	private static final int MSG_OUTGOING_AUDIO_RTP = 4;
	private static final int MSG_OUTGOING_AUDIO_RTCP = 5;
	private static final int MSG_AUDIO_CANDIDATES_CREATE = 6;
	private static final int MSG_VIDEO_CANDIDATES_CREATE = 7;
	private static final int MSG_LOCAL_NETWORK_STATE = 8;
	private static final int MSG_REMOTE_NETWORK_STATE = 9;
	
	private static final AudioCodecInfo[] SUPPORTED_AUDIO_CODECS = {
			new AudioCodecInfo(AudioCodecType.AUDIO_CODEC_ISAC, "ISAC", 103,
					16000, 1),
			new AudioCodecInfo(AudioCodecType.AUDIO_CODEC_ILBC, "ILBC", 102,
					8000, 1),
			new AudioCodecInfo(AudioCodecType.AUDIO_CODEC_PCMU, "PCMU", 0,
					8000, 1),
			new AudioCodecInfo(AudioCodecType.AUDIO_CODEC_AMRWB, "AMRWB", 115,
					16000, 1),
			new AudioCodecInfo(AudioCodecType.AUDIO_CODEC_SILK, "SILK", 124,
					16000, 1),
			new AudioCodecInfo(AudioCodecType.AUDIO_CODEC_EVS, "EVS", 122,
					16000, 1),
			new AudioCodecInfo(AudioCodecType.AUDIO_CODEC_OPUS, "OPUS", 121,
					48000, 2),

	};
	private static final VideoCodecInfo[] SUPPORTED_VIDEO_CODECS = {
			new VideoCodecInfo(VideoCodecType.VIDEO_CODEC_VP8, 120, "VP8"),
			new VideoCodecInfo(VideoCodecType.VIDEO_CODEC_H264, 125, "H264") };

	private static final MediaEngine INSTANCE = new MediaEngine();
	private static final String TAG = "MediaEngine";
	private static Handler handler;

	private Context context;
	private boolean isMultiMode;
	private TraceLevel traceLevel;
	private boolean isInited;
	private VideoSession videoSession;
	private AudioSession audioSession;
	private NetworkStateListener networkStateListener;

	private MediaEngine() {
	}

	/**
	 * 获取音视频引擎的唯一实例
	 * 
	 * @return 音视频引擎
	 */
	public static MediaEngine getInstance() {
		return INSTANCE;
	}

	/**
	 * 检查引擎是否已经初始化
	 * 
	 * @return 引擎是否已经初始化
	 */
	public boolean isInitialized() {
		return isInited;
	}

	/**
	 * 获取已创建的音频会话
	 * @return  音频会话
	 */
	public AudioSession getAudioSession() {
		return audioSession;
	}
	/**
	 * 获取已创建的视频会话
	 * @return  视频会话
	 */
	public VideoSession getVideoSession() {
		return videoSession;
	}
	/**
	 * 初始化音视频引擎，在使用音视频引擎前调用
	 * 
	 * @param config
	 *            音视频配置信息
	 * @throws IllegalStateException
	 *             引擎状态错误
	 */
	public void initialize(EngineConfig config) throws IllegalStateException {
		if (isInited) {
			throw new IllegalStateException("engine is inited");
		}
		if (config == null) {
			throw new IllegalArgumentException("config must not be null");
		}
		if (config.getContext() == null) {
			throw new IllegalArgumentException(
					"context in config must not be null");
		}

	
		if (handler == null) {
			handler = new Handler(new Handler.Callback() {
				
				@Override
				public boolean handleMessage(Message msg) {
					switch (msg.what) {
					case MSG_AUDIO_EVENT:
						MediaEngine.INSTANCE.dispatchAudioEvent(msg.arg1,msg.arg2);
						return true;
					case MSG_VIDEO_EVENT:
						MediaEngine.INSTANCE.dispatchVideoEvent(msg.arg1,msg.arg2);
						return true;
					case MSG_OUTGOING_AUDIO_RTP:
						MediaEngine.INSTANCE.outgoingAudioData((byte[])msg.obj, false);
						return true;
					case MSG_OUTGOING_AUDIO_RTCP:
						MediaEngine.INSTANCE.outgoingAudioData((byte[])msg.obj, true);
						return true;
					case MSG_OUTGOING_VIDEO_RTP:
						MediaEngine.INSTANCE.outgoingVideoData((byte[])msg.obj, false);
						return true;
					case MSG_OUTGOING_VIDEO_RTCP:
						MediaEngine.INSTANCE.outgoingVideoData((byte[])msg.obj, true);
						return true;
					case MSG_AUDIO_CANDIDATES_CREATE:
						MediaEngine.INSTANCE.audioCandidatesCreated((IceCandidate [])msg.obj);
						return true;
					case MSG_VIDEO_CANDIDATES_CREATE:
						MediaEngine.INSTANCE.videoCandidatesCreated((IceCandidate [])msg.obj);
						return true;
					case MSG_LOCAL_NETWORK_STATE:
						MediaEngine.INSTANCE.localNetworkChanged((Integer) msg.obj);
						return true;
					case MSG_REMOTE_NETWORK_STATE:
						MediaEngine.INSTANCE.remoteNetworkChanged(msg.arg1,msg.arg2);
						return true;
					default:
						break;
					}
					return false;
				}
			});
		}

		
		context = config.getContext();
		AudioManager mAudioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
		// todo:do some jni operations,if successed set isInited = true;
		if (init(config) == 0) {
			this.isInited = true;
		} else {
			Log.i("MediaEngine", "init failed");
			
		}
		
		networkStateListener = config.getNetworkStateListener();

	}

	private int init(EngineConfig config) {

		return nativeInitialize(config.getContext(),
				config.isMultiMode(),
				config.getTraceLevel().getValue(),
				config.isAudioUseSrtp(),
				config.isAudioUseSrtcp(),
				config.isVideoUseSrtp(),
				config.isVideoUseSrtcp(),
				config.getAudioKey(),
				config.getAudioCipher(),
				config.getVideoKey(),
				config.getVideoCipher());
	}

	private native int nativeInitialize(Context context, boolean isMultiMode,
			int tracelevel, boolean isAudioUseSrtp, boolean isAudioUseSrtcp, boolean isVideoUseSrtp, boolean isVideoUseSrtcp, String audioKey, String audioCipher, String videoKey, String videoCipher);

	/**
	 * 终止音视频，销毁所有音视频相关资源
	 */
	public void terminate() {
		if (isInited) {
			if (videoSession != null && !videoSession.isTerminated()) {
				Log.i(TAG, "video session terminate");
				videoSession.terminate();
				videoSession = null;
			}
			if (audioSession != null && !audioSession.isTerminated()) {
				Log.i(TAG, "audio session terminate");
				audioSession.terminate();
				audioSession = null;
			}
			// todo:do terminate engine;
			nativeTerminate();
			this.isInited = false;

		}
	}

	private native void nativeTerminate();

	/**
	 * 获取音频编解码信息
	 * 
	 * @return 音频编解支持列表
	 */
	public AudioCodecInfo[] getAudioCodecs() {

		return SUPPORTED_AUDIO_CODECS.clone();
	}

	/**
	 * 获取视频编码信息
	 * 
	 * @return 视频编码支持列表
	 */
	public VideoCodecInfo[] getVideoCodecs() {
		return SUPPORTED_VIDEO_CODECS.clone();
	}

	/**
	 * 创建一个音频会话进行音频通信,默认采用随机ssrc
	 * 
	 * @return 音频会话
	 */
	public AudioSession createAudioSession() {
		return createAudioSession(0);
	}

	/**
	 * 创建一个音频会话进行音频通信，其中ssrc采用指定的值
	 * 
	 * @param ssrc
	 *            指定的ssrc值，若ssrc为0，将采用随机ssrc值
	 * @return 音频会话
	 */
	public AudioSession createAudioSession(int ssrc) {
		if(!isInited)
		{
			Log.e(TAG, "media engine is not initialize");
			return null;
		}
		if (audioSession != null && !audioSession.isTerminated()) {
			Log.e(TAG, "audio session is already created");
			return null;
		}
		int sid = nativeCreateAuidoSession(ssrc);
		if (sid < 0) {
			return null;
		}
		audioSession = new AudioSession(sid);

		return audioSession;
	}

	private native int nativeCreateAuidoSession(int ssrc);

	/**
	 * 创建一个视频会话进行视频通信
	 * 
	 * @return 视频会话
	 */
	public VideoSession createVideoSession() {
		return createVideoSession(0);
	}

	/**
	 * 创建一个视频会话进行视频通信，其中ssrc采用指定的值
	 * 
	 * @param ssrc
	 *            指定的ssrc值，若ssrc为0，将采用随机ssrc值
	 * @return 视频会话
	 */
	public VideoSession createVideoSession(int ssrc) {
		if(!isInited)
		{
			Log.e(TAG, "media engine is not initialize");
			return null;
		}
		if (videoSession != null && !videoSession.isTerminated()) {
			Log.e(TAG, "video session is already created");
			return null;
		}
		int sid = nativeCreatVideoSession(ssrc);
		if (sid < 0) {
			return null;
		}
		videoSession = new VideoSession(sid);

		return videoSession;
	}

	private native int nativeCreatVideoSession(int ssrc);

	private void dispatchVideoEvent(int sid, int event) {
		Log.i(TAG, "dispatchVideoEvent in java sid :" + sid + ":, event : "
				+ event);
		if (videoSession != null && !videoSession.isTerminated()) {
			videoSession.dispatchEvent(sid,event);
		}

	}

	private void dispatchAudioEvent(int sid, int event) {
		Log.i(TAG, "dispatchAudioEvent in java sid :" + sid + ":, event : "
				+ event);
		if (audioSession != null && !audioSession.isTerminated()) {
			audioSession.dispatchEvent(sid,event);
		}
	}



	private void outgoingAudioData(byte[] data, boolean isRtcp) {
//		Log.i(TAG, String.format(
//				"external transport outgoing audio data,length=%d isrtcp=%B",
//				data.length, isRtcp));
		if (audioSession != null && !audioSession.isTerminated()) {
			audioSession.outgoingData(data, isRtcp);
		}
	}

	private void outgoingVideoData(byte[] data, boolean isRtcp) {
//		Log.i(TAG, String.format(
//				"external transport outgoing video data,length=%d isrtcp=%B",
//				data.length, isRtcp));

		if (videoSession != null && !videoSession.isTerminated()) {
			videoSession.outgoingData(data, isRtcp);
		}
	}
	
	private void audioCandidatesCreated(IceCandidate[] candidateBuf)
	{
		if (audioSession != null && !audioSession.isTerminated()) {
			audioSession.onCandidatesCreated(candidateBuf);
		}
	}
	
	private void videoCandidatesCreated(IceCandidate[] candidateBuf)
	{
		if (videoSession != null && !videoSession.isTerminated()) {
			videoSession.onCandidatesCreated(candidateBuf);
		}
	}
	
	static void staticDispatchVideoEvent(int sid, int event) {
		if (handler!=null) {
			handler.obtainMessage(MSG_VIDEO_EVENT, sid, event).sendToTarget();
		}
	}

	static void staticDispatchAudioEvent(int sid, int event) {
		if (handler!=null) {
			handler.obtainMessage(MSG_AUDIO_EVENT, sid, event).sendToTarget();
		}
	}
	static private void staticOutgoingAudioData(byte[] data, boolean isRtcp) {
		int msgType = isRtcp?MSG_OUTGOING_AUDIO_RTCP:MSG_OUTGOING_AUDIO_RTP;
		if (handler!=null) {
			handler.obtainMessage(msgType,data).sendToTarget();
		}
	}

	static private void staticOutgoingVideoData(byte[] data, boolean isRtcp) {
		int msgType = isRtcp?MSG_OUTGOING_VIDEO_RTCP:MSG_OUTGOING_VIDEO_RTP;
		if (handler!=null) {
			handler.obtainMessage(msgType,data).sendToTarget();
		}
	}
	
	static private void staticAudioCandidatesCreated(IceCandidate[] candidateBuf)
	{
		Log.i(TAG, "candidateBuf length is:" +candidateBuf.length);
		if (handler!=null) {
			handler.obtainMessage(MSG_AUDIO_CANDIDATES_CREATE,candidateBuf).sendToTarget();
		}
	}
	
	static private void staticVideoCandidatesCreated(IceCandidate[] candidateBuf)
	{
		if (handler!=null) {
			handler.obtainMessage(MSG_VIDEO_CANDIDATES_CREATE,candidateBuf).sendToTarget();
		}
	}
	
	static private void staticLocalNetworkChanged(int state)
	{
		if (handler!=null) {
			handler.obtainMessage(MSG_LOCAL_NETWORK_STATE, state).sendToTarget();
		}
	}
	
	static private void staticRemoteNetworkChanged(int channelId, int state)
	{
		if (handler!=null) {
			handler.obtainMessage(MSG_REMOTE_NETWORK_STATE, channelId, state).sendToTarget();
		}
	}
	
	private NetworkState intToNetworkState(int state) {

		switch (state) {
		case 1:
			return NetworkState.NETWORK_STATE_BAD;
		case 2:
			return NetworkState.NETWORK_STATE_LOW;
		case 3:
			return NetworkState.NETWORK_STATE_NORMAL;
		case 4:
			return NetworkState.NETWORK_STATE_GOOD;
		default:
			return NetworkState.NETWORK_STATE_UNKNOWN;
		}
	}
	private void localNetworkChanged(int state) {
		Log.i(TAG, "localNetworkChanged state=" + state);
		if(networkStateListener != null)
		{
			networkStateListener.onLocalNetworkStateChanged(intToNetworkState(state));
		}
	}
	
	private void remoteNetworkChanged(int channelId, int state) {
		Log.i(TAG, "remoteNetworkChanged channelId=" + channelId + " state=" + state);
		if(networkStateListener != null)
		{
			networkStateListener.onRemoteNetworkStateChanged(channelId, intToNetworkState(state));
		}
	}
}
