package com.hh.hhdb_admin.common.bean;

import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;

public class SqlBookBean extends BaseJsonBean {
    private static final String PATH = "path";
    
    public SqlBookBean(String path) {
        setPath(path);
    }
    
    public SqlBookBean(JsonObject obj) {
        this.jobj = obj;
    }
    
    public String getPath() {
        return jobj.getString(PATH);
    }
    
    public void setPath(String path) {
        jobj.set(PATH, path);
    }
}
