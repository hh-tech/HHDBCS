package com.hh.hhdb_admin.mgr.rule;

import com.hh.frame.json.JsonObject;
import com.hh.hhdb_admin.common.bean.BaseRefreshBean;

public class RuleBean extends BaseRefreshBean {
    private static final String TABLE = "table";
    private static final String SCHEMA = "schema";
    
    private static final String OPR_TYPE = "opr_type";
    
    public enum OprType{
        ADD
    }
    
    public RuleBean(OprType optype,String freshNodeId) {
        super(freshNodeId);
        setOprType(optype);
    }
    
    public RuleBean(JsonObject obj) {
        super(obj);
    }
    
    public String getTable() {
        return jobj.getString(TABLE);
    }
    
    public void setTable(String table) {
        jobj.set(TABLE, table);
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