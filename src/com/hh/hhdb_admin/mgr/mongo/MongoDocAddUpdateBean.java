package com.hh.hhdb_admin.mgr.mongo;

import com.hh.frame.json.JsonObject;
import com.hh.hhdb_admin.common.bean.BaseRefreshBean;
public class MongoDocAddUpdateBean extends BaseRefreshBean{
	public MongoDocAddUpdateBean(JsonObject obj) {
		super(obj);
	}
	public MongoDocAddUpdateBean(String refreshNodeId) {
		super(refreshNodeId);
	}
	private final static String DOCID="docId";
	private final static String DBNAME="dbName";
	private final static String COLLNAME="collName";
	public String getDocId() {
		return jobj.getString(DOCID);
	}
	public void setDocId(String docId) {
		jobj.set(DOCID, docId);
	}
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
