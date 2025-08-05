package com.hh.hhdb_admin.mgr.table_space2;

import com.hh.frame.json.JsonObject;
import com.hh.hhdb_admin.common.bean.BaseRefreshBean;

public class TableSpaceBean extends BaseRefreshBean {
    private static final String OPR_TYPE = "opr_type";
    
    public enum OprType{
        SHOW
    }
    
    public TableSpaceBean(OprType optype, String freshNodeId) {
        super(freshNodeId);
        setOprType(optype);
    }
    
    public TableSpaceBean(JsonObject obj) {
		super(obj);
    }
    
    public OprType getOprType() {
        return OprType.valueOf(jobj.getString(OPR_TYPE));
    }
    
    public void setOprType(OprType colOprType) {
        jobj.set(OPR_TYPE, colOprType.name());
    }
}
