package com.hh.hhdb_admin.mgr.dblink2;

import com.hh.frame.json.JsonObject;
import com.hh.hhdb_admin.common.bean.BaseRefreshBean;

public class DbLinkBean extends BaseRefreshBean {
    private static final String NAME = "name";
    public DbLinkBean(String freshNodeId) {
    	super(freshNodeId);
    }
   
    public DbLinkBean(JsonObject obj) {
        super(obj);
    }
    
    public String getName() {
        return jobj.getString(NAME);
    }
    
    public void setName(String name) {
        jobj.set(NAME, name);
    }
    
}
