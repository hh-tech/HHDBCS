package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.view.tree.HTreeNode;


/**
 * @author: Jiang
 * @date: 2020/9/15
 */

public class CascadeTruncateHandler extends AbsHandler {

    public static final String TRUNCATE_CASCADE = "truncate table \"%s\".\"%s\" cascade";

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        if (treeNode.getType().equals(TreeMrType.TABLE.name().toLowerCase())) {
            String tableName = treeNode.getName();
            String schemaName = getSchemaName();
            SqlExeUtil.executeUpdate(loginBean.getConn(), String.format(TRUNCATE_CASCADE, schemaName, tableName));
        }
    }
}
