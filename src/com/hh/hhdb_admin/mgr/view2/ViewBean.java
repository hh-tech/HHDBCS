package com.hh.hhdb_admin.mgr.view2;

import com.hh.frame.json.JsonObject;
import com.hh.hhdb_admin.common.bean.BaseRefreshBean;

public class ViewBean extends BaseRefreshBean {
    private static final String NAME = "name";
    private static final String SCHEMA = "schema";
    
    private static final String OPR_TYPE = "opr_type";
    
    public enum OprType{
        ADD,ADD_MVIEW,UPDATE, MODIFY, SHOW
    }
    
    public ViewBean(OprType optype, String freshNodeId) {
        super(freshNodeId);
        setOprType(optype);
    }
    
    public ViewBean(JsonObject obj) {
        super(obj);
    }
    
    public String getName() {
        return jobj.getString(NAME);
    }
    
    public void setName(String name) {
        jobj.set(NAME, name);
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
