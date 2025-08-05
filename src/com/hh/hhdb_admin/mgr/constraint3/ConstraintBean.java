package com.hh.hhdb_admin.mgr.constraint3;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.json.JsonObject;
import com.hh.hhdb_admin.common.bean.BaseRefreshBean;

public class ConstraintBean extends BaseRefreshBean {
    private static final String SCHEMA = "schema";
    private static final String TABLENAME = "tableName";
    private static final String CONSTTYPE = "constType";
    private static final String OPR_TYPE = "opr_type";
    
    public enum OprType{
        FK,UK,PK,CK
    }
    
    public ConstraintBean(OprType optype,String freshNodeId) {
        super(freshNodeId);
        setOprType(optype);
    }
    
    public ConstraintBean(JsonObject obj) {
        super(obj);
    }
    
    public String getTableName() {
        return jobj.getString(TABLENAME);
    }
    
    public void setTableName(String tableName) {
        jobj.set(TABLENAME, tableName);
    }
    
    public TreeMrType getConstType() {
        return TreeMrType.valueOf(jobj.getString(CONSTTYPE));
    }
    
    public void setConstType(TreeMrType constType) {
        jobj.set(CONSTTYPE, constType.name());
    }
    
    public String getSchema() {
        return this.jobj.getString(SCHEMA);
    }
    
    public void setSchema(String schema) {
        this.jobj.set(SCHEMA, schema);
    }
    
    public ConstraintBean.OprType getOprType() {
        return ConstraintBean.OprType.valueOf(jobj.getString(OPR_TYPE));
    }
    
    public void setOprType(ConstraintBean.OprType colOprType) {
        jobj.set(OPR_TYPE, colOprType.name());
    }
}
