package com.hh.hhdb_admin.mgr.type;

import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.CloseBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;

/**
 * @author YuSai
 */
public class TypeMgr extends AbsGuiMgr {
    @Override
    public void init(JsonObject jObj) {
    }
    
    @Override
    public Enum<?> getType() {
        return CsMgrEnum.TYPE;
    }
    
    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        if (bj instanceof CloseBean) return null;
        TypeBean csGuiBean = new TypeBean(bj.getJobj());
        switch (csGuiBean.getOprType()) {
            case ADD:
            case UPDATE:
                if (csGuiBean.getOprType() == TypeBean.OprType.ADD)
                    csGuiBean.setName(String.format("type_%s_%s", csGuiBean.getSchema().toLowerCase(), System.currentTimeMillis()));
                TypePanel addUpComp = new TypePanel(csGuiBean, CsVarUtil.conn) {
                    @Override
                    protected void inform() {
                        CsVarUtil.eng2.doCall(CsMgrEnum.TREE, new RefreshNodeBean(csGuiBean.getRefreshNodeId()));
                    }
                };
                if (csGuiBean.getOprType() == TypeBean.OprType.ADD) {
                    addUpComp.show();
                    
                } else {
                    addUpComp.show(csGuiBean.getName());
                }
                break;
            default:
                break;
        }
        return null;
    }
}
