package com.hh.hhdb_admin.mgr.usr.util;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.HHdbPgsqlPrefixEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.dbobj2.hhdb.HHdbUser;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;

import java.sql.Connection;
import java.sql.SQLException;
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

}
