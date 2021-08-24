package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.tree.TreeComp;
import com.hh.hhdb_admin.mgr.tree.TreeUtil;

/**
 * @author: Jiang
 * @date: 2020/9/15
 */

public class RemovePartitionHandler extends AbsHandler {

    public static final String REMOVE_PARTITION = "ALTER TABLE %s.%s DETACH PARTITION %s.%s";

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        String schemaName = getSchemaName();
        String parentTableName = treeNode.getParentHTreeNode().getName();
        String tableName = treeNode.getName();
        SqlExeUtil.executeUpdate(loginBean.getConn(), String.format(REMOVE_PARTITION, schemaName, parentTableName, schemaName, tableName));
        HTreeNode tableGroupNode = TreeUtil.getTargetParentNode(treeNode, TreeMrType.TABLE_GROUP);
        if (tableGroupNode != null) {
            refreshWithNode(tableGroupNode);
        }
        PopPaneUtil.info(StartUtil.parentFrame.getWindow(), TreeComp.getLang("remove_partition_success"));
    }
}
