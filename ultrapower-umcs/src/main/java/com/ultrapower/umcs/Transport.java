package com.ultrapower.umcs;

/**
 * 音频会话或视频会话传输信息描述类.<br>
 * 
 * 当使用音频会话或视频会话时，需要用此类型的对象对会话进行传输配置。 <h3>外部传输</h3> <li>
 * <p>
 * 外部传输表示会话内的数据流使用引擎外部的传输通道进行传输, 当引擎需要发送数据时会回调接口 #OnDataOutgoingListener 响应的方法
 * 当外部传输收到对方会话的数据时 使用 {@link #createExternalTransport(OnDataOutgoingListener)}
 * 方法产生配置信息
 * </p>
 * </li> <h3>内部传输</h3> <li>内部传输使用
 * {@link #createExternalTransport(OnDataOutgoingListener)} 方法产生配置信息</li>
 */
public class Transport {
	TransportType getTransportType() {
		return transportType;
	}

	public String getLocalIp() {
		return localIp;
	}

	public int getLocalRtpPort() {
		return localRtpPort;
	}

	public int getLocalRtcpPort() {
		return localRtcpPort;
	}

	public String getRemoteIp() {
		return remoteIp;
	}

	public int getRemoteRtpPort() {
		return remoteRtpPort;
	}

	public int getRemoteRtcpPort() {
		return remoteRtcpPort;
	}

	OnDataOutgoingListener getOnOutgoingListener() {
		return onOutgoingListener;
	}

	TransportType transportType;


	String localIp;
	int localRtpPort;
	int localRtcpPort;

	String remoteIp;
	int remoteRtpPort;
	int remoteRtcpPort;

	OnDataOutgoingListener onOutgoingListener;
	
	OnCandidatesCreatedListener onCandidatesCreatedListener;

	
/*	struct UMCS_IceConfig{
	    
	    bool useAuthentication;
	    char username[UMCS_USERNAME_MAX_LEN];
	    int usernameLength;
	    char password[UMCS_PASSWORD_MAX_LEN];
	    int passwordLength;
	    
	    enum UMCS_NetworkType netType;
	    
	    bool hasDefaultTransport;
	    struct UMCS_IpEndpoint defaultLocal;
	    struct UMCS_IpEndpoint defaultRelay;
	    
	    struct UMCS_IpEndpoint stunLocal;
	    struct UMCS_IpEndpoint stunServer;
	    
	    bool useEnhanceMode;
	    struct UMCS_IpEndpoint enhanceStunServer;
	    
	    void(*OnCandidatesCreated)(struct UMCS_IceCandidateArray* candidates);
	};*/
	int iceId;
	
	public OnCandidatesCreatedListener getOnCandidatesCreatedListener() {
		return onCandidatesCreatedListener;
	}

	public int getIceId() {
		return iceId;
	}

	public boolean isUseAuthentication() {
		return useAuthentication;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public NetworkType getNetworkType() {
		return networkType;
	}

	public boolean isHasDefaultTransport() {
		return hasDefaultTransport;
	}

	public IpEndpoint getDefaultLocal() {
		return defaultLocal;
	}

	public IpEndpoint getDefaultRelay() {
		return defaultRelay;
	}

	public IpEndpoint getStunLocal() {
		return stunLocal;
	}

	public IpEndpoint getStunServer() {
		return stunServer;
	}

	public boolean isUseEnhanceMode() {
		return useEnhanceMode;
	}

	public IpEndpoint getEnhanceStunServer() {
		return enhanceStunServer;
	}

	boolean useAuthentication;
	String  username;
	String  password;
	
	NetworkType networkType;


	boolean hasDefaultTransport;
	IpEndpoint defaultLocal;
	IpEndpoint defaultRelay;
	IpEndpoint stunLocal;
	IpEndpoint stunServer;
	
	boolean useEnhanceMode;
	IpEndpoint enhanceStunServer;
	
	private Transport() {

	}

	/**
	 * 创建一个使用外部传输的配置信息
	 * 
	 * @param onAudioOutgoingListener
	 *            外部传输的数据监听器，当引擎产生数据后会回调此监听器
	 * 
	 * @return 外部传输
	 */
	static public Transport createExternalTransport(
			OnDataOutgoingListener onOutgoingListener) {
		Transport config = new Transport();
		config.onOutgoingListener = onOutgoingListener;
		config.transportType = TransportType.TRANSPORT_EXTERNAL;
		return config;
	}

	/**
	 * 创建一个使用内部传输的配置信息
	 * 
	 * @param localIp
	 *            本地IP
	 * @param localRtpPort
	 *            本地RTP端口
	 * @param localRtcpPort
	 *            本地RTCP端口
	 * @param remoteIp
	 *            远端IP
	 * @param remoteRtpPort
	 *            远端RTP端口
	 * @param remoteRtcpPort
	 *            远端RTCP端口
	 * 
	 * @return 内部传输
	 */
	static public Transport createInternalTransport(String localIp,
			int localRtpPort, int localRtcpPort, String remoteIp,
			int remoteRtpPort, int remoteRtcpPort) {
		Transport config = new Transport();
		config.localIp = localIp;
		config.localRtpPort = localRtpPort;
		config.localRtcpPort = localRtcpPort;
		config.remoteIp = remoteIp;
		config.remoteRtpPort = remoteRtpPort;
		config.remoteRtcpPort = remoteRtcpPort;
		config.transportType = TransportType.TRANSPORT_INTERNAL;
		return config;
	}
	
	/**
	 * 创建一个ICE方式传输的配置信息
	 * @param hasDefaultTransport
	 * 				是否有默认的传输地址
	 * @param defaultLocal
	 * 				默认的本地传输地址
	 * @param defaultRelay
	 * 				默认的Relay地址
	 * @param stunLocal
	 * 				stun本地地址
	 * @param stunServer
	 * 				stun服务器地址
	 * @param listener
	 * 				收集到候选地址列表的监听器
	 * @return
	 * 				ICE方式传输
	 */			
	static public Transport createIceTransport(int iceId,boolean useAuthentication,
													String  username,
													String  password,
													NetworkType networkType,
													boolean hasDefaultTransport,
													IpEndpoint defaultLocal,
													IpEndpoint defaultRelay,
													IpEndpoint stunLocal,
													IpEndpoint stunServer,
													boolean useEnhanceMode,
													IpEndpoint enhanceStunServer,
													OnCandidatesCreatedListener listener)
	{
		Transport config = new Transport();
		config.transportType = TransportType.TRANSPORT_ICE;
		
		config.iceId = iceId;
		
		config.useAuthentication = useAuthentication;
		config.username = username;
		config.password = password;
		
		config.hasDefaultTransport = hasDefaultTransport;
		config.networkType = networkType;
		config.defaultLocal = defaultLocal;
		config.defaultRelay = defaultRelay;
		config.stunLocal = stunLocal;
		config.stunServer = stunServer;
		
		config.useEnhanceMode = useEnhanceMode;
		config.enhanceStunServer = enhanceStunServer;
		
		config.onCandidatesCreatedListener = listener;
		
		return config;
	}
}
