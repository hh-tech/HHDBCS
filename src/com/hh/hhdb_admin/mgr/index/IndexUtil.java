package com.hh.hhdb_admin.mgr.index;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.HHdbPgsqlPrefixEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.index.IndexEnum;
import com.hh.frame.dbobj2.db2.tab.Db2Table;
import com.hh.frame.dbobj2.dm.tab.DmTable;
import com.hh.frame.dbobj2.hhdb.tab.HHdbTable;
import com.hh.frame.dbobj2.mysql.tab.MysqlTable;
import com.hh.frame.dbobj2.ora.tab.OraTable;
import com.hh.frame.dbobj2.sqlserver.tab.SqlTable;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.RowStatus;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * @author YuSai
 */
class IndexUtil {

    static JsonObject getIndexType(DBTypeEnum dbTypeEnum) {
        JsonObject json = new JsonObject();
        switch (dbTypeEnum) {
            case hhdb:
            case pgsql:
            case mysql:
                json.add(IndexEnum.BTREE_NORMAL.getIndexName(), IndexEnum.BTREE_NORMAL.name());
                json.add(IndexEnum.BTREE_UNIQUE.getIndexName(), IndexEnum.BTREE_UNIQUE.name());
                json.add(IndexEnum.HASH.getIndexName(), IndexEnum.HASH.name());
                break;
            case oracle:
                json.add(IndexEnum.BTREE_NORMAL.getIndexName(), IndexEnum.BTREE_NORMAL.name());
                json.add(IndexEnum.BTREE_UNIQUE.getIndexName(), IndexEnum.BTREE_UNIQUE.name());
                json.add(IndexEnum.REVERSE.getIndexName(), IndexEnum.REVERSE.name());
                break;
            case sqlserver:
                json.add(IndexEnum.CLUSTERED.getIndexName(), IndexEnum.CLUSTERED.name());
                json.add(IndexEnum.UNIQUE_CLUSTERED.getIndexName(), IndexEnum.UNIQUE_CLUSTERED.name());
                json.add(IndexEnum.NONCLUSTERED.getIndexName(), IndexEnum.NONCLUSTERED.name());
                json.add(IndexEnum.UNIQUE_NONCLUSTERED.getIndexName(), IndexEnum.UNIQUE_NONCLUSTERED.name());
                break;
            case db2:
                json.add(IndexEnum.NORMAL_INDEX.getIndexName(), IndexEnum.NORMAL_INDEX.name());
                json.add(IndexEnum.UNIQUE_INDEX.getIndexName(), IndexEnum.UNIQUE_INDEX.name());
                break;
            case dm:
                json.add(IndexEnum.BTREE_NORMAL.getIndexName(), IndexEnum.BTREE_NORMAL.name());
                json.add(IndexEnum.UNIQUE_INDEX.getIndexName(), IndexEnum.UNIQUE_INDEX.name());
                break;

            default:
        }
        return json;
    }

    static List<Map<String, String>> getColumns(Connection conn, String schema, String tableName) {
        try {
            DBTypeEnum dbTypeEnum = DriverUtil.getDbType(conn);
            switch (dbTypeEnum) {
                case hhdb:
                case pgsql:
                    String prefix = dbTypeEnum.name().substring(0, 2);
                    HHdbTable table = new HHdbTable(conn, schema, tableName, HHdbPgsqlPrefixEnum.valueOf(prefix));
                    return table.getAllColumn();
                case oracle:
                    return new OraTable(conn, schema, tableName).getAllColumn();
                case mysql:
                    return new MysqlTable(conn, schema, tableName).getAllColumn();
                case sqlserver:
                    LinkedHashSet<String> columns = new SqlTable(conn, schema, tableName).getColumnNameList(schema);
                    List<Map<String, String>> maps = new ArrayList<>();
                    for (String column: columns) {
                        Map<String, String> map = new HashMap<>();
                        map.put("column_name", column);
                        maps.add(map);
                    }
                    return maps;
                case db2:
                columns = new Db2Table(conn, schema, tableName).getColumnNameList(schema);
                maps = new ArrayList<>();
                for (String column: columns) {
                    Map<String, String> map = new HashMap<>();
                    map.put("column_name", column);
                    maps.add(map);
                }
                return maps;
                case dm:
                    return new DmTable(conn, schema, tableName).getAllColumn();
                default:
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getColumnsName(HTable table, DBTypeEnum dbTypeEnum) {
        String mark = "\"";
        if (DBTypeEnum.mysql.equals(dbTypeEnum)) {
            mark = "`";
        }
        List<HTabRowBean> tabRowBeans = table.getRowBeans(RowStatus.UPDATE);
        StringBuilder colNames = new StringBuilder();
        for (HTabRowBean tabRowBean : tabRowBeans) {
            Map<String, String> oldRow = tabRowBean.getOldRow();
            Map<String, String> currRow = tabRowBean.getCurrRow();
            if (Boolean.parseBoolean(currRow.get("selected"))) {
                String column = oldRow.get("column_name");
                if (StringUtils.isEmpty(colNames)) {
                    colNames.append(mark).append(column).append(mark);
                } else {
                    colNames.append(",").append(mark).append(column).append(mark);
                }
            }
        }
        return colNames.toString();
    }

}
