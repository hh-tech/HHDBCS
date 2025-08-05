package com.hh.hhdb_admin.common.bean;

import com.hh.frame.json.JsonObject;

public class BarVisibelBean extends BaseOpBean {
	private static final String VISIBLE = "visible";
	
    public BarVisibelBean(BaseOpEnum optype,boolean visible) {
        super(optype);
        setVisible(visible);
    }
    
    public BarVisibelBean(JsonObject obj) {
        super(obj);
    }
    
    public boolean getVisible() {
        return jobj.getBoolean(VISIBLE);
    }
    
    public void setVisible(boolean visible) {
        jobj.set(VISIBLE, visible);
    }
}