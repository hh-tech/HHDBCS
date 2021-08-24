package com.hh.hhdb_admin.mgr.tree.handler;

import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.treeMr.base.EventType;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.create_dbobj.treeMr.mr.AbsTreeMr;
import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.frame.swingui.view.hmenu.HPopMenu;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.tree.CsTree;
import com.hh.hhdb_admin.mgr.tree.TreeComp;
import com.hh.hhdb_admin.mgr.tree.TreeUtil;

import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author: Jiang
 * @date: 2020/9/10
 */

public class RightClickHandler {

    private final RightMenuActionHandler rightMenuActionHandler;
    private final LoginBean loginBean;
    private final CsTree tree;

    public RightClickHandler(CsTree tree, LoginBean loginBean) {
        this.rightMenuActionHandler = new RightMenuActionHandler();
        this.tree = tree;
        this.loginBean = loginBean;
    }

    /**
     * 多选点击事件
     *
     * @param treeNodeList 节点列表
     * @param e            e
     */
    public void onClick(List<HTreeNode> treeNodeList, MouseEvent e) throws Exception {
        String type = null;
        for (HTreeNode treeNode : treeNodeList) {
            if (type == null) {
                type = treeNode.getType();
                continue;
            }
            if (!treeNode.getType().equals(type)) {
                return;
            }
        }
        showPopup(e, treeNodeList.toArray(new HTreeNode[0]));
    }

    public void showSchemaTreePopup(MouseEvent e, HTreeNode treeNode) {
        if (treeNode.getType().equals(TreeMrType.DATA_MODEL_SCHEMA_GROUP.name())) {
            HPopMenu popMenu = new HPopMenu();
            popMenu.addItem(new HMenuItem(TreeComp.getLang("refresh")) {
                @Override
                protected void onAction() {
                    TreeUtil.refreshSchemaTree(loginBean, tree, treeNode);
                }
            });
            popMenu.showPopup(e);
        }
    }

    public void showPopup(MouseEvent e, HTreeNode... treeNodes) {
        HTreeNode treeNode = treeNodes[0];
        TreeMrNode treeMrNode = new TreeMrNode(treeNode.getName(), TreeMrType.valueOf(treeNode.getType()), null);
        treeMrNode.setTableName(TreeUtil.getTableName(treeNode));
        treeMrNode.setPackageName(TreeUtil.getPackageName(treeNode));
        DriverUtil.getDbTypeOp(loginBean.getJdbc()).ifPresent(dbType -> {
            treeMrNode.setSchemaName(TreeUtil.getSchemaName(treeNode, dbType));
            AbsTreeMr.genTreeMr(loginBean.getJdbc()).ifPresent(treeMr -> {
                HPopMenu hp = new HPopMenu();
                Map<String, EventType> map = treeNodes.length == 1 ? treeMr.getRightMenu(treeMrNode) : treeMr.getMultiRightMenu(treeMrNode);
                for (String key : map.keySet()) {
                    EventType value = map.get(key);
                    if (value.equals(EventType.SEP)) {
                        hp.addSeparator();
                    } else {
                        HMenuItem item = new HMenuItem(key) {
                            @Override
                            protected void onAction() {
                                rightMenuActionHandler.resolve(value.name().toLowerCase(Locale.ROOT), loginBean, tree, treeNodes);
                            }
                        };
                        hp.addItem(item);
                    }
                }
                hp.showPopup(e);
            });
        });
    }

}
