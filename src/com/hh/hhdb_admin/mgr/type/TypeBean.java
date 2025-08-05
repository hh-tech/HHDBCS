package com.hh.hhdb_admin.mgr.type;

import com.hh.frame.json.JsonObject;
import com.hh.hhdb_admin.common.bean.BaseRefreshBean;

public class TypeBean extends BaseRefreshBean {
    private static final String NAME = "name";
    private static final String SCHEMA = "schema";
    private static final String TYPE = "type";
    private static final String FILENAME = "fileName";     //类型文件名称
    private static final String TEXT = "text";             //文件内容
    
    private static final String OPR_TYPE = "opr_type";
    
    public enum OprType{
        ADD,UPDATE
    }
    
    public TypeBean(OprType optype, String freshNodeId) {
        super(freshNodeId);
        setOprType(optype);
    }
    
    public TypeBean(JsonObject obj) {
        super(obj);
    }
    
    public String getType() {
        return jobj.getString(TYPE);
    }
    
    public void setType(String type) {
        jobj.set(TYPE, type);
    }
    
    public String getFileName() {
        return jobj.getString(FILENAME);
    }
    
    public void setFileName(String fileName) {
        jobj.set(FILENAME, fileName);
    }
    
    public String getText() {
        return jobj.getString(TEXT);
    }
    
    public void setText(String text) {
        jobj.set(TEXT, text);
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
