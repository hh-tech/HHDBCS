package com.hh.hhdb_admin.mgr.delete.impl;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.hhdb_admin.mgr.delete.AbsDel;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;

/**
 * @Author: Jiang
 * @Date: 2021/9/15 14:25
 */
public class RuleDel extends AbsDel {


    @Override
    public void del(NodeInfo nodeInfo) throws Exception {
        String sql = "DROP RULE \"%s\" ON \"%s\".\"%s\"";
        String schemaName = nodeInfo.getSchemaName();
        String tableName = nodeInfo.getTableName();
        String name = nodeInfo.getName();
        SqlExeUtil.executeUpdate(conn, String.format(sql, name, schemaName, tableName));
    }
}
