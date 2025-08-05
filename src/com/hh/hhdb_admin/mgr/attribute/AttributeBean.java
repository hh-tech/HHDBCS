package com.hh.hhdb_admin.mgr.attribute;

import com.hh.frame.json.JsonObject;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.BaseOpEnum;

public class AttributeBean extends BaseOpBean {
    private static final String OID = "oid";
    private static final String DATABASENAME = "databaseName";
    private static final String TABLENAME = "tableName";
    
    public AttributeBean() {
        super(BaseOpEnum.SHOW);
    }
    
    public AttributeBean(JsonObject obj) {
        super(obj);
    }
    
    public String getOid() {
        return jobj.getString(OID);
    }
    
    public void setOid(String oid) {
        jobj.set(OID, oid);
    }
    
    public String getDatabaseName() {
        return jobj.getString(DATABASENAME);
    }
    
    public void setDatabaseName(String databaseName) {
        jobj.set(DATABASENAME, databaseName);
    }
    
    public String getTableName() {
        return jobj.getString(TABLENAME);
    }
    
    public void setTableName(String tableName) {
        jobj.set(TABLENAME, tableName);
    }
}
