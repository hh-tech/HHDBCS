package com.hhdb.csadmin.common.bean;

public class VersionBean {
	private String name = "";
	
	private String version = "";
	
	private String versionid = "";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersionid() {
		return versionid;
	}

	public void setVersionid(String versionid) {
		this.versionid = versionid;
	}

	@Override
	public String toString() {
		return "VersionBean [name=" + name + ", version=" + version
				+ ", versionid=" + versionid + "]";
	}
	
}
