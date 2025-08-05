package com.hh.hhdb_admin.mgr.tree;

import com.hh.frame.json.JsonObject;
import com.hh.hhdb_admin.common.bean.BaseRefreshBean;

/*
* Author:Zhang Dagang
* Date:2024年4月27日
*/
public class RedisRefreshBean extends BaseRefreshBean {
	private final String REDIS_OBJ_KEY="redis_obj_key";

	public RedisRefreshBean(JsonObject jobj) {
		super(jobj);
	}
	
	public RedisRefreshBean(String refreshNodeId,String redisObjKey) {
		super(refreshNodeId);
		setKeyName(redisObjKey);
	}
	
	public String getKeyName() {
		return jobj.getString(REDIS_OBJ_KEY);
	}
	public void setKeyName(String redisObjKey) {
		jobj.set(REDIS_OBJ_KEY, redisObjKey);
	}

}
