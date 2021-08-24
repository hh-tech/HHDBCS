package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;

import javax.swing.*;

/**
 * @author: Jiang
 * @date: 2020/9/15
 * <p>
 * 处理清空事件
 */

public class TruncateHandler extends AbsHandler {

    private static final String TRUNCATE_TABLE = "truncate table \"%s\".\"%s\"";

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        int res = JOptionPane.showConfirmDialog(null, "是否清空表数据", "警告", JOptionPane.YES_NO_OPTION);
        if (res != JOptionPane.YES_OPTION) {
            return;
        }
        String tableName = treeNode.getName();
        DBTypeEnum dbTypeEnum = DriverUtil.getDbType(loginBean.getConn());
        String schemaName = getSchemaName();
        if (dbTypeEnum == DBTypeEnum.db2) {
            SqlExeUtil.executeUpdate(loginBean.getConn(), String.format(TRUNCATE_TABLE + " IMMEDIATE", schemaName, tableName));
        } else {
            SqlExeUtil.executeUpdate(loginBean.getConn(), String.format(TRUNCATE_TABLE, schemaName, tableName));
        }
        PopPaneUtil.info(StartUtil.parentFrame.getWindow(), "清空表数据成功");
    }

}
