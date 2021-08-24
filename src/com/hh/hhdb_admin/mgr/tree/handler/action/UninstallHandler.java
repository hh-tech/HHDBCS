package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;


/**
 * @author: Jiang
 * @date: 2020/9/17
 * <p>
 * 处理卸载事件
 */

public class UninstallHandler extends AbsHandler {

    public static final String UNINSTALL_EXTENSION = "drop extension %s cascade";

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        String extensionName = treeNode.getName();
        SqlExeUtil.executeUpdate(loginBean.getConn(), String.format(UNINSTALL_EXTENSION, extensionName));
        PopPaneUtil.info(StartUtil.parentFrame.getWindow(), "卸载成功");
        refreshWithNode(treeNode.getParentHTreeNode());
    }
}
