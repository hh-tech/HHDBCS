package com.hh.hhdb_admin.mgr.database2;

import com.hh.frame.json.JsonObject;
import com.hh.hhdb_admin.common.bean.BaseRefreshBean;

public class DataBaseBean extends BaseRefreshBean {
    private static final String OPR_TYPE = "opr_type";
    
    public enum OprType{
    	ADD
    }
    
    public DataBaseBean(OprType optype, String freshNodeId) {
    	super(freshNodeId);
        setOprType(optype);
    }
   
    public DataBaseBean(JsonObject obj) {
        super(obj);
    }
    

	public OprType getOprType() {
		return OprType.valueOf(jobj.getString(OPR_TYPE));
	}

	public void setOprType(OprType colOprType) {
		jobj.set(OPR_TYPE, colOprType.name());
	}
}
