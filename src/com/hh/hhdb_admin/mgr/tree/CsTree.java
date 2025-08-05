package com.hh.hhdb_admin.mgr.tree;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tree2.HTree2;
import com.hh.frame.swingui.view.tree2.LeafBean;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import com.hh.hhdb_admin.mgr.login.SwitchBean;
import com.hh.hhdb_admin.mgr.tree.handler.LeftDoubleClickHandler;
import com.hh.hhdb_admin.mgr.tree.handler.RightClickHandler;

import java.awt.event.MouseEvent;
import java.util.List;

public class CsTree extends HTree2 {
    private RightClickHandler rightHandler;
    private LeftDoubleClickHandler leftDoubleHandler;
    private boolean isSchemaChange;
    private boolean schemaNodeClick = false;

    public CsTree(CsFolderBean rootNode) {
        super(rootNode,false);
        getComp().setDragEnabled(false);
    }

    public static CsTree newSchemaChangeTree(CsFolderBean rootNode) {
        CsTree csTree = new CsTree(rootNode);
        csTree.isSchemaChange = true;
        csTree.schemaNodeClick = true;
        return csTree;
    }

    @Override
    protected void oneClickNode(LeafBean bean) {
        CsFolderBean treeNode = (CsFolderBean) bean;
        if (!isSchemaChange || !schemaNodeClick || treeNode.getType() == TreeMrType.SCHEMA_GROUP) {
            if(!schemaNodeClick) schemaNodeClick = true;
            return;
        }
        if (bean.getFontType() == FaSolidEnum.HOCKEY_PUCK || bean.getFontType() == FaSolidEnum.FOLDER_OPEN ) return;
        if (!PopDlgUtil.confirm(LangMgr.sLang(CsL.SURE_CHANGE))) return;
        schemaNodeClick = false;
        CsVarUtil.eng2.doCall(CsMgrEnum.LOGIN, new SwitchBean(treeNode.getName()));
    }

    @Override
    protected void rightClickNode(List<LeafBean> treeNodes, MouseEvent e) {
    	GlobalWorkUtil.checkConn();
        if (isSchemaChange || treeNodes.size() == 0) return;
        try {
            if (treeNodes.size() == 1) {
                rightHandler.showPopup(e, (CsFolderBean) treeNodes.get(0));
            } else {
                rightHandler.onClick(treeNodes, e);
            }
        } catch (Exception exception) {
            Lg.error(CsTree.class,exception);
            PopDlgUtil.error(CsVarUtil.parentFrame,exception);
        }
    }

    @Override
    protected void dbClickNode(LeafBean treeNode) {
    	GlobalWorkUtil.checkConn();
        try {
            if (isSchemaChange || treeNode.getName().contains("搜索中")) return;
            leftDoubleHandler.onClick((CsFolderBean) treeNode);
        } catch (Exception e) {
            Lg.error(CsTree.class,e);
            PopDlgUtil.error(CsVarUtil.parentFrame,e);
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
