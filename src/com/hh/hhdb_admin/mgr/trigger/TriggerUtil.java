package com.hh.hhdb_admin.mgr.trigger;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.HHdbPgsqlPrefixEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.dbobj2.db2.Db2Database;
import com.hh.frame.dbobj2.dm.DmUser;
import com.hh.frame.dbobj2.hhdb.HHdbDatabase;
import com.hh.frame.dbobj2.hhdb.HHdbSchema;
import com.hh.frame.dbobj2.hhdb.HHdbSessionEnum;
import com.hh.frame.dbobj2.hhdb.tab.HHdbTable;
import com.hh.frame.dbobj2.mysql.MysqlDatabase;
import com.hh.frame.dbobj2.mysql.tab.MysqlTable;
import com.hh.frame.dbobj2.ora.OraUser;
import com.hh.frame.dbobj2.ora.tab.OraTable;
import com.hh.frame.dbobj2.sqlserver.SqlServerDatabase;
import com.hh.frame.dbobj2.sqlserver.SqlServerTable;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;
import com.hh.hhdb_admin.mgr.trigger.comp.form.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

public class TriggerUtil {

    public static void setDialogSize(DBTypeEnum typeEnum, HDialog dialog, int height) {
        switch (typeEnum) {
            case sqlserver:
            case oracle:
            case dm:
            case db2:
            case mysql:
                dialog.setSize(620, height + 80);
                break;
            default:
                dialog.setSize(620, height + 300);
        }
    }

    public static void delTrigger(Connection conn, String triggerName, String tableName, String schemaName) throws Exception {
        DBTypeEnum typeEnum = DriverUtil.getDbType(conn);
        String fTriggerName = DbCmdStrUtil.toDbCmdStr(triggerName, typeEnum);
        String fSchemaName = DbCmdStrUtil.toDbCmdStr(schemaName, typeEnum);
        switch (typeEnum) {
            case pgsql:
            case hhdb:
                SqlExeUtil.executeUpdate(conn, String.format("DROP TRIGGER %s ON %s.%s",
                        fTriggerName,
                        fSchemaName,
                        DbCmdStrUtil.toDbCmdStr(tableName, typeEnum)));
                break;
            case sqlserver:
            case oracle:
            case db2:
            case dm:
                SqlExeUtil.executeUpdate(conn, String.format("DROP TRIGGER %s.%s", fSchemaName, fTriggerName));
                break;
            case mysql:
                SqlExeUtil.executeUpdate(conn, String.format("DROP TRIGGER %s.%s",
                        fSchemaName,
                        DbCmdStrUtil.toDbCmdStr(fTriggerName, typeEnum)));
                break;
            default:
        }
    }

    public static String getTriggerSqlForUpdate(Connection conn, String triggerName, String schemaName) throws Exception {
        DBTypeEnum typeEnum = DriverUtil.getDbType(conn);
        StringBuilder buffer = new StringBuilder();
        switch (typeEnum) {
            case sqlserver:
                List<Map<String, String>> maps = SqlQueryUtil.selectStrMapList(conn, String.format("sp_helptext '%s.%s'", schemaName, triggerName));
                for (Map<String, String> map : maps) {
                    buffer.append(map.get("text"));
                }
                Pattern p = Pattern.compile("(?ms)('(?:''|[^'])*')|--.*?$|/\\*.*?\\*/");
                String presult = p.matcher(buffer.toString()).replaceAll("$1");
                return presult.replaceFirst("(?i)CREATE", "ALTER");
            case oracle:
                List<Map<String, String>> dbsMap = SqlQueryUtil.selectStrMapList(conn, String.format(
                        "select text from all_source where type='TRIGGER' and name = '%s' and owner='%s'",
                        SqlStrUtil.toWhereStr(typeEnum, triggerName), SqlStrUtil.toWhereStr(typeEnum, schemaName)));
                if (!dbsMap.isEmpty()) {
                    buffer.append("CREATE OR REPLACE ");
                }
                for (Map<String, String> map : dbsMap) {
                    String sql = map.get("text");
                    buffer.append(sql);
                }
                break;
            case dm:
                Map<String, String> dmMap = SqlQueryUtil.selectOneStrMap(conn, String.format(
                        "select text from all_source where type='TRIG' and name = %s and owner=%s",
                        SqlStrUtil.dealSingleQuote(typeEnum, triggerName), SqlStrUtil.dealSingleQuote(typeEnum, schemaName)));
                if (!dmMap.isEmpty()) {
                    String sql = dmMap.get("text");
                    sql = sql.substring(sql.toLowerCase().indexOf("trigger"));
                    buffer.append("CREATE OR REPLACE ").append(sql);
                }
                break;
//            case db2:
//                buffer.append(String.format("DROP TRIGGER \"%s\".\"%s\";", schemaName, triggerName));
//                List<Map<String, String>> dbsMap = SqlQueryUtil.selectStrMapList(conn, "select text from syscat.triggers WHERE TRIGSCHEMA = '%s' AND TRIGNAME ='%s'", schemaName, triggerName);
//                for(Map<String,String> map:dbsMap){
//                    String sql = map.get("text");
//                    if(!map.get("text").trim().endsWith(";")){
//                        sql = map.get("text") + ";";
//                    }
//                    buffer.append(sql);
//                }
            default:
        }
        return buffer.toString();
    }

