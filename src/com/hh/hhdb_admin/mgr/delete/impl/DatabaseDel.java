package com.hh.hhdb_admin.mgr.delete.impl;
import com.hh.hhdb_admin.mgr.delete.AbsDel;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;

/**
 * @Author: Jiang
 * @Date: 2021/9/13 10:30
 */
public class DatabaseDel extends AbsDel {

    @Override
    public void del(NodeInfo nodeInfo) throws Exception {
        String name = nodeInfo.getName();
        switch (dbType) {
            case hhdb:
            case pgsql:
                execute(String.format("DROP DATABASE IF EXISTS \"%s\"", name));
                return;
            case mysql:
                execute(String.format("DROP DATABASE IF EXISTS `%s`", name));
                return;
            case sqlserver:
                execute(String.format("DROP DATABASE \"%s\"", name));
                return;
            case db2:
                execute(String.format("db2 drop database \"%s\"", name));
                return;
            default:
                throw new Exception("暂不支持该数据库");
        }
    }
}
