package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.tree.TreeComp;

import javax.swing.*;

/**
 * 处理清除对象事件
 *
 * @author Yusai
 */

public class PurgeObjectHandler extends AbsHandler {

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        int res = JOptionPane.showConfirmDialog(null, TreeComp.getLang("sure_purge"), getLang("hint"), JOptionPane.YES_NO_OPTION);
        if (res != 0) {
            return;
        }
        switch (TreeMrType.valueOf(treeNode.getType())) {
            case RECYCLE_BIN_TABLE:
                SqlExeUtil.executeUpdate(loginBean.getConn(), String.format("PURGE TABLE %s", treeNode.getName()));
                break;
            case RECYCLE_BIN_INDEX:
                SqlExeUtil.executeUpdate(loginBean.getConn(), String.format("PURGE INDEX %s", treeNode.getName()));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + TreeMrType.valueOf(treeNode.getType()));
        }
        PopPaneUtil.info(StartUtil.parentFrame.getWindow(), getLang("purgeSuccess"));
        refreshWithNode(treeNode.getParentHTreeNode());
    }

}
