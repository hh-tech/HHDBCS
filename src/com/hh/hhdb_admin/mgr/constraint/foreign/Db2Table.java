package com.hh.hhdb_admin.mgr.constraint.foreign;

import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.ListCol;

import java.sql.Connection;
import java.util.Arrays;

/**
 * @author YuSai
 */
public class Db2Table extends AbsForeTable {

    @Override
    public HTable getTable(Connection conn, String schema, String tableName) {
        super.getTable(conn, schema, tableName);
        table.addCols(new ListCol("foreignOnUpdate", getLang("onUpdating"), Arrays.asList("", "NO ACTION", "RESTRICT")),
                new ListCol("foreignOnDelete", getLang("onDeletion"), Arrays.asList("", "NO ACTION", "CASCADE", "SET NULL")));
        return table;
    }

}
