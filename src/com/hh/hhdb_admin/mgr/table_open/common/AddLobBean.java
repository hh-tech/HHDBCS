package com.hh.hhdb_admin.mgr.table_open.common;

import java.io.Serializable;

/**
 * @author ouyangxu
 * @date 2021-01-29 0029 16:25:42
 */
public class AddLobBean implements Serializable {

	private static final long serialVersionUID = -7796238765289954464L;
	private String name;
	private boolean isBlob;
	private String type;
	private byte[] data;

	public AddLobBean(String name, boolean isBlob, byte[] data) {
		this.name = name;
		this.isBlob = isBlob;
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isBlob() {
		return isBlob;
	}

	public void setBlob(boolean blob) {
		isBlob = blob;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
