package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;


/**
 * @author: Jiang
 * @date: 2020/9/15
 */

public class CascadeTruncateHandler extends AbsHandler {

    public static final String TRUNCATE_CASCADE = "truncate table \"%s\".\"%s\" cascade";

    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        if (treeNode.getType() == TreeMrType.TABLE) {
            String tableName = treeNode.getName();
            String schemaName = getSchemaName();
            SqlExeUtil.executeUpdate(CsVarUtil.conn, String.format(TRUNCATE_CASCADE, schemaName, tableName));
        }
    }
}
