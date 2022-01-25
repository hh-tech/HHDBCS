package com.hh.hhdb_admin.mgr.constraint.otherConst;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.hhdb_admin.mgr.constraint.ConstraintComp;

import java.sql.Connection;

/**
 * @author YuSai
 */
public abstract class AbsTable {

    public HTable table = new HTable();

    public static AbsTable getOtherTable(TreeMrType treeMrType) {
        switch (treeMrType) {
            case CHECK_KEY_GROUP:
                return new CkTable();
            case UNIQUE_KEY_GROUP:
                return new UkTable();
            case PRIMARY_KEY_GROUP:
                return new PkTable();
            default:
                return null;
        }
    }

    public HTable getTable(Connection conn, String schema, String tableName) {
        table.setNullSymbol("");
        DataCol idCol = new DataCol("id", "id");
        idCol.setShow(false);
        table.addCols(idCol);
        table.hideSeqCol();
        table.setEvenBgColor(table.getOddBgColor());
        return table;
    }

    protected String getLang(String name){
        return ConstraintComp.getLang(name);
    }

}
