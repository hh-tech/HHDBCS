package com.hh.hhdb_admin.mgr.delete.impl;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;
import com.hh.hhdb_admin.mgr.delete.AbsDel;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;

/**
 * @Author: Jiang
 * @Date: 2021/9/15 14:15
 */
public class RoleDel extends AbsDel {

    @Override
    public void del(NodeInfo nodeInfo) throws Exception {
        String name = nodeInfo.getName();
        String realName = DbCmdStrUtil.toDbCmdStr(name, dbType);
        if (dbType == DBTypeEnum.oracle) {
            SqlExeUtil.executeUpdate(conn, String.format("DROP ROLE %s", realName));
        }
    }
}
