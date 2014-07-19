package com.zxsoft.crawler.cache.proxy;

import java.io.Serializable;


public class Proxy implements Serializable {

    private static final long serialVersionUID = 518169468536248943L;
	private String username;
	private String password;
	private String host;
	private int port;
	private String type;
	private String realm;
	
	
	public Proxy(String type, String username, String password, String host, int port, String realm) {
	    super();
	    this.username = username;
	    this.password = password;
	    this.host = host;
	    this.port = port;
	    this.type = type;
    }
	
	
	public String getRealm() {
		return realm;
	}


	public void setRealm(String realm) {
		this.realm = realm;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String toString() {
		return username + ":" + password + ":" + host + ":" + port;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	
}