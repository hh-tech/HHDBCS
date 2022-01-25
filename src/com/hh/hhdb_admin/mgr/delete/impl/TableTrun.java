package com.hh.hhdb_admin.mgr.delete.impl;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.hhdb_admin.mgr.delete.AbsDel;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;

/**
 * 表数据清空
 */
public class TableTrun extends AbsDel {
    
    private static final String TRUNCATE_TABLE = "truncate table %s.%s";
    
    @Override
    public void del(NodeInfo nodeInfo) throws Exception {
        String nodeName = nodeInfo.getName();
        String schemaName = nodeInfo.getSchemaName();
        String sql;
        switch (dbType) {
            case oracle:
            case hhdb:
            case pgsql:
            case sqlserver:
            case mysql:
            case dm:
                sql = String.format(TRUNCATE_TABLE, SqlStrUtil.dealDoubleQuote(dbType, schemaName), SqlStrUtil.dealDoubleQuote(dbType, nodeName));
                break;
            case db2:
                sql = String.format(TRUNCATE_TABLE + " IMMEDIATE", SqlStrUtil.dealDoubleQuote(dbType, schemaName), SqlStrUtil.dealDoubleQuote(dbType, nodeName));
                break;
            default:
                throw new Exception("暂不支持该数据库");
        }
        execute(sql);
    }
}
