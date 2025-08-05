package com.hh.hhdb_admin.mgr.tree;

import java.util.ArrayList;
import java.util.List;

import com.hh.hhdb_admin.common.bean.BaseRefreshBean;

/*
* Author:Zhang Dagang
* Date:2024年5月7日
*/
public class RedisRmPreLoadKeysBean extends BaseRefreshBean {
	public RedisRmPreLoadKeysBean(String refreshNodeId) {
		super(refreshNodeId);
	}
	private List<String> keys=new ArrayList<>();
	public void addKey(String key) {
		keys.add(key);
	}
	public List<String> getKeys(){
		return keys;
	}

}
