package com.hh.hhdb_admin.mgr.constraint;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.create_dbobj.constraint.ConstModel;
import com.hh.frame.create_dbobj.constraint.Constraint;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonArray;
import com.hh.frame.json.JsonObject;
import com.hh.frame.json.JsonValue;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.RowStatus;
import com.hh.frame.swingui.view.tab.col.abs.AbsCol;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.mgr.table.column.ForeignTableColumn;
import com.hh.hhdb_admin.mgr.table.comp.SqlViewDialog;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author YuSai
 */
public class ConstraintUtil {

    private static final String LOG_NAME = ConstraintUtil.class.getSimpleName();

    private static Constraint constraint;

    static void initConst(Connection conn, DBTypeEnum dbTypeEnum) {
        constraint = new Constraint(dbTypeEnum, conn);
    }

    public static void save(TreeMrType treeMrType, String schema, String tableName, HTable table, boolean bool) throws SQLException {
        if (bool) {
            constraint.saveConstraint(getFores(schema, tableName, table));
        } else {
            constraint.saveConstraint(getOthers(treeMrType, schema, tableName, table));
        }
    }

    public static void delete(String constType, String schema, String table, String constName) throws Exception {
        constraint.delConst(constType, schema, table, constName);
    }

    static void previewSql(TreeMrType treeMrType, String schema, String tableName, HTable table, boolean bool) {
        SqlViewDialog dialog = new SqlViewDialog();
        if (bool) {
            dialog.setSql(constraint.getSql(getFores(schema, tableName, table)));
        } else {
            dialog.setSql(constraint.getSql(getOthers(treeMrType, schema, tableName, table)));
        }
        dialog.show();
    }

    private static List<ConstModel> getFores(String schema, String tableName, HTable table) {
        JsonArray jsonArray = getRowColJson(table);
        List<ConstModel> constraints = new ArrayList<>();
        IntStream.range(0, jsonArray.size()).forEach(i -> {
            JsonObject row = jsonArray.get(i).asObject();
            String constName = row.getString("foreignName");
            ConstModel constraint = new ConstModel();
            constraint.setSchema(schema);
            constraint.setTableName(tableName);
            constraint.setConstName(constName);
            constraint.setConstType(" FOREIGN KEY ");
            constraint.setColumns(getColNames(row, "colName").toString());
            String foreignTableName = row.getString(ForeignTableColumn.FOREIGN_TABLE_NAME);
            constraint.setForeTableName(foreignTableName);
            constraint.setForeTableColumns(getColNames(row, "foreignTableColName").toString());
            constraint.setOnUpdate(row.getString("foreignOnUpdate"));
            constraint.setOnDelete(row.getString("foreignOnDelete"));
            constraint.setLinkBreak(i < jsonArray.size() - 1 ? "\n\n" : "");
            constraints.add(constraint);
        });
        return constraints;
    }

    private static JsonArray getRowColJson(HTable table) {
        JsonArray array = new JsonArray();
        List<HTabRowBean> rowBeans = table.getRowBeans(RowStatus.ADD);
        for (HTabRowBean rowBean : rowBeans) {
            JsonObject columnJson = new JsonObject();
            Map<String, String> map = rowBean.getCurrRow();
            for (AbsCol absCol : table.getColumns()) {
                String columnName = absCol.getName();
                String value = map.get(columnName);
                if (StringUtils.isNotEmpty(value)) {
                    switch (columnName) {
                        case "colName":
                        case "foreignTableColName":
                            JsonValue jsonValue = Json.parse(value).asObject().get(SelectColumn.COL_NAMES);
                            columnJson.add(columnName, jsonValue != null ? jsonValue.asArray() : new JsonArray());
                            break;
                        case "foreignTableName":
                            JsonObject object = Json.parse(value).asObject();
                            jsonValue = object.get(ForeignTableColumn.FOREIGN_SCHEMA_NAME);
                            columnJson.add(ForeignTableColumn.FOREIGN_SCHEMA_NAME, jsonValue.toString() == null ? "" : jsonValue.toString());
                            jsonValue = object.get(ForeignTableColumn.FOREIGN_TABLE_NAME);
                            columnJson.add(ForeignTableColumn.FOREIGN_TABLE_NAME, jsonValue.toString() == null ? "" : jsonValue.toString());
                            break;
                        default:
                            columnJson.add(columnName, value.trim());
                    }
                }
            }
            array.add(columnJson);
        }
        return array;
    }

    private static StringBuilder getColNames(JsonObject row, String name) {
        StringBuilder sql = new StringBuilder();
        JsonValue names = row.get(name);
        if (names != null) {
            try {
                JsonArray colName = names.asArray();
                if (colName != null && colName.size() > 0) {
                    sql.append("(");
                    for (int j = 0; j < colName.size(); j++) {
                        sql.append("\"").append(colName.get(j).asString()).append("\"").append(j < colName.size() - 1 ? "," : "");
                    }
                    sql.append(")");
                }
            } catch (Exception e) {
                e.printStackTrace();
                logUtil.error(LOG_NAME, e);
            }
        }
        return sql;
    }

    private static List<ConstModel> getOthers(TreeMrType type, String schema, String tableName, HTable table) {
        List<ConstModel> constraints = new ArrayList<>();
        int rowCount = table.getRowCount();
        List<HTabRowBean> rowBeans = table.getRowBeans(RowStatus.ADD);
        for (int i = 0; i < rowBeans.size(); i++) {
            Map<String, String> map = rowBeans.get(i).getCurrRow();
            ConstModel constraint = new ConstModel();
            constraint.setSchema(schema);
            constraint.setTableName(tableName);
            if (TreeMrType.CHECK_KEY_GROUP.equals(type)) {
                constraint.setConstName(map.get("checkName"));
                constraint.setConstType(" CHECK (");
                constraint.setColumns(map.get("checkText") + ")");
            } else {
                switch (type) {
                    case UNIQUE_KEY_GROUP:
                        constraint.setConstName(map.get("uniqueName"));
                        constraint.setConstType(" UNIQUE (");
                        constraint.setColumns(getColumns(map.get("uniqueColName")));
                        break;
                    case PRIMARY_KEY_GROUP:
                        constraint.setConstName(map.get("pkName"));
                        constraint.setConstType(" PRIMARY KEY (");
                        constraint.setColumns(getColumns(map.get("pkColName")));
                        break;
                    default:
                }
            }
            constraint.setLinkBreak(i < rowCount - 1 ? "\n\n" : "");
            constraints.add(constraint);
        }
        return constraints;
    }

    private static String getColumns(String value) {
        StringBuilder sql = new StringBuilder();
        if (StringUtils.isNotEmpty(value)) {
            JsonValue jsonValue = Json.parse(value).asObject().get(SelectColumn.COL_NAMES);
            if (jsonValue == null) {
                return "";
            }
            JsonArray array = jsonValue.asArray();
            for (int j = 0; j < array.size(); j++) {
                sql.append("\"").append(array.get(j).asString()).append("\"").append(j < array.size() - 1 ? "," : "");
            }
            sql.append(")");
        }
        return sql.toString();
    }

}
