package com.hh.hhdb_admin.common.bean;

import com.hh.frame.json.JsonObject;

public class VmBean extends BaseOpBean {
    private static final String TEXT = "text";  //需要显示的内容
    
    public VmBean() {
        super(BaseOpEnum.SHOW);
    }
    
    public VmBean(String text) {
        super(BaseOpEnum.SHOW);
        setText(text);
    }
    
    public VmBean(JsonObject obj) {
        super(obj);
    }
    
    public String getText() {
        return jobj.getString(TEXT);
    }
    
    public void setText(String text) {
        jobj.set(TEXT, text);
    }
}
