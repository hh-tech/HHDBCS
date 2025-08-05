package com.hh.hhdb_admin.common.bean;

import com.hh.frame.json.JsonObject;

public class CloseBean extends BaseOpBean {
    private static final String ID = "id";
    
    public CloseBean(String id) {
        super(BaseOpEnum.CLOSE);
        setId(id);
    }
    
    public CloseBean(JsonObject obj) {
		super(obj);
    }
    
    public String getId() {
        return jobj.getString(ID);
    }
    
    public void setId(String id) {
        jobj.set(ID, id);
    }
    
    
}
