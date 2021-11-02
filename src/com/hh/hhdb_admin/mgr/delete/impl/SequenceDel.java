package com.hh.hhdb_admin.mgr.delete.impl;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.hhdb_admin.mgr.delete.AbsDel;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;

/**
 * @Author: Jiang
 * @Date: 2021/9/13 14:08
 */
public class SequenceDel extends AbsDel {

    @Override
    public void del(NodeInfo nodeInfo) throws Exception {
        String schemaName = nodeInfo.getSchemaName();
        String name = nodeInfo.getName();
        String sql;
        switch (dbType) {
            case db2:
            case dm:
            case oracle:
            case sqlserver:
                sql = String.format("DROP SEQUENCE \"%s\".\"%s\"", schemaName, name);
                break;
            case hhdb:
            case pgsql:
                sql = String.format("DROP SEQUENCE \"%s\".\"%s\" CASCADE", schemaName, name);
                break;
            default:
                throw new Exception("暂不支持该数据库");
        }
        SqlExeUtil.executeUpdate(conn, sql);
    }
}
