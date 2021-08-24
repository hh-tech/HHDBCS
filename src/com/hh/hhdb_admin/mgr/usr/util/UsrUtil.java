package com.hh.hhdb_admin.mgr.usr.util;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.HHdbPgsqlPrefixEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.create_dbobj.userMr.UsrMrUtil;
import com.hh.frame.dbobj2.hhdb.HHdbUser;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UsrUtil {
    public static String formatName(DBTypeEnum typeEnum, String userName) {
        if (typeEnum == DBTypeEnum.mysql || typeEnum == DBTypeEnum.sqlserver) {
            return userName;
        } else {
            return DbCmdStrUtil.toDbCmdStr(userName, typeEnum);
        }
    }

    public static boolean isSuperUsrExtra(Connection conn, String userName) throws SQLException {
        DBTypeEnum typeEnum = DriverUtil.getDbType(conn);
        if (typeEnum == DBTypeEnum.pgsql || typeEnum == DBTypeEnum.hhdb) {
            HHdbUser hHdbUser = new HHdbUser(conn, typeEnum == DBTypeEnum.hhdb ? HHdbPgsqlPrefixEnum.hh : HHdbPgsqlPrefixEnum.pg);
            Map<String, String> usrProp = hHdbUser.getProp(userName);
            return !usrProp.isEmpty() && usrProp.get("is_superuser").equals("t");
        }
        return false;
    }

    public static String getPermTitleKey(DBTypeEnum typeEnum) {
        if (typeEnum == DBTypeEnum.mysql) {
            return "GLOBAL";
        } else if (typeEnum == DBTypeEnum.hhdb) {
            return "DATABASE";
        } else if (typeEnum == DBTypeEnum.pgsql) {
            return "DATABASE";
        } else {
            return "PRIVILEGE";
        }

    }

    /**
     * 删除用户
     *
     * @param conn     连接
     * @param userName 用户名
     * @throws Exception e
     */
    public static void delUser(Connection conn, String userName) throws Exception {
        DBTypeEnum typeEnum = DriverUtil.getDbType(conn);
        String name = DbCmdStrUtil.toDbCmdStr(userName, typeEnum);
        switch (typeEnum) {
            case hhdb:
            case pgsql:
                SqlExeUtil.executeUpdate(conn, String.format("DROP ROLE %s", name));
                break;
            case oracle:
                SqlExeUtil.executeUpdate(conn, String.format("DROP USER %s CASCADE", name));
                break;
            case mysql:
                SqlExeUtil.executeUpdate(conn, String.format("DROP USER %s", UsrMrUtil.toMysqlUsrHost(userName)));
                break;
            case sqlserver:
                String sql = "SELECT name as db FROM Master..SysDatabases ORDER BY Name";
                List<String> privileges = SqlQueryUtil.selectOneColumn(conn, sql);
                StringBuffer buffer = new StringBuffer();
                for (String db : privileges) {
                    String dbUser = String.format("use %s;SELECT DP.name \n" +
                            "FROM sys.database_principals DP ,sys.server_principals SP\n" +
                            "WHERE SP.sid = DP.sid AND SP.name = '%s'", db, userName);
                    List<String> myPerms = SqlQueryUtil.selectOneColumn(conn, dbUser);
                    if (!myPerms.isEmpty()) {
                        buffer.append(String.format("USE [%s];", db));
                        buffer.append(System.lineSeparator());
                        buffer.append(String.format("EXEC sp_dropuser [%s];", myPerms.get(0)));
                        buffer.append(System.lineSeparator());
                    }
                }
                buffer.append(String.format("DROP LOGIN [%s];", userName));
                SqlExeUtil.batchExecute(conn, Arrays.asList(buffer.toString().split(System.lineSeparator())));
                break;
            default:
        }
    }


    /**
     * 删除角色
     *
     * @param conn 连接
     */
    public static void delRole(Connection conn, String roleName) throws Exception {
        DBTypeEnum typeEnum = DriverUtil.getDbType(conn);
        String name = DbCmdStrUtil.toDbCmdStr(roleName, typeEnum);
        if (typeEnum == DBTypeEnum.oracle) {
            SqlExeUtil.executeUpdate(conn, String.format("DROP ROLE %s", name));
        }
    }
}
