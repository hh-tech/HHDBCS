package com.hh.hhdb_admin.mgr.redis_obj;

import com.hh.frame.json.JsonObject;
import com.hh.hhdb_admin.common.bean.BaseRefreshBean;

public class RedisObjBean extends BaseRefreshBean {
    private static final String NAME = "name";
    private static final String SCHEMA = "schema";
    
    private static final String OPR_TYPE = "opr_type";
    
    public enum OprType{
        ADD,UPDATE
    }
    
    public RedisObjBean(OprType optype,String freshNodeId) {
        super(freshNodeId);
        setOprType(optype);
    }
    
    public RedisObjBean(JsonObject obj) {
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
