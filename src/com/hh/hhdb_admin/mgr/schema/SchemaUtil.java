package com.hh.hhdb_admin.mgr.schema;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.HHdbPgsqlPrefixEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.common.util.db.SqlStrUtil;

/**
 * @author: Jiang
 * @date: 2020/12/22
 */

public class SchemaUtil {

    /**
     * 不支持修改模式名的数据库
     */
    private static final DBTypeEnum[] NOT_EDIT_SCHEMA_NAME = {DBTypeEnum.sqlserver, DBTypeEnum.db2, DBTypeEnum.dm};


    public static String getSchemaComment(Connection conn, String schemaName) throws SQLException {
        DBTypeEnum dbTypeEnum = DriverUtil.getDbType(conn);
        switch (dbTypeEnum) {
            case hhdb:
            case pgsql:
                HHdbPgsqlPrefixEnum prefixEnum = dbTypeEnum == DBTypeEnum.hhdb ? HHdbPgsqlPrefixEnum.hh : HHdbPgsqlPrefixEnum.pg;
                String sql = "select des.description as comment\n" +
                        "from " + prefixEnum + "_namespace nsp\n" +
                        "         LEFT JOIN " + prefixEnum + "_description des ON des.objoid = nsp.oid\n" +
                        "where nsp.nspname = %s";
                return SqlQueryUtil.selectOneStrMap(conn, String.format(sql, SqlStrUtil.dealSingleQuote(dbTypeEnum, schemaName)))
                        .get("comment");
            case sqlserver:
                String tempSchema = SqlStrUtil.dealSingleQuote(dbTypeEnum, schemaName);
                return SqlQueryUtil.selectOneStrMap(conn, String.format(SQLSERVER_GET_COMMENT, tempSchema)).get("comment");
            default:
                return "";
        }
    }


    /**
     * 根据数据库类型判断模式名是否可以修改
     *
     * @param oldName 模式名
     * @param conn    连接
     * @return 是否可修改
     */
    public static boolean isNameEditable(String oldName, Connection conn) {
        if (StringUtils.isBlank(oldName)) {
            return true;
        }
        DBTypeEnum dbTypeEnum;
        try {
            dbTypeEnum = DriverUtil.getDbType(conn);
        } catch (SQLException e) {
            return false;
        }
        return !Arrays.asList(NOT_EDIT_SCHEMA_NAME).contains(dbTypeEnum);
    }

    /**
     * 创建模式
     *
     * @param conn       conn
     * @param user       用户，针对hhdb pg
     * @param schemaName 模式名
     * @param comment    模式注释
     * @throws SQLException e
     */
    public static void addSchema(Connection conn, String user, String schemaName, String comment) throws SQLException {
        DBTypeEnum type = DriverUtil.getDbType(conn);
        boolean isCommentNotEmpty = StringUtils.isNotEmpty(comment);
        switch (type) {
            case hhdb:
            case pgsql:
                SqlExeUtil.executeUpdate(conn, String.format("CREATE SCHEMA %s AUTHORIZATION %s;", schemaName, user));
                if (isCommentNotEmpty) {
                    comment = SqlStrUtil.dealSingleQuote(type, comment);
                    SqlExeUtil.executeUpdate(conn, String.format("COMMENT ON SCHEMA %s IS %s", schemaName, comment));
                }
                break;
            case sqlserver:
                SqlExeUtil.executeUpdate(conn, String.format("CREATE SCHEMA %s", schemaName));
                if (isCommentNotEmpty) {
                    SqlExeUtil.executeUpdate(conn, String.format("exec sp_addextendedproperty 'MS_Description', %s, 'SCHEMA', %s", comment, schemaName));
                }
                break;
            case db2:
                SqlExeUtil.executeUpdate(conn, String.format("CREATE SCHEMA %s", schemaName));
                if (isCommentNotEmpty) {
                    comment = SqlStrUtil.dealSingleQuote(type, comment);
                    SqlExeUtil.executeUpdate(conn, String.format("COMMENT ON SCHEMA %s is %s", schemaName, comment));
                }
                break;
            case dm:
                SqlExeUtil.executeUpdate(conn, String.format("CREATE SCHEMA %s AUTHORIZATION %s", schemaName, user));
                break;
            default:
                break;
        }
    }

    public static void updateSchema(Connection conn, String oldSchemaName, String newSchemaName, String oldComment,
                                    String newComment) throws SQLException {
        DBTypeEnum type = DriverUtil.getDbType(conn);
        boolean isCommentHasEdit = !oldComment.equals(newComment);
        boolean isNameHasEdit = !oldSchemaName.equals(newSchemaName);
        switch (type) {
            case hhdb:
            case pgsql:
                oldSchemaName = SqlStrUtil.dealDoubleQuote(type, oldSchemaName);
                newComment = SqlStrUtil.dealSingleQuote(type, newComment);
                if (isNameHasEdit) {
                    SqlExeUtil.executeUpdate(conn, String.format("ALTER SCHEMA %s RENAME TO %s;", oldSchemaName, newSchemaName));
                }
                if (isCommentHasEdit) {
                    if (isNameHasEdit) {
                        SqlExeUtil.executeUpdate(conn, String.format("COMMENT ON SCHEMA %s IS %s", newSchemaName, newComment));
                    } else {
                        SqlExeUtil.executeUpdate(conn, String.format("COMMENT ON SCHEMA %s IS %s", oldSchemaName, newComment));
                    }
                }
                break;
            case sqlserver:
                if (isCommentHasEdit) {
                    oldSchemaName = SqlStrUtil.dealDoubleQuote(type, oldSchemaName);
                    newComment = SqlStrUtil.dealDoubleQuote(type, newComment);
                    if (StringUtils.isBlank(oldComment)) {
                        SqlExeUtil.executeUpdate(conn, String.format("exec sp_addextendedproperty 'MS_Description', %s, 'SCHEMA', %s",
                                newComment, oldSchemaName));
                    } else {
                        SqlExeUtil.executeUpdate(conn, String.format(SQLSERVER_UPDATE_COMMENT, newComment, oldSchemaName));
                    }
                }
                break;
            case db2:
                if (isCommentHasEdit) {
                    oldSchemaName = SqlStrUtil.dealDoubleQuote(type, oldSchemaName);
                    newComment = SqlStrUtil.dealSingleQuote(type, newComment);
                    SqlExeUtil.executeUpdate(conn, String.format("COMMENT ON SCHEMA %s is %s", oldSchemaName, newComment));
                }
                break;
            default:
                break;
        }
    }

    private final static String SQLSERVER_GET_COMMENT = "select cast(value as varchar(500)) as comment\n" +
            "from fn_listextendedproperty('MS_Description', 'schema', %s, default, default, default, default)";

    private final static String SQLSERVER_UPDATE_COMMENT = "EXEC sp_updateextendedproperty\n" +
            "     @name = N'MS_Description'\n" +
            "    ,@value = %s\n" +
            "    ,@level0type = N'Schema', @level0name = %s";

}
