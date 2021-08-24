package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;

import javax.swing.*;

/**
 * @author: Jiang
 * @date: 2020/10/9
 */

public class RefreshDataHandler extends AbsHandler {

    public static final String ORA_REFRESH = "BEGIN DBMS_MVIEW.REFRESH('\"%s\"'); END;";
    public static final String HH_PG_REFRESH = "REFRESH  Materialized view \"%s\".\"%s\"  WITH DATA";
    public static final String DM_REFRESH = "refresh  materialized view \"%s\".\"%s\" ";

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        int res = JOptionPane.showConfirmDialog(null, "是否更新", "提示", JOptionPane.YES_NO_OPTION);
        if (res != JOptionPane.YES_OPTION) {
            return;
        }
        String name = treeNode.getName();
        switch (DriverUtil.getDbType(loginBean.getConn())) {
            case oracle:
                SqlExeUtil.executeUpdate(loginBean.getConn(), String.format(ORA_REFRESH, name));
                break;
            case hhdb:
            case pgsql:
                String schemaName = getSchemaName();
                SqlExeUtil.executeUpdate(loginBean.getConn(), String.format(HH_PG_REFRESH, schemaName, name));
                break;
            case dm:
            	String schemaName1 = getSchemaName();
                SqlExeUtil.executeUpdate(loginBean.getConn(), String.format(DM_REFRESH, schemaName1, name));
                break;
            default:
                return;
        }
        PopPaneUtil.info(StartUtil.parentFrame.getWindow(), "更新成功");
    }
}
