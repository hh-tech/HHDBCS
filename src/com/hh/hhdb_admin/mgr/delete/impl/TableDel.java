package com.hh.hhdb_admin.mgr.delete.impl;

import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.hhdb_admin.mgr.delete.AbsDel;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;

/**
 * @Author: Jiang
 * @Date: 2021/9/9 9:28
 */
public class TableDel extends AbsDel {

    @Override
    public void del(NodeInfo nodeInfo) throws Exception {
        String nodeName = nodeInfo.getName();
        String schemaName = nodeInfo.getSchemaName();
        String sql;
        switch (dbType) {
            case oracle:
            case dm:
                sql = String.format("DROP TABLE %s.%s CASCADE CONSTRAINTS", SqlStrUtil.dealDoubleQuote(dbType, schemaName),
                        SqlStrUtil.dealDoubleQuote(dbType, nodeName));
                break;
            case mysql:
                sql = String.format("DROP TABLE `%s`.`%s`", schemaName, nodeName);
                break;
            case hhdb:
            case pgsql:
            	sql = String.format("DROP TABLE %s.%s CASCADE", SqlStrUtil.dealDoubleQuote(dbType, schemaName),
                        SqlStrUtil.dealDoubleQuote(dbType, nodeName));
                break;
            case db2:
                sql = String.format("DROP TABLE %s.%s", SqlStrUtil.dealDoubleQuote(dbType, schemaName),
                        SqlStrUtil.dealDoubleQuote(dbType, nodeName));
                break;
            case sqlserver:
                sql = String.format("DROP TABLE %s.%s", SqlStrUtil.dealDoubleQuote(dbType, schemaName),
                        SqlStrUtil.dealDoubleQuote(dbType, nodeName));
                break;
            default:
                throw new Exception("暂不支持该数据库");
        }
        execute(sql);
    }

}
