package com.hh.hhdb_admin.conf;

import com.hh.frame.json.JsonObject;

public class CmdPanelConf extends SqlPanelConf {
	private static final String ERROR_BREAK="error_break";
	
	public CmdPanelConf() {
		super();
		this.setRowPerPage(10);
		setErrorBreak(true);
	}
	
	public CmdPanelConf(JsonObject jobj) {
		super(jobj);
	}
	
	public void setErrorBreak(boolean errorBreak) {
		jobj.set(ERROR_BREAK, errorBreak);
	}
	
	public boolean isErrorBreak() {
		return jobj.getBoolean(ERROR_BREAK);
	}
}
