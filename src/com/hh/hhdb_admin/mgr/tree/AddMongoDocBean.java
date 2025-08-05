package com.hh.hhdb_admin.mgr.tree;

import com.hh.frame.json.JsonObject;
import com.hh.hhdb_admin.common.bean.BaseRefreshBean;

public class AddMongoDocBean extends BaseRefreshBean {
	private final static String DOC_ID_OBJ="doc_id_obj";
	public AddMongoDocBean(JsonObject jobj) {
		super(jobj);
	}
	public AddMongoDocBean(String refreshNodeId,JsonObject docIdObj) {
		super(refreshNodeId);
		setDocIdObj(docIdObj);
	}
	
	public void setDocIdObj(JsonObject docIdObj) {
		jobj.set(DOC_ID_OBJ, docIdObj);
	}
	
	public JsonObject getDocIdObj() {
		return jobj.get(DOC_ID_OBJ).asObject();
	}
}
