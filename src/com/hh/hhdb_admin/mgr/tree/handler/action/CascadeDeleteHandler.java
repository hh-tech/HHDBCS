package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.mgr.tree.TreeUtil;
import com.hh.hhdb_admin.mgr.tree.handler.RightMenuActionHandler;

/**
 * @author: Jiang
 * @date: 2020/9/15
 */
public class CascadeDeleteHandler extends AbsHandler {

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        DeleteHandler deleteHandler = (DeleteHandler) RightMenuActionHandler.getInstance("Delete");
        if (deleteHandler == null) {
            return;
        }
        deleteHandler.setSchemaName(TreeUtil.getSchemaName(treeNode, DriverUtil.getDbType(loginBean.getConn())));
        deleteHandler.setLoginBean(loginBean);
        deleteHandler.cascadeResolve(treeNode);
    }

    public void resolveMulti(HTreeNode treeNode) throws Exception {
        DeleteHandler deleteHandler = (DeleteHandler) RightMenuActionHandler.getInstance("Delete");
        if (deleteHandler == null) {
            return;
        }
        deleteHandler.setSchemaName(TreeUtil.getSchemaName(treeNode, DriverUtil.getDbType(loginBean.getConn())));
        deleteHandler.setLoginBean(loginBean);
        deleteHandler.cascadeMultiResolve(treeNode);
    }
}
