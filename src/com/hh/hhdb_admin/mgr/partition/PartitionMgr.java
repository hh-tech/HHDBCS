package com.hh.hhdb_admin.mgr.partition;

import java.sql.Connection;

import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.CloseBean;
import com.hh.hhdb_admin.common.bean.TableOpenBean;
import com.hh.hhdb_admin.common.bean.TableOpenBean.OpenObjType;
import com.hh.hhdb_admin.common.bean.TableOpenBean.OprType;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.partition.comp.TablePartitionComp;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;

public class PartitionMgr extends AbsGuiMgr {


    @Override
    public void init(JsonObject jObj) {
    }

    @Override
    public CsMgrEnum getType() {
        return CsMgrEnum.PARTITION;
    }

    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        if (bj instanceof CloseBean) {
        	CsVarUtil.eng2.doCall(CsMgrEnum.TABLE_OPEN, new CloseBean(bj.getJobj()));
        } else {
            PartitionBean pBean = new PartitionBean(bj.getJobj());
            String schemaName = pBean.getSchema();
            String tableName = pBean.getTableName();
            String name = pBean.getName();
            JdbcInfo jdbc = CsVarUtil.jdbc;
            Connection conn = CsVarUtil.conn;
            jdbc.setSchema(schemaName);
            switch (pBean.getOprType()) {
                case ADD:
                    TablePartitionComp parCom = new TablePartitionComp(conn, jdbc, schemaName, tableName) {
                        @Override
                        protected void refresh() {
                            CsVarUtil.eng2.doCall(CsMgrEnum.TREE, new RefreshNodeBean(pBean.getRefreshNodeId()));
                        }
                    };
                    parCom.show();
                    break;
                case OPEN:
                	TableOpenBean bean = new TableOpenBean(OprType.SHOW,pBean.getRefreshNodeId());
                	bean.setPartitionTableName(name);
                	bean.setSchema(schemaName);
                	bean.setTableName(tableName);
                	bean.setOpenObjType(OpenObjType.PATITION);
                	CsVarUtil.eng2.doCall(CsMgrEnum.TABLE_OPEN,bean);
                    break;
            }
        }
        return null;
    }



}
