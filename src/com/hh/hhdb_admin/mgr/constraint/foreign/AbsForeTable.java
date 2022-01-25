package com.hh.hhdb_admin.mgr.constraint.foreign;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.hhdb_admin.mgr.constraint.ConstraintComp;
import com.hh.hhdb_admin.mgr.constraint.SelectColumn;
import com.hh.hhdb_admin.mgr.table.TableComp;
import com.hh.hhdb_admin.mgr.table.column.ForeignKeyColumn;
import com.hh.hhdb_admin.mgr.table.column.ForeignTableColumn;

import java.sql.Connection;

/**
 * @author YuSai
 */
public class AbsForeTable {

    private static DBTypeEnum dbTypeEnum;

    public HTable table = new HTable();

    public static AbsForeTable getForeTable(DBTypeEnum dbTypeEnum) {
        AbsForeTable.dbTypeEnum = dbTypeEnum;
        switch (dbTypeEnum) {
            case hhdb:
            case pgsql:
                return new HhPgTable();
            case oracle:
            case dm:
                return new OracleTable();
            case mysql:
                return new MysqlTable();
            case sqlserver:
                return new SQLServerTable();
            case db2:
                return new Db2Table();
            default:
                return null;
        }
    }

    public HTable getTable(Connection conn, String schema, String tableName) {
        table.setNullSymbol("");
        TableComp panelCreate = new TableComp(conn, dbTypeEnum);
        panelCreate.genTableData();
        DataCol idCol = new DataCol("id", "id");
        idCol.setShow(false);
        String column = getLang("column");
        String selectColumn = getLang("selectColumn");
        String foreTableName = getLang("foreignKeyTableName");
        String tableColumn = getLang("foreignKeyTableColumn");
        ForeignKeyColumn foreignKeyColumn = new ForeignKeyColumn("foreignTableColName", tableColumn, table, selectColumn, null, conn);
        foreignKeyColumn.setIndex(2);
        table.addCols(idCol, new DataCol("foreignName", getLang("fkName")),
                new SelectColumn("colName", column, selectColumn, conn, schema, tableName),
                new ForeignTableColumn("foreignTableName", foreTableName, table, conn), foreignKeyColumn);
        table.hideSeqCol();
        table.setEvenBgColor(table.getOddBgColor());
        return table;
    }

    protected String getLang(String name){
        return ConstraintComp.getLang(name);
    }

}
