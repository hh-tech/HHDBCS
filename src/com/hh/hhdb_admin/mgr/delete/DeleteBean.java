package com.hh.hhdb_admin.mgr.delete;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonArray;
import com.hh.frame.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class DeleteBean extends BaseJsonBean {
    private static final String TABLENAME="tableName";
    private static final String NODETYPE="nodeType";
    private static final String REFRESH_NODE_ID="node_id";
    private static final String OBJ_NAMES="obj_names";
    private static final String SCHEMA = "schema";  //模式

    public DeleteBean() {
        jobj.set(OBJ_NAMES, new JsonArray());
    }
    
    public DeleteBean(JsonObject obj) {
        this.jobj=obj;
    }
    public String getSchema() {
        return jobj.getString(SCHEMA);
    }
    
    public void setSchema(String schema) {
        jobj.set(SCHEMA, schema);
    }
    public String getTableName() {
        return jobj.getString(TABLENAME);
    }
    public void setTableName(String tableName) {
        jobj.set(TABLENAME, tableName);
    }
    public TreeMrType getNodeType() {
        return TreeMrType.valueOf(jobj.getString(NODETYPE));
    }
    public void setNodeType(TreeMrType nodeType) {
        jobj.set(NODETYPE, nodeType.name());
    }
    
    public void setRefreshNodeId(String  nodeId) {
    	jobj.set(REFRESH_NODE_ID, nodeId);
    }
    public String getRefreshNodeId() {
    	return jobj.getString(REFRESH_NODE_ID);
    }
    
    public void addObjName(String name) {
    	jobj.get(OBJ_NAMES).asArray().add(name);
    }
    
    public List<String> getObjNames() {
    	List<String> list = new ArrayList<>();
    	JsonArray arr = jobj.get(OBJ_NAMES).asArray();
    	for (int i = 0; i < arr.size(); i++) {
    		list.add(arr.get(i).asString());
    	}
    	return list;
    }
    
}
