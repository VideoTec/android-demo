package com.ultrapower.umcs;

/**
 * @author Administrator
 * 端点
 */
public class IpEndpoint {
    public String getIP() {
		return IP;
	}

	public int getPort() {
		return Port;
	}
    /**
     * IP地址
     */
    String IP;

	/**
     * 端口
     */
    int Port;
    
    public IpEndpoint(String ip,int port) {
		this.IP = ip;
		this.Port = port;
	}
    
}
