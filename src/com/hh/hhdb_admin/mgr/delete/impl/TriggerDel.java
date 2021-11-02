package com.hh.hhdb_admin.mgr.delete.impl;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;
import com.hh.hhdb_admin.mgr.delete.AbsDel;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;

/**
 * @Author: Jiang
 * @Date: 2021/9/13 14:15
 */
public class TriggerDel extends AbsDel {

    @Override
    public void del(NodeInfo nodeInfo) throws Exception {
        String name = nodeInfo.getName();
        String schemaName = nodeInfo.getSchemaName();
        String tableName = nodeInfo.getTableName();
        String realTriggerName = DbCmdStrUtil.toDbCmdStr(name, dbType);
        String realSchemaName = DbCmdStrUtil.toDbCmdStr(schemaName, dbType);
        switch (dbType) {
            case pgsql:
            case hhdb:
                SqlExeUtil.executeUpdate(conn, String.format("DROP TRIGGER %s ON %s.%s",
                        realTriggerName,
                        realSchemaName,
                        DbCmdStrUtil.toDbCmdStr(tableName, dbType)));
                break;
            case sqlserver:
            case oracle:
            case db2:
            case dm:
                SqlExeUtil.executeUpdate(conn, String.format("DROP TRIGGER %s.%s", realSchemaName, realTriggerName));
                break;
            case mysql:
                SqlExeUtil.executeUpdate(conn, String.format("DROP TRIGGER %s.%s",
                        realSchemaName,
                        DbCmdStrUtil.toDbCmdStr(realTriggerName, dbType)));
                break;
            default:
                throw new Exception("暂不支持该数据库");
        }
    }
}
