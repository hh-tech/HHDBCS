package com.hh.hhdb_admin.common.bean;

import com.hh.frame.json.JsonObject;

public class TableOpenBean extends BaseRefreshBean {
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String TABLENAME = "tableName";
    private static final String SCHEMA = "schema";
    private static final String PATITION_TABLENAME = "partTableName";
    private static final String OPR_TYPE = "opr_type";
    private static final String OPN_OBJ = "open_obj_type";
    
    public enum OprType{
        SHOW
    }
    
    /**
     * 打开对象类型
     */
    public enum OpenObjType{
        TABLE,VIEW,PATITION
    }
    
    public TableOpenBean(OprType optype, String freshNodeId) {
        super(freshNodeId);
        setOprType(optype);
    }
    
    public TableOpenBean(JsonObject obj) {
		super(obj);
    }
    
    public String getId() {
        return jobj.getString(ID);
    }
    
    public void setId(String id) {
        jobj.set(ID, id);
    }
    
    public String getTableName() {
        return jobj.getString(TABLENAME);
    }
    
    public void setTableName(String tableName) {
        jobj.set(TABLENAME, tableName);
    }
    
    public String getPartitionTableName() {
    	return jobj.getString(PATITION_TABLENAME,"");
    }
    
    public void setPartitionTableName(String name) {
    	jobj.set(PATITION_TABLENAME,name);
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
    
    public OpenObjType getOpenObjType() {
        return OpenObjType.valueOf(jobj.getString(OPN_OBJ,OpenObjType.TABLE.name()));
    }
    
    public void setOpenObjType(OpenObjType openType) {
        jobj.set(OPN_OBJ, openType.name());
    }
    

}
