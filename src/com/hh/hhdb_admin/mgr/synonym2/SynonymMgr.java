package com.hh.hhdb_admin.mgr.synonym2;

import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;

/**
 * 同义词
 */
public class SynonymMgr extends AbsGuiMgr {
    
    @Override
    public void init(JsonObject jObj) {
    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.SYNONYM;
    }
    
    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        SynonymBean csGuiBean = new SynonymBean(bj.getJobj());
        String schema = csGuiBean.getSchema();
       
        switch (csGuiBean.getOprType()) {
            case SHOW:
                new SynonymPanel(CsVarUtil.conn, CsVarUtil.jdbcCloneWithSsh(),schema) {
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
