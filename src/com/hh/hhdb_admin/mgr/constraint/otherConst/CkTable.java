package com.hh.hhdb_admin.mgr.constraint.otherConst;

import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;

import java.sql.Connection;

/**
 * @author YuSai
 */
public class CkTable extends AbsTable {

    @Override
    public HTable getTable(Connection conn, String schema, String tableName) {
        super.getTable(conn, schema, tableName);
        table.addCols(new DataCol("checkName", getLang("ckName")),
                new DataCol("checkText", getLang("ckText")));
        return table;
    }
}
