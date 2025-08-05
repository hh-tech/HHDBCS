package com.hh.hhdb_admin.mgr.trigger2;

import com.hh.frame.json.JsonObject;
import com.hh.hhdb_admin.common.bean.BaseRefreshBean;

public class TriggerBean extends BaseRefreshBean {
    private static final String TABLENAME = "tableName";
    private static final String NAME = "name";
    private static final String SCHEMA = "schema";
    
    private static final String OPR_TYPE = "opr_type";
    
    public enum OprType{
        ADD,UPDATE
    }
    
    public TriggerBean(OprType optype, String freshNodeId) {
        super(freshNodeId);
        setOprType(optype);
    }
    
    public TriggerBean(JsonObject obj) {
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
    
    public String getName() {
        return jobj.getString(NAME);
    }
    
    public void setName(String name) {
        jobj.set(NAME, name);
    }
    
    public OprType getOprType() {
        return OprType.valueOf(jobj.getString(OPR_TYPE));
    }
    
    public void setOprType(OprType colOprType) {
        jobj.set(OPR_TYPE, colOprType.name());
    }
    
}
