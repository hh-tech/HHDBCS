package com.hh.hhdb_admin.mgr.delete;

import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;


public class DeleteMgr extends AbsGuiMgr {
    @Override
    public void init(JsonObject jObj) {

    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.DELETE;
    }
    
    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        DeleteBean deleteBean = (DeleteBean)bj;
        new DeleteComp(deleteBean, CsVarUtil.jdbcCloneWithSsh(), CsVarUtil.parentFrame);
        return null;
    }
}
