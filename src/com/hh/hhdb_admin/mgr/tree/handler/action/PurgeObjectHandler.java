package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.db_sql.ora.OraRecycleBinSqlUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import com.hh.hhdb_admin.comp.MultiHandleDialog;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;


/**
 * 清除对象
 */
public class PurgeObjectHandler extends AbsHandler {

    @Override
    public void resolve(CsFolderBean csNode) throws Exception {
        resolve(new CsFolderBean[]{csNode});
    }

    @Override
    public void resolve(CsFolderBean[] treeNodes) throws Exception {
        if (!PopDlgUtil.confirm(LangMgr.lang(Word4.SURE_PURGE))) return;

        CsFolderBean firstNode = treeNodes[0];
        TreeMrType treeNodeType = firstNode.getType();

        String schema = SqlStrUtil.addQuote4DDL(CsVarUtil.jdbc.getSchema(), DBTypeEnum.oracle, null);
        DBTypeEnum dbType = DriverUtil.getDbType(jdbc);
        DBTypeEnum.Version ver = jdbc.getCurrentVersion();

        MultiHandleDialog multiHandleDialog = new MultiHandleDialog(CsVarUtil.parentFrame, treeNodes, treeNode -> {
            try {
                String sql1;
                String nodeId = SqlStrUtil.addQuote4DDL(treeNode.getMrNode().getId(), DBTypeEnum.oracle, null);
                if (treeNodeType == TreeMrType.RECYCLE_BIN_TABLE) {
                    sql1 = OraRecycleBinSqlUtil.purgeTable(dbType, ver, schema, nodeId);
                } else {
                    sql1 = OraRecycleBinSqlUtil.purgeIndex(dbType, ver, schema, nodeId);
                }
                return GlobalWorkUtil.exeSql(jdbc, CsVarUtil.conn, sql1);
            } catch (Exception e) {
                return false;
            }
        });
        multiHandleDialog.setTitle(LangMgr.lang(Word2.DELETE));
        multiHandleDialog.setAfterDone(() -> {
            CsVarUtil.eng2.doCall(CsMgrEnum.TREE, new RefreshNodeBean(firstNode.getFolder().getId()));
        });
        multiHandleDialog.show();
    }
}
