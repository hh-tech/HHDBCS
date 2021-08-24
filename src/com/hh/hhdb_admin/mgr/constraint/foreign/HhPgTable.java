package com.hh.hhdb_admin.mgr.constraint.foreign;

import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.ListCol;

import java.sql.Connection;
import java.util.Arrays;

/**
 * @author YuSai
 */
public class HhPgTable extends AbsForeTable {

    @Override
    public HTable getTable(Connection conn, String schema, String tableName) {
        String[] comboBox = new String[]{"", "NO ACTION", "CASCADE", "RESTRICT", "SET NULL", "SET DEFAULT"};
        super.getTable(conn, schema, tableName);
        table.addCols(new ListCol("foreignOnUpdate", getLang("onUpdating"), Arrays.asList(comboBox)),
                new ListCol("foreignOnDelete", getLang("onDeletion"), Arrays.asList(comboBox)));
        return table;
    }

}
