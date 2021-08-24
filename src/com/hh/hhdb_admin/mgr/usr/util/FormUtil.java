package com.hh.hhdb_admin.mgr.usr.util;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.userMr.base.UsrFormType;
import com.hh.hhdb_admin.mgr.usr.comp.GrantTableComp;
import com.hh.hhdb_admin.mgr.usr.comp.SqlServerGrantComp;
import com.hh.hhdb_admin.mgr.usr.comp.form.*;

import java.sql.Connection;
import java.sql.SQLException;

public class FormUtil {

    public static UsrBaseForm getUsrBaseForm(DBTypeEnum typeEnum, Connection conn, UsrFormType compType) {
        if (compType == UsrFormType.usr) {
            if (typeEnum == DBTypeEnum.hhdb) {
                return new HHUsrForm(conn);
            } else if (typeEnum == DBTypeEnum.pgsql) {
                return new HHUsrForm(conn);
            } else if (typeEnum == DBTypeEnum.oracle) {
                return new OrUsrForm(conn);
            } else if (typeEnum == DBTypeEnum.mysql) {
                return new MysqlUsrForm(conn);
            } else if (typeEnum == DBTypeEnum.sqlserver) {
                return new SqlServerLoginForm(conn);
            }
        } else if (compType == UsrFormType.role) {
            if (typeEnum == DBTypeEnum.oracle) {
                return new OrRoleForm(conn);
            }
        }
        return null;
    }

    public static GrantTableComp getGrantTableComp(Connection conn, GrantTableComp.PrivsType type) throws SQLException {
        DBTypeEnum typeEnum = DriverUtil.getDbType(conn);
        if (typeEnum == DBTypeEnum.sqlserver) {
            return new SqlServerGrantComp(type, conn);
        } else {
            return new GrantTableComp(type, conn);
        }

    }


}
