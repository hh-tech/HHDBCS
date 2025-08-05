package com.hh.hhdb_admin.mgr.tablespace;

import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.table_space2.TableSpaceBean;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;

import java.sql.Connection;


public class TableSpaceMgr extends AbsGuiMgr {
    @Override
    public void init(JsonObject jObj) {}

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.TABLE_SPACE;
    }
    
    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        TableSpaceBean csGuiBean = new TableSpaceBean(bj.getJobj());
        Connection conn = CsVarUtil.conn;
        TableSpaceComp spaceComp = new TableSpaceComp(conn, CsVarUtil.getDbtype()) {
            @Override
            public void refreshTree() {
                CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(csGuiBean.getRefreshNodeId()));
            }
        };
        switch (csGuiBean.getOprType()) {
            case SHOW:
                spaceComp.show();
                break;
            default:
                break;
        }
        return null;
    }
}
