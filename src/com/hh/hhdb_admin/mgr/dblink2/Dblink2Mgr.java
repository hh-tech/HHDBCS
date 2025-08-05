package com.hh.hhdb_admin.mgr.dblink2;

import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;


public class Dblink2Mgr extends AbsGuiMgr {

    @Override
    public void init(JsonObject jObj) {
    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.DBLINK;
    }

    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        DbLinkBean csGuiBean = (DbLinkBean) bj;
        Dblink2Comp dblinkComp = new Dblink2Comp(CsVarUtil.conn, CsVarUtil.jdbc.getUser(), csGuiBean.getName()) {
            @Override
            public void refreshTree() {
                CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(csGuiBean.getRefreshNodeId()));
            }
        };
        dblinkComp.show();
        return null;
    }
}
