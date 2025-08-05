package com.hh.hhdb_admin.mgr.database2;

import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;


public class DatabaseMgr extends AbsGuiMgr {

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.DATABASE;
    }
    
    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        DataBaseBean csGuiBean = (DataBaseBean) bj;
        switch (csGuiBean.getOprType()) {
            case ADD:
                new DatabasePanel(CsVarUtil.jdbc,CsVarUtil.conn) {
                    @Override
                    public void inform() {
                        CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(csGuiBean.getRefreshNodeId()));
                    }
                };
                break;
            default:
                break;
        }
        return null;
    }
}
