package com.hh.hhdb_admin.mgr.mongo;

import com.hh.frame.json.JsonObject;
import com.hh.hhdb_admin.common.bean.BaseRefreshBean;

public class MongoDocOpenBean extends BaseRefreshBean{
	public MongoDocOpenBean(JsonObject obj) {
		super(obj);
	}
	public MongoDocOpenBean(String refreshNodeId) {
		super(refreshNodeId);
	}
	private final static String DBNAME="dbName";
	private final static String COLLNAME="collName";
	public String getDbName() {
		return jobj.getString(DBNAME);
	}
	public void setDbName(String dbName) {
		jobj.set(DBNAME, dbName);
	}
	public String getCollName() {
		return jobj.getString(COLLNAME);
	}
	public void setCollName(String collName) {
		jobj.set(COLLNAME, collName);
	}
}
