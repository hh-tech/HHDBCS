package com.hh.hhdb_admin.mgr.tree.handler;

import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.create_dbobj.treeMr.mr.AbsTreeMr;
import com.hh.frame.swingui.view.tree.HTree;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.tree.TreeUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * @author: Jiang
 * @date: 2020/9/10
 */

public class LeftDoubleClickHandler {

    private final HTree tree;
    private final LoginBean loginBean;

    public LeftDoubleClickHandler(HTree tree, LoginBean loginBean) {
        this.tree = tree;
        this.loginBean = loginBean;
    }

    /**
     * 刷新指定节点
     *
     * @param treeNode 节点
     */
    public void refreshNode(HTreeNode treeNode) {
        treeNode.removeAllChildren();
        try {
            onClick(treeNode);
        } catch (Exception e) {
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e.getMessage());
        }
    }

    /**
     * 加载指定节点的所有子节点
     *
     * @param treeNode 节点
     * @throws Exception e
     */
    public void onClick(HTreeNode treeNode) throws Exception {
        if (treeNode.getChildNode().size() > 0) {
            return;
        }
        if (treeNode.getType().equals("MORE")) {
            loadMore(treeNode.getParentHTreeNode());
            tree.openAllByNode(treeNode.getParentHTreeNode());
        } else {
            AbsTreeMr.genTreeMr(loginBean.getJdbc()).ifPresent(treeMr -> DriverUtil.getDbTypeOp(loginBean.getJdbc()).ifPresent(typeEnum -> {
                treeNode.removeAllChildren();
                TreeMrNode treeMrNode = new TreeMrNode(treeNode.getName(), TreeMrType.valueOf(treeNode.getType()), null);
                treeMrNode.setTableName(TreeUtil.getTableName(treeNode));
                treeMrNode.setSchemaName(TreeUtil.getSchemaName(treeNode, typeEnum));
                treeMrNode.setPackageName(TreeUtil.getPackageName(treeNode));
                String filterData = loginBean.getFilterData().get(treeMrNode.getType().name());
                List<TreeMrNode> allList = treeMr.getChildNode(treeMrNode, loginBean.getConn(), filterData);
                List<HTreeNode> childList = new ArrayList<>();
                for (TreeMrNode t : allList) {
                    HTreeNode newNode = new HTreeNode();
                    newNode.setId(t.getId());
                    newNode.setName(t.getName());
                    newNode.setType(t.getType().name());
                    newNode.setOpenIcon(IconFileUtil.getIcon(new IconBean(TreeUtil.ICON_CONTEXT, t.getIcon())));
                    childList.add(newNode);
                }
                for (HTreeNode hTreeNode : childList) {
                    treeNode.add(hTreeNode);
                }
                tree.openAllByNode(treeNode);
            }));
        }

    }

    private void loadMore(HTreeNode treeNode) {
        HTreeNode lastNode = treeNode.getChildNode(treeNode.getChildSize() - 1);
        if (lastNode.getType().equals("MORE")) {
            treeNode.remove(lastNode);
        }
        AbsTreeMr.genTreeMr(loginBean.getJdbc()).ifPresent(treeMr -> DriverUtil.getDbTypeOp(loginBean.getJdbc()).ifPresent(typeEnum -> {
            TreeMrNode treeMrNode = new TreeMrNode(treeNode.getName(), TreeMrType.valueOf(treeNode.getType()), null);
            treeMrNode.setTableName(TreeUtil.getTableName(treeNode));
            treeMrNode.setSchemaName(TreeUtil.getSchemaName(treeNode, typeEnum));
            treeMrNode.setChildSize(treeNode.getChildSize());
            List<TreeMrNode> allList = treeMr.getChildNode(treeMrNode, loginBean.getConn());
            List<HTreeNode> childList = new ArrayList<>();
            for (TreeMrNode t : allList) {
                HTreeNode newNode = new HTreeNode();
                newNode.setId(t.getId());
                newNode.setName(t.getName());
                newNode.setType(t.getType().name());
                newNode.setOpenIcon(IconFileUtil.getIcon(new IconBean(TreeUtil.ICON_CONTEXT, t.getIcon())));
                childList.add(newNode);
            }
            for (HTreeNode hTreeNode : childList) {
                treeNode.add(hTreeNode);
            }
            tree.openAllByNode(treeNode);
        }));
    }

}
