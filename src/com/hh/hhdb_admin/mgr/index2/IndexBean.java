package com.hh.hhdb_admin.mgr.index2;

import com.hh.frame.json.JsonObject;
import com.hh.hhdb_admin.common.bean.BaseRefreshBean;

public class IndexBean extends BaseRefreshBean {
    private static final String TABLENAME = "tableName";
    private static final String SCHEMA = "schema";
    
    private static final String OPR_TYPE = "opr_type";
    
    public enum OprType{
        SHOW
    }
    
    public IndexBean(OprType optype, String freshNodeId) {
        super(freshNodeId);
        setOprType(optype);
    }
    
    public IndexBean(JsonObject obj) {
        super(obj);
    }
    
    public String getTableName() {
        return jobj.getString(TABLENAME);
    }
    
    public void setTableName(String tableName) {
        jobj.set(TABLENAME, tableName);
    }
    
    public String getSchema() {
        return this.jobj.getString(SCHEMA);
    }
    
    public void setSchema(String schema) {
        this.jobj.set(SCHEMA, schema);
    }
    
    public OprType getOprType() {
        return OprType.valueOf(jobj.getString(OPR_TYPE));
    }
    
    public void setOprType(OprType colOprType) {
        jobj.set(OPR_TYPE, colOprType.name());
    }
}
