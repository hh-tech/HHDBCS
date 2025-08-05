package com.hh.hhdb_admin.mgr.usr2;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.json.JsonObject;
import com.hh.hhdb_admin.common.bean.BaseRefreshBean;
import org.apache.commons.lang3.StringUtils;

public class UsrBean extends BaseRefreshBean {
    private static final String NAME = "name";
    private static final String DB_TYPE = "dbType";
    private static final String VERSION = "version";
    
    private static final String OPR_TYPE = "opr_type";
    
    public enum OprType{
        ADD_USER, UPDATE_USER,ADD_ROLE,UPDATE_ROLE,RENAME, PERM
    }
    
    public UsrBean(OprType optype) {
        this(optype,null);
    }
    
    public UsrBean(OprType optype, String freshNodeId) {
        super(freshNodeId);
        setOprType(optype);
    }
    
    public UsrBean(JsonObject obj) {
        super(obj);
    }
    
    public String getName() {
        return jobj.getString(NAME);
    }
    
    public void setName(String name) {
        jobj.set(NAME, name);
    }
    
    public DBTypeEnum getDbType() {
        return DBTypeEnum.valueOf(jobj.getString(DB_TYPE));
    }
    
    public void setDbType(DBTypeEnum dbType) {
        jobj.set(DB_TYPE, dbType.name());
    }
    
    public DBTypeEnum.Version getVersion() {
        return StringUtils.isNotBlank(jobj.getString(VERSION)) ?DBTypeEnum.Version.valueOf(jobj.getString(VERSION)) : null;
    }
    
    public void setVersion(DBTypeEnum.Version version) {
        if (null != version) jobj.set(VERSION, version.name());
    }
    
    public OprType getOprType() {
        return OprType.valueOf(jobj.getString(OPR_TYPE));
    }
    
    public void setOprType(OprType colOprType) {
        jobj.set(OPR_TYPE, colOprType.name());
    }
    
    public boolean isUser(){
        if (getOprType() == OprType.ADD_USER || getOprType() == OprType.UPDATE_USER) {
            return true;
        }
        return false;
    }
    
    public boolean isRole(){
        if (getOprType() == OprType.ADD_ROLE || getOprType() == OprType.UPDATE_ROLE) {
            return true;
        }
        return false;
    }
}
