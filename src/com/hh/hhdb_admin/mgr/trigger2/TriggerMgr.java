package com.hh.hhdb_admin.mgr.trigger2;

import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;


public class TriggerMgr extends AbsGuiMgr {

    @Override
    public void init(JsonObject jObj) {

    }

    @Override
    public CsMgrEnum getType() {
        return CsMgrEnum.TRIGGER;
    }
    
    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        TriggerBean csGuiBean = new TriggerBean(bj.getJobj());
        String schemaName = csGuiBean.getSchema();
        String tableName = csGuiBean.getTableName();
        
        switch (csGuiBean.getOprType()) {
            case ADD:
                TriggerPanel p = new TriggerPanel(CsVarUtil.conn,CsVarUtil.jdbcCloneWithSsh(), schemaName,tableName) {
                    @Override
                    protected void inform() {
                        CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(csGuiBean.getRefreshNodeId()));
                    }
                };
                p.show();
                break;
            case UPDATE:
                new TriggerEditPanel(CsVarUtil.conn,CsVarUtil.jdbc,schemaName).show(csGuiBean.getName());
                break;
            default:
                break;
        }
        return null;
    }
}
