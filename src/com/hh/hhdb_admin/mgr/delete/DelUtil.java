package com.hh.hhdb_admin.mgr.delete;

import java.sql.SQLException;

/**
 * @Author: Jiang
 * @Date: 2021/9/15 15:35
 */
public class DelUtil {

    public static void constraintDel(AbsDel absDel, NodeInfo nodeInfo) throws SQLException {
        String name = nodeInfo.getName();
        String schemaName = nodeInfo.getSchemaName();
        String tableName = nodeInfo.getTableName();
        String sql = "";
        switch (absDel.dbType) {
            case hhdb:
            case pgsql:
            case oracle:
            case sqlserver:
            case dm:
                sql = String.format("ALTER TABLE \"%s\".\"%s\" DROP CONSTRAINT \"%s\"", schemaName, tableName, name);
                break;
            case db2:
                if (name.contains(".")) {
                    String column = name.replace("\"", "").split("\\.")[1];
                    sql = String.format("ALTER TABLE \"%s\".\"%s\" ALTER COLUMN \"%s\" DROP NOT NULL",
                            schemaName, tableName, column);
                    absDel.execute(sql);
                    sql = String.format("CALL SYSPROC.ADMIN_CMD('reorg table \"%s\".\"%s\"')", schemaName, tableName);
                    absDel.execute(sql);
                    return;
                } else {
                    sql = String.format("ALTER TABLE \"%s\".\"%s\" DROP CONSTRAINT \"%s\"", schemaName, tableName, name);
                }
                break;
            case mysql:
                switch (nodeInfo.getTreeMrType()) {
                    case UNIQUE_KEY:
                        sql = String.format("ALTER TABLE %s DROP key %s", tableName, name);
                        break;
                    case FOREIGN_KEY:
                        sql = String.format("ALTER TABLE %s DROP FOREIGN KEY %s", tableName, name);
                        break;
                    case PRIMARY_KEY:
                        sql = String.format("ALTER TABLE %s DROP PRIMARY KEY", tableName);
                        break;
                    default:
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + absDel.dbType);
        }
        absDel.execute(sql);
    }
}
