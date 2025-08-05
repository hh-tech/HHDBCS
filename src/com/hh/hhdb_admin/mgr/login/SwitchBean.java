package com.hh.hhdb_admin.mgr.login;

import com.hh.frame.json.BaseJsonBean;

public class SwitchBean extends BaseJsonBean {
	private final String SWITCH_NAME="switch_name";
	
	public SwitchBean(String switchName) {
		setSwitchName(switchName);
	}

	public String getSwitchName() {
		return jobj.getString(SWITCH_NAME);
	}

	public void setSwitchName(String switchName) {
		jobj.set(SWITCH_NAME, switchName);
	}

}
