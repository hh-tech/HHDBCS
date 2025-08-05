package com.hh.hhdb_admin.mgr.main_frame;

import com.hh.hhdb_admin.CsMgrEnum;


public class SharePaneObj {
    private String id;
    private String name;
    private CsMgrEnum csMgrEnumme;
    private boolean fixTab;		//tab页面是否固定
    private Object obj;			//要传递的对象
    
    
    public String getTitle() {
        return name;
    }
    
    public void setTitle(String name) {
        this.name = name;
    }
    
    public Object getObj() {
        return obj;
    }
    
    public void setObj(Object obj) {
        this.obj = obj;
    }
    
    public boolean isFixTab() {
        return fixTab;
    }
    
    public void setFixTab(boolean fixTab) {
        this.fixTab = fixTab;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public CsMgrEnum getCsMgrEnum() {
        return csMgrEnumme;
    }
    
    public void setCsMgrEnum(CsMgrEnum csMgrEnumme) {
        this.csMgrEnumme = csMgrEnumme;
    }
    
}
