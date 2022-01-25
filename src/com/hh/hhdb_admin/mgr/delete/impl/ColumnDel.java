package com.hh.hhdb_admin.mgr.delete.impl;
import com.hh.hhdb_admin.mgr.delete.AbsDel;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;

/**
 * @Author: Jiang
 * @Date: 2021/9/15 14:18
 */
public class ColumnDel extends AbsDel {

    @Override
    public void del(NodeInfo nodeInfo) throws Exception {
        String tableName = nodeInfo.getTableName();
        String schemaName = nodeInfo.getSchemaName();
        String name = nodeInfo.getName();
        switch (dbType) {
            case hhdb:
            case pgsql:
            case oracle:
            case sqlserver:
            case db2:
                execute(String.format("ALTER TABLE \"%s\".\"%s\" DROP COLUMN \"%s\"",
                        schemaName, tableName, name));
                break;
            case mysql:
                execute(String.format("ALTER TABLE `%s`.`%s` DROP COLUMN %s",
                        schemaName, tableName, name));
                break;
            default:
        }
    }
}
