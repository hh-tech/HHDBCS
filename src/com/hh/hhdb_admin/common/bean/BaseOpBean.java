package com.hh.hhdb_admin.common.bean;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;

public class BaseOpBean extends BaseJsonBean {
    private static final String OPTYPE = "opType";  //操作类型
    private static final String SCHEMA = "schema";  //模式
    private static final String NAME = "name";      //名称
    private static final String NODEID = "nodeId";
    private static final String NODETYPE = "nodeType";
    
    public BaseOpBean(JsonObject obj) {
        this.jobj = obj;
    }
    
    public BaseOpBean(BaseOpEnum optype) {
        this.jobj.set(OPTYPE, optype.name());
    }
    
    public BaseOpEnum getOpType() {
        return BaseOpEnum.valueOf(jobj.getString(OPTYPE));
    }
    
    public String getSchema() {
        return jobj.getString(SCHEMA);
    }
    
    public void setSchema(String schema) {
        jobj.set(SCHEMA, schema);
    }
    
    public String getName() {
        return jobj.getString(NAME);
    }
    
    public void setName(String name) {
        jobj.set(NAME, name);
    }
    
    public String getNodeId() {
        return jobj.getString(NODEID);
    }
    
    public void setNodeId(String nodeId) {
        jobj.set(NODEID, nodeId);
    }
    
    public TreeMrType getNodeType() {
        return TreeMrType.valueOf(jobj.getString(NODETYPE));
    }
    
    public void setNodeType(TreeMrType nodeType) {
        jobj.set(NODETYPE, nodeType.name());
    }
}
