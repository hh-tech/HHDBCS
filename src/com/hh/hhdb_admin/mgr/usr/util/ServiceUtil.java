package com.hh.hhdb_admin.mgr.usr.util;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.hhdb_admin.mgr.usr.comp.form.OrUsrForm;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceUtil {
    /**
     * 获取角色的具体信息
     */
    public static List<Map<String, Object>> getconnInformation(Connection conn, String userName) throws Exception {
        DBTypeEnum db_type = DriverUtil.getDbType(conn);
        String sql = "";
        if (db_type == DBTypeEnum.hhdb) {
            sql = "select * from hh_roles where rolname = '%s'";
        } else if (db_type == DBTypeEnum.pgsql) {
            sql = "select * from pg_roles where rolname = '%s'";
        }
        return SqlQueryUtil.select(conn, String.format(sql, SqlStrUtil.toWhereStr(db_type, userName)));
    }


    /**
     * 获取数据库profile集合--oracle
     */
    public static List<Map<String, String>> getProfile(Connection conn) throws SQLException {
        String sql = "select profile from dba_profiles group by profile";
        return SqlQueryUtil.selectStrMapList(conn, sql);
    }

    /**
     * 根据表空间类型获取表空间集合 -- oracle
     */
    public static List<Map<String, String>> getTabSpace(Connection conn, OrUsrForm.SpaceType spaceType) throws SQLException {
        if (spaceType != null) {
            String sql = String.format("select tablespace_name from dba_tablespaces where contents='%s'",
                    SqlStrUtil.toWhereStr(DriverUtil.getDbType(conn), spaceType.name()));
            return SqlQueryUtil.selectStrMapList(conn, sql);
        }
        return null;
    }

    /**
     * 获取数据库配置的默认值表空间、临时表空间 -- oracle
     */
    public static List<Map<String, String>> getTabSpaceDefault(Connection conn, OrUsrForm.SpaceType spaceType) throws SQLException {
        List<Map<String, String>> list = new ArrayList<>();
        if (spaceType != null) {
            String sql;
            if (spaceType == OrUsrForm.SpaceType.PERMANENT) {
                sql = "select PROPERTY_VALUE from database_properties where PROPERTY_NAME='DEFAULT_PERMANENT_TABLESPACE'";
                list = SqlQueryUtil.selectStrMapList(conn, sql);
            } else if (spaceType == OrUsrForm.SpaceType.TEMPORARY) {
                sql = "select PROPERTY_VALUE from database_properties where PROPERTY_NAME='DEFAULT_TEMP_TABLESPACE'";
                list = SqlQueryUtil.selectStrMapList(conn, sql);
            }

        }
        return list;
    }


    /**
     * 获取用户表空间，profile的默认值 -- oracle
     */
    public static List<Map<String, String>> getTabSpaceDefault(Connection conn, String user) throws SQLException {
        String sql = "select default_tablespace,temporary_tablespace,profile from dba_users where username = '%s'";
        return SqlQueryUtil.selectStrMapList(conn, String.format(sql, SqlStrUtil.toWhereStr(DriverUtil.getDbType(conn), user)));
    }


    /**
     * 获取默认语言 -- sqlserver
     *
     * @param conn
     * @return
     */
    public static List<Map<String, String>> getSqlServerLanguage(Connection conn) throws SQLException {
        String sql = "select alias from sys.syslanguages";
        return SqlQueryUtil.selectStrMapList(conn, sql);
    }


    /**
     * 获取数据库 -- sqlserver
     *
     * @param conn
     * @return
     */
    public static List<Map<String, String>> getSqlServerDB(Connection conn) throws SQLException {
        String sql = "SELECT name as db,'' as usr FROM Master..SysDatabases ORDER BY Name";
        return SqlQueryUtil.selectStrMapList(conn, sql);
    }


    /**
     * 获取role -- sqlserver
     *
     * @param conn
     * @return
     */
    public static List<Map<String, String>> getSqlServerDbRole(Connection conn, String dbName) throws SQLException {
        String sql = "use " + dbName + ";\n" +
                "SELECT name FROM Sysusers where altuid=1";
        return SqlQueryUtil.selectStrMapList(conn, sql);
    }


    /**
     * 获取登录名的信息 -- sqlserver
     *
     * @param conn
     * @return
     */
    public static List<Map<String, String>> getSqlServeLoginInfo(Connection conn, String usrname) throws SQLException {
        String sql =
                "Select a.*,b.is_disabled,b.is_policy_checked,is_expiration_checked from master..syslogins a left join sys.sql_logins b on a.loginname=b.name where a.loginname='%s'";
        return SqlQueryUtil.selectStrMapList(conn, String.format(sql, usrname));
    }


    /**
     * 根据登录名，获取数据库中的用户名--sqlserver
     */
    public static String getDbUsrName(Connection conn, String db, String sysUsr) throws SQLException {
        String sql = String.format("use %s;SELECT DP.name \n" +
                "FROM sys.database_principals DP ,sys.server_principals SP\n" +
                "WHERE SP.sid = DP.sid AND SP.name = '%s'", db, SqlStrUtil.toWhereStr(DriverUtil.getDbType(conn), sysUsr));
        List<String> myPerms = SqlQueryUtil.selectOneColumn(conn, sql);
        return myPerms.get(0);
    }


}
