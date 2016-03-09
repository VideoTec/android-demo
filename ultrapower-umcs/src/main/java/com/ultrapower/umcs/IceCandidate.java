package com.ultrapower.umcs;

/**
 * @author Administrator
 * ICE 候选地址
 */
public class IceCandidate {
    /**
     * ICE 的类型
     */
    public int iceType;
    /**
     * IP地址
     */
    public String ip;
    /**
     * 端口
     */
    public int port;
    /**
     * 基本的IP地址
     */
    public String baseIp;
    /**
     * 基本地址的端口
     */
    public int basePort;
    
    public IceCandidate(int type, String ip, int port, String baseIp, int basePort) {
		// TODO Auto-generated constructor stub
    	this.iceType = type;
    	this.ip = ip;
    	this.port = port;
    	this.baseIp = baseIp;
    	this.basePort = basePort;
	}
    
    public IceCandidate()
    {
    	
    }

}
