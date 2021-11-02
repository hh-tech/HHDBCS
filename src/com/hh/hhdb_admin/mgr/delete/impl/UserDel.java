package com.hh.hhdb_admin.mgr.delete.impl;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.create_dbobj.userMr.UsrMrUtil;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.delete.AbsDel;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: Jiang
 * @Date: 2021/9/15 14:07
 */
public class UserDel extends AbsDel {

    @Override
    public void del(NodeInfo nodeInfo) throws Exception {
        String name = nodeInfo.getName();
        if (jdbcBean.getUser().equals(name)) {
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), "无法删除当前登录用户");
            return;
        }

        String realName = DbCmdStrUtil.toDbCmdStr(name, dbType);
        switch (dbType) {
            case hhdb:
            case pgsql:
                SqlExeUtil.executeUpdate(conn, String.format("DROP ROLE %s", realName));
                break;
            case oracle:
                SqlExeUtil.executeUpdate(conn, String.format("DROP USER %s CASCADE", realName));
                break;
            case mysql:
                SqlExeUtil.executeUpdate(conn, String.format("DROP USER %s", UsrMrUtil.toMysqlUsrHost(name)));
                break;
            case sqlserver:
                String sql = "SELECT name as db FROM Master..SysDatabases ORDER BY Name";
                List<String> privileges = SqlQueryUtil.selectOneColumn(conn, sql);
                StringBuffer buffer = new StringBuffer();
                for (String db : privileges) {
                    String dbUser = String.format("use %s;SELECT DP.name \n" +
                            "FROM sys.database_principals DP ,sys.server_principals SP\n" +
                            "WHERE SP.sid = DP.sid AND SP.name = '%s'", db, name);
                    List<String> myPerms = SqlQueryUtil.selectOneColumn(conn, dbUser);
                    if (!myPerms.isEmpty()) {
                        buffer.append(String.format("USE [%s];", db));
                        buffer.append(System.lineSeparator());
                        buffer.append(String.format("EXEC sp_dropuser [%s];", myPerms.get(0)));
                        buffer.append(System.lineSeparator());
                    }
                }
                buffer.append(String.format("DROP LOGIN [%s];", name));
                SqlExeUtil.batchExecute(conn, Arrays.asList(buffer.toString().split(System.lineSeparator())));
                break;
            default:
        }
    }
}
