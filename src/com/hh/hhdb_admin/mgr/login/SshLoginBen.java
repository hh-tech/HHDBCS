package com.hh.hhdb_admin.mgr.login;

import java.io.Serializable;

/**
 * @author ouyangxu
 * @date 2021-06-11 0011 15:59:14
 */
public class SshLoginBen implements Serializable {
	private static final long serialVersionUID = -5503115161853464652L;
	private String host;
	private int port = 22;
	private String user;
	private String password;
	private boolean enabled = true;
	private SshType type;
	private String privateKey;
	private String keyPassphrase;

	public SshLoginBen() {
	}

	public enum SshType {
		/**
		 * 密码,公钥
		 */
		PASSWORD, PUBLIC_KEY
	}

	public SshLoginBen(String host, int port, String user, String password) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		this.type = SshType.PASSWORD;
	}

	public SshLoginBen(String host, int port, String user, String privateKey, String keyPassphrase) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.privateKey = privateKey;
		this.keyPassphrase = keyPassphrase;
		this.type = SshType.PUBLIC_KEY;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public SshType getType() {
		return type;
	}

	public void setType(SshType type) {
		this.type = type;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getKeyPassphrase() {
		return keyPassphrase;
	}

	public void setKeyPassphrase(String keyPassphrase) {
		this.keyPassphrase = keyPassphrase;
	}

	@Override
	public String toString() {
		return "SshLoginBen{" +
				"host='" + host + '\'' +
				", port=" + port +
				", user='" + user + '\'' +
				", password='" + password + '\'' +
				", enabled=" + enabled +
				", type=" + type +
				", privateKey='" + privateKey + '\'' +
				", keyPassphrase='" + keyPassphrase + '\'' +
				'}';
	}
}
