package com.hh.hhdb_admin.mgr.delete.impl;

import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.hhdb_admin.mgr.delete.AbsDel;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;

/**
 * @Author: Jiang
 * @Date: 2021/9/9 12:19
 */
public class MViewDel extends AbsDel {

    @Override
    public void del(NodeInfo nodeInfo) throws Exception {
        String nodeName = nodeInfo.getName();
        String schemaName = nodeInfo.getSchemaName();
        String sql;
        switch (dbType) {
            case oracle:
            case sqlserver:
            case dm:
                sql = String.format("DROP MATERIALIZED VIEW %s.%s", SqlStrUtil.dealDoubleQuote(dbType, schemaName),
                        SqlStrUtil.dealDoubleQuote(dbType, nodeName));
                break;
            case mysql:
                sql = String.format("DROP MATERIALIZED VIEW `%s`.`%s`", schemaName, nodeName);
                break;
            case hhdb:
            case pgsql:
            case db2:
                sql = String.format("DROP MATERIALIZED VIEW %s.%s CASCADE", SqlStrUtil.dealDoubleQuote(dbType, schemaName),
                        SqlStrUtil.dealDoubleQuote(dbType, nodeName));
                break;
            default:
                throw new Exception("暂不支持该数据库");
        }
        execute(sql);
    }
}
