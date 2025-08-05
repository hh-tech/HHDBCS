package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.CsTreeUtil;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;

/**
 * @author: Jiang
 * @date: 2020/9/15
 */

public class RemovePartitionHandler extends AbsHandler {

    public static final String REMOVE_PARTITION = "ALTER TABLE %s.%s DETACH PARTITION %s.%s";

    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        String schemaName = getSchemaName();
        String parentTableName = treeNode.getFolder().getName();
        String tableName = treeNode.getName();
        SqlExeUtil.executeUpdate(CsVarUtil.conn, String.format(REMOVE_PARTITION, schemaName, parentTableName, schemaName, tableName));
        CsFolderBean tableGroupNode = CsTreeUtil.getTargetParentNode(treeNode, TreeMrType.TABLE_GROUP);
        if (tableGroupNode != null) {
            CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(treeNode.getId()));
        }
        PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.lang(Word4.EXECUTED_SUCCESSFULLY));
    }
}
