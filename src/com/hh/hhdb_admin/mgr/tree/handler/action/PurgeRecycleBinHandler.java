package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.tree.TreeComp;

import javax.swing.*;

/**
 * 处理清除回收站事件
 *
 * @author Yusai
 */

public class PurgeRecycleBinHandler extends AbsHandler {

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        int res = JOptionPane.showConfirmDialog(null, TreeComp.getLang("sure_purge"), getLang("hint"), JOptionPane.YES_NO_OPTION);
        if (res != 0) {
            return;
        }
        SqlExeUtil.executeUpdate(loginBean.getConn(), "PURGE RECYCLEBIN");
        PopPaneUtil.info(StartUtil.parentFrame.getWindow(), getLang("purgeSuccess"));
        refreshWithNode(treeNode.getParentHTreeNode());
    }

}
