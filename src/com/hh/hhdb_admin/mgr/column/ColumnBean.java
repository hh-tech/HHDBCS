package com.hh.hhdb_admin.mgr.column;

import com.hh.frame.json.JsonObject;
import com.hh.hhdb_admin.common.bean.BaseRefreshBean;

public class ColumnBean extends BaseRefreshBean {
    private static final String TABLENAME = "tableName";
    private static final String COLUMNNAME = "columnName";
    private static final String COL_OPR_TYPE="col_opr_type";
    private static final String SCHEMA = "schema";
    
    public enum ColOprType{
    	ADD,MODIFY,UPDATE,RENAME,COPY
    }
    
    public ColumnBean(ColOprType optype,String freshNodeId) {
    	super(freshNodeId);
    	setColOprType(optype);
    }
   
    public ColumnBean(JsonObject obj) {
        super(obj);
    }
    
    public String getTableName() {
        return jobj.getString(TABLENAME);
    }
    
    public void setTableName(String tableName) {
        jobj.set(TABLENAME, tableName);
    }
    
    public String getColumnName() {
        return jobj.getString(COLUMNNAME);
    }
    
    public void setColumnName(String columnName) {
        jobj.set(COLUMNNAME, columnName);
    }



	public ColOprType getColOprType() {
		return ColOprType.valueOf(jobj.getString(COL_OPR_TYPE));
	}

	public void setColOprType(ColOprType colOprType) {
		jobj.set(COL_OPR_TYPE, colOprType.name());
	}

	public String getSchema() {
		return this.jobj.getString(SCHEMA);
	}

	public void setSchema(String schema) {
		this.jobj.set(SCHEMA, schema);
	}
	
	
	
	
    
}
