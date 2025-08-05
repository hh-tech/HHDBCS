package com.hh.hhdb_admin.mgr.function2.debug.base;

import java.util.List;

import com.hh.frame.db_sql.info.SqlParamBean;

public class DebugBean {
	private String debugSql;
	private String debugObjId;
	private String debugObjSchema;
	private List<SqlParamBean> params;
	public String getDebugSql() {
		return debugSql;
	}
	public void setDebugSql(String debugSql) {
		this.debugSql = debugSql;
	}

	public String getDebugObjId() {
		return debugObjId;
	}
	public void setDebugObjId(String debugObjId) {
		this.debugObjId = debugObjId;
	}
	public String getDebugObjSchema() {
		return debugObjSchema;
	}
	public void setDebugObjSchema(String debugObjSchema) {
		this.debugObjSchema = debugObjSchema;
	}
	public List<SqlParamBean> getParams() {
		return params;
	}
	public void setParams(List<SqlParamBean> params) {
		this.params = params;
	}
	
	
}
