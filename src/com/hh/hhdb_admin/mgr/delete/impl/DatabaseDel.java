package com.hh.hhdb_admin.mgr.delete.impl;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
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
        DBTypeEnum type = DriverUtil.getDbType(conn);
        switch (type) {
            case hhdb:
            case pgsql:
                SqlExeUtil.executeUpdate(conn, String.format("DROP DATABASE IF EXISTS \"%s\"", name));
                return;
            case mysql:
                SqlExeUtil.executeUpdate(conn, String.format("DROP DATABASE IF EXISTS `%s`", name));
                return;
            case sqlserver:
                SqlExeUtil.executeUpdate(conn, String.format("DROP DATABASE \"%s\"", name));
                return;
            case db2:
                SqlExeUtil.executeUpdate(conn, String.format("db2 drop database \"%s\"", name));
                return;
            default:
                throw new Exception("暂不支持该数据库");
        }
    }
}
