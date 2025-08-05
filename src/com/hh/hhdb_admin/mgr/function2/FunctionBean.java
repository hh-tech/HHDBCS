package com.hh.hhdb_admin.mgr.function2;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.json.JsonObject;
import com.hh.hhdb_admin.common.bean.BaseRefreshBean;

public class FunctionBean extends BaseRefreshBean {
    private static final String ID = "id";
    private static final String SCHEMA = "schema";
    private static final String NAME = "name";
    private static final String PACKNAME = "packName";  //包名称
    private static final String NODETYPE = "nodeType";
    
    private static final String OPR_TYPE = "opr_type";
    
    public enum OprType{
        ADD,UPDATE,RUN,EXAMINE,ADD_DEBUG_INFO,DEBUG_FUNCTION,DEBUG
    }
    
    public FunctionBean(OprType optype,String freshNodeId) {
        super(freshNodeId);
        setOprType(optype);
    }
    
    public FunctionBean(JsonObject obj) {
        super(obj);
    }
    
    
    public String getId() {
        return jobj.getString(ID);
    }
    
    public void setId(String id) {
        jobj.set(ID, id);
    }
    
    public String getPackName() {
        return jobj.getString(PACKNAME);
    }
    
    public void setPackName(String packName) {
        jobj.set(PACKNAME, packName);
    }
    
    public TreeMrType getNodeType() {
    	if(jobj.contains(NODETYPE)) {
    		return TreeMrType.valueOf(jobj.getString(NODETYPE));
    	}else {
    		return null;
    	}
        
    }
    
    public void setNodeType(TreeMrType nodeType) {
        jobj.set(NODETYPE, nodeType.name());
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
    
    public FunctionBean.OprType getOprType() {
        return FunctionBean.OprType.valueOf(jobj.getString(OPR_TYPE));
    }
    
    public void setOprType(FunctionBean.OprType colOprType) {
        jobj.set(OPR_TYPE, colOprType.name());
    }
}
