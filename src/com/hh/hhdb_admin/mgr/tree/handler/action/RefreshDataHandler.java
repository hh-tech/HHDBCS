package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.treeMr.TreeMrUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;

/**
 * @author: Jiang
 * @date: 2020/10/9
 */

public class RefreshDataHandler extends AbsHandler {

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        if (PopPaneUtil.confirm(StartUtil.parentFrame.getWindow(), getLang("update"))) {
            String name = treeNode.getName();
            TreeMrUtil.refreshData(loginBean.getConn(), getSchemaName(), name);
            PopPaneUtil.info(StartUtil.parentFrame.getWindow(), getLang("updateSucceeded"));
        }
    }
}
