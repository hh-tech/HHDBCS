package com.hh.hhdb_admin.mgr.constraint.otherConst;

import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.hhdb_admin.mgr.constraint.SelectColumn;

import java.sql.Connection;

/**
 * @author YuSai
 */
public class UkTable extends AbsTable {

    @Override
    public HTable getTable(Connection conn, String schema, String tableName) {
        super.getTable(conn, schema, tableName);
        table.addCols(new DataCol("uniqueName", getLang("ukName")),
                new SelectColumn("uniqueColName", getLang("column"),
                        getLang("selectColumn"), conn, schema, tableName));
        return table;
    }
}
