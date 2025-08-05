package com.hh.hhdb_admin.common.bean;

import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;

public class BaseRefreshBean extends BaseJsonBean {
	private final static String REFRESH_NODE_ID="refresh_node_id";
	public BaseRefreshBean(JsonObject jobj) {
		this.jobj=jobj;
	}
	public BaseRefreshBean(String refreshNodeId) {
		setRefreshNodeId(refreshNodeId);
	}
	public String getRefreshNodeId() {
		return jobj.getString(REFRESH_NODE_ID);
	}
	public void setRefreshNodeId(String refreshNodeId) {
		jobj.set(REFRESH_NODE_ID, refreshNodeId);
	}

}
