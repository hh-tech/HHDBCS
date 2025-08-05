package com.hh.hhdb_admin.mgr.tree;

import com.hh.hhdb_admin.common.bean.BaseRefreshBean;

public class RefreshNodeBean extends BaseRefreshBean {
	private final static String ONLY_GUI="only_gui";
	public RefreshNodeBean(String refreshNodeId) {
		this(refreshNodeId,false);
	}
	
	public RefreshNodeBean(String refreshNodeId,boolean onlyGui) {
		super(refreshNodeId);
		setOnlyGui(onlyGui);
	}
	
	public boolean isOnlyGui() {
		return jobj.getBoolean(ONLY_GUI);
	}
	public void setOnlyGui(boolean onlyGui) {
		jobj.set(ONLY_GUI, onlyGui);
	}
	
}
