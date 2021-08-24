package com.hh.hhdb_admin.mgr.tree;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.tree.HTree;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginMgr;
import com.hh.hhdb_admin.mgr.tree.handler.LeftDoubleClickHandler;
import com.hh.hhdb_admin.mgr.tree.handler.RightClickHandler;

import java.awt.event.MouseEvent;
import java.util.List;

public class CsTree extends HTree {

    private RightClickHandler rightHandler;
    private LeftDoubleClickHandler leftDoubleHandler;
    private boolean isSchemaChange;

    public CsTree(HTreeNode rootNode) {
        super(rootNode);
    }

    public static CsTree newSchemaChangeTree(HTreeNode rootNode) {
        CsTree csTree = new CsTree(rootNode);
        csTree.isSchemaChange = true;
        return csTree;
    }

    @Override
    protected void selectTreeNode(HTreeNode treeNode) {
        if (!isSchemaChange || treeNode.getType().equals(TreeMrType.DATA_MODEL_SCHEMA_GROUP.name())) {
            return;
        }
        boolean sure = PopPaneUtil.confirm(TreeComp.getLang("sure_change"));
        if (!sure) {
            return;
        }
        StartUtil.eng.doPush(CsMgrEnum.LOGIN, GuiJsonUtil.toJsonCmd(LoginMgr.CMD_SWITCH_SCHEMA)
                .add("schemaName", treeNode.getName()));
    }

    @Override
    protected void rightClickTreeNode(HTreeNode treeNode, MouseEvent e) {
        super.rightClickTreeNode(treeNode, e);
        try {
            if (isSchemaChange) {
                rightHandler.showSchemaTreePopup(e, treeNode);
            } else {
                rightHandler.showPopup(e, treeNode);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), exception.getMessage());
        }
    }

    @Override
    protected void rightClickTreeNode(List<HTreeNode> treeNodes, MouseEvent e) {
        if (isSchemaChange) {
            return;
        }
        try {
            if (treeNodes.size() == 0) {
                return;
            }
            rightHandler.onClick(treeNodes, e);
        } catch (Exception exception) {
            exception.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), exception.getMessage());
        }
    }

    @Override
    protected void dbClickTreeNode(HTreeNode treeNode) {
        try {
            if (isSchemaChange) {
                return;
            }
            leftDoubleHandler.onClick(treeNode);
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e.getMessage());
        }
    }

    public RightClickHandler getRightHandler() {
        return rightHandler;
    }

    public CsTree setRightHandler(RightClickHandler rightHandler) {
        this.rightHandler = rightHandler;
        return this;
    }

    public LeftDoubleClickHandler getLeftDoubleHandler() {
        return leftDoubleHandler;
    }

    public void setLeftDoubleHandler(LeftDoubleClickHandler leftDoubleHandler) {
        this.leftDoubleHandler = leftDoubleHandler;
    }

}