    public static TriggerForm getTriggerComp(HDialog dialog, Connection conn, JdbcBean jdbc, String schemaName, String tableName) throws Exception {
        DBTypeEnum typeEnum = DriverUtil.getDbType(conn);
        switch (typeEnum) {
            case pgsql:
            case hhdb:
                return new HhPgForm(dialog, conn, jdbc, schemaName, tableName);
            case mysql:
                return new MysqlForm(dialog, conn, jdbc, schemaName, tableName);
            case oracle:
            case dm:
                return new OraDmFrom(dialog, conn, jdbc, schemaName, tableName);
            case sqlserver:
                return new SqlserverForm(dialog, conn, jdbc, schemaName, tableName);
            case db2:
                return new Db2Form(dialog, conn, jdbc, schemaName, tableName);
            default:
                throw new IllegalStateException("Unexpected value: " + typeEnum);
        }
    }

    public static boolean isSqlEditModal(Connection conn) throws Exception {
        DBTypeEnum typeEnum = DriverUtil.getDbType(conn);
        switch (typeEnum) {
            case pgsql:
            case hhdb:
            case mysql:
                return false;
            default:
                return true;
        }
    }

    public static List<String> getColumns(Connection conn, String schemaName, String tabName) throws SQLException {
        DBTypeEnum typeEnum = DriverUtil.getDbType(conn);
        List<String> list = new ArrayList<>();
        switch (typeEnum) {
            case pgsql:
            case hhdb:
                String prefix = typeEnum.name().substring(0, 2);
                new HHdbTable(conn, schemaName, tabName, HHdbPgsqlPrefixEnum.valueOf(prefix)).getAllColumn().stream().map(
                        item -> item.get("column_name")).forEach(list::add);
                return list;
            case oracle:
                new OraTable(conn, schemaName, tabName).getAllColumn().stream().map(
                        item -> item.get("column_name")).forEach(list::add);
                return list;
            case mysql:
                new MysqlTable(conn, schemaName, tabName).getAllColumn().stream().map(
                        item -> item.get("column_name")).forEach(list::add);
                return list;
            case sqlserver:
                new SqlServerTable(conn, schemaName, tabName).getAllColumn().stream().map(
                        item -> item.get("column_name")).forEach(list::add);
                return list;
            default:
                throw new IllegalStateException("Unexpected value: " + typeEnum);
        }
    }

    public static List<String> getAllSchema(Connection conn, String dbName) {
        List<String> list = new ArrayList<>();
        try {
            DBTypeEnum typeEnum = DriverUtil.getDbType(conn);
            switch (typeEnum) {
                case pgsql:
                case hhdb:
                    String prefix = typeEnum.name().substring(0, 2);
                    new HHdbDatabase(conn, dbName, HHdbPgsqlPrefixEnum.valueOf(prefix)).getAllSchema().stream()
                            .map(item -> item.get("name"))
                            .forEach(list::add);
                    return list;
                case oracle:
                    new OraUser(conn).getAllUser().stream()
                            .map(item -> item.get("user_name"))
                            .forEach(list::add);
                    return list;
                case mysql:
                    new MysqlDatabase(conn).getAllSchema().stream()
                            .map(item -> item.get("database_name"))
                            .forEach(list::add);
                    return list;
                case sqlserver:
                    new SqlServerDatabase(conn).getAllSchema().stream()
                            .map(item -> item.get("schema_name"))
                            .forEach(list::add);
                    return list;
                case db2:
                    new Db2Database(conn).getAllSchema().stream()
                            .map(item -> item.get("schema_name").trim())
                            .forEach(list::add);
                    return list;
                case dm:
                    new DmUser(conn).getAllSchema().stream()
                            .map(item -> item.get("name").trim())
                            .forEach(list::add);
                    return list;
                default:
                    throw new IllegalStateException("Unexpected value: " + typeEnum);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
    }

    public static List<String> getFunctions(Connection conn, String schemaName, HHdbPgsqlPrefixEnum prefixEnum) {
        Set<String> set = new HashSet<>();
        try {
            new HHdbSchema(conn, schemaName, prefixEnum).getChildList(HHdbSessionEnum.function).stream()
                    .map(item -> {
                        boolean isTriggerFunc = "trigger".equalsIgnoreCase(String.valueOf(item.get("typname")));
                        return isTriggerFunc ? item.get("function_name") : "";
                    }).forEach(set::add);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new ArrayList<>(set);
    }

    public static String getDb(Connection conn) {
        try {
            DBTypeEnum typeEnum = DriverUtil.getDbType(conn);
            List<Map<String, String>> maps;
            switch (typeEnum) {
                case pgsql:
                case hhdb:
                    maps = SqlQueryUtil.selectStrMapList(conn, "select current_database() as db");
                    break;
                case oracle:
                    maps = SqlQueryUtil.selectStrMapList(conn, "select name  as db from V$database");
                    break;
                case mysql:
                    maps = SqlQueryUtil.selectStrMapList(conn, "select database() as db");
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + typeEnum);
            }
            if (maps != null && maps.size() > 0) {
                return maps.get(0).get("db");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
