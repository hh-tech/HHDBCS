package com.hh.hhdb_admin.mgr.delete.impl;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.HHdbPgsqlPrefixEnum;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.create_dbobj.constraint.Constraint;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.dbobj2.hhdb.HHdbObjSqlUtil;
import com.hh.frame.dbobj2.version.VersionBean;
import com.hh.frame.dbobj2.version.VersionUtil;
import com.hh.hhdb_admin.mgr.delete.AbsDel;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: Jiang
 * @Date: 2021/9/15 14:27
 */
public class IndexDel extends AbsDel {

    @Override
    public void del(NodeInfo nodeInfo) throws Exception {
        String schemaName = nodeInfo.getSchemaName();
        String tableName = nodeInfo.getTableName();
        String name = nodeInfo.getName();
        List<Map<String, String>> primaryKeyLists = isPrimaryKey(schemaName, tableName, name);
        if (primaryKeyLists.size() > 0) {
            Constraint constraint = new Constraint(dbType, conn);
            constraint.delConst(TreeMrType.PRIMARY_KEY.name(), schemaName, tableName,
                    primaryKeyLists.get(0).get("constraint_name"));
        } else {
            String sql;
            switch (dbType) {
                case hhdb:
                case pgsql:
                case oracle:
                case db2:
                case dm:
                    sql = String.format("drop index \"%s\".\"%s\"", schemaName, name);
                    break;
                case mysql:
                    sql = String.format("alter table `%s` drop index %s", tableName, name);
                    break;
                case sqlserver:
                    sql = String.format("drop index \"%s\".\"%s\".\"%s\"", schemaName, tableName, name);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + dbType);
            }
            SqlExeUtil.executeUpdate(conn, sql);
        }
    }

    public List<Map<String, String>> isPrimaryKey(String schema, String tableName, String indexName) throws SQLException {
        String sql;
        switch (dbType) {
            case oracle:
                sql = "SELECT ac.constraint_name  as \"constraint_name\",\n" +
                        "       ac.constraint_type  AS \"type\",\n" +
                        "       ac.deferrable       AS \"deferrable\",\n" +
                        "       ac.search_condition AS \"condition\"\n" +
                        "FROM all_constraints ac\n" +
                        "WHERE ac.OWNER = '%s'\n" +
                        "  and ac.table_name = '%s'" +
                        "  and ac.constraint_type = 'P'" +
                        " and ac.constraint_name = '%s'";
                break;
            case dm:
                sql = "SELECT ac.constraint_name  as \"constraint_name\",\n" +
                        "       ac.constraint_type  AS \"type\",\n" +
                        "       ac.deferrable       AS \"deferrable\",\n" +
                        "       ac.search_condition AS \"condition\"\n" +
                        "FROM all_constraints ac\n" +
                        "WHERE ac.OWNER = '%s'\n" +
                        "  and ac.table_name = '%s'" +
                        "  and ac.constraint_type = 'P'" +
                        " and ac.index_name = '%s'";
                break;
            case mysql:
                sql = "SELECT CONSTRAINT_SCHEMA,CONSTRAINT_NAME ,TABLE_SCHEMA,TABLE_NAME,CONSTRAINT_TYPE " +
                        "FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS " +
                        "WHERE CONSTRAINT_SCHEMA='%s' " +
                        "AND TABLE_NAME='%s' " +
                        "AND CONSTRAINT_TYPE = 'PRIMARY KEY' " +
                        "AND CONSTRAINT_NAME = '%s'";
                break;
            case pgsql:
            case hhdb:
                HHdbPgsqlPrefixEnum prefix = DBTypeEnum.hhdb.equals(dbType) ? HHdbPgsqlPrefixEnum.hh : HHdbPgsqlPrefixEnum.pg;
                schema = HHdbObjSqlUtil.getSchemaId(conn, schema, prefix);
                sql = "select cons.conname as constraint_name " +
                        "from  " + prefix + "_constraint cons " +
                        "inner join " + prefix + "_class  c on cons.conrelid = c.oid " +
                        "where c.relnamespace='%s' " +
                        "and c.relname = '%s' " +
                        "and cons.contype='p' " +
                        "and cons.conname = '%s'";
                break;
            case sqlserver:
                VersionBean versionBean = VersionUtil.getDbVersion(conn);
                if (versionBean.bigVer < 8) {
                    sql = "select b.name constraint_name from sysobjects a,sysobjects b " +
                            "LEFT JOIN sys.schemas s ON s.schema_id=b.schema_id " +
                            "where a.ID=b.parent_object_id ";
                    if (StringUtils.isNotBlank(schema)) {
                        sql += "AND s.name='%s' ";
                    } else {
                        sql += "AND s.name='dbo' ";
                    }
                    sql += " and a.name='%s' " +
                            " and b.type = 'Pk' " +
                            " and b.name = '%s'";
                } else {
                    sql = "select b.name constraint_name\n" +
                            "  from sys.sysobjects a, sys.objects b\n" +
                            "  LEFT JOIN sys.schemas s ON s.schema_id = b.schema_id\n" +
                            "  where a.ID = b.parent_object_id\n" +
                            "  and b.name is not null\n" +
                            "  and s.name = '%s' " +
                            "  and a.name = '%s'\n" +
                            "  and b.type = 'Pk' " +
                            "  and b.name = '%s'";
                }
                break;
            case db2:
                sql = "select CONSTNAME constraint_name from SYSCAT.TABCONST " +
                        "where TABSCHEMA = '%s' " +
                        "and TABNAME = '%s' " +
                        "AND TYPE = 'PK' " +
                        "AND CONSTNAME = '%s'";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + dbType);
        }
        if (StringUtils.isEmpty(sql)) {
            return new ArrayList<>();
        } else {
            return SqlQueryUtil.selectStrMapList(conn, String.format(sql, schema, tableName, indexName));
        }
    }
}
