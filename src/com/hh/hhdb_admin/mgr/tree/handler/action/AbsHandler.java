package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.tree.CsTree;
import com.hh.hhdb_admin.mgr.tree.TreeComp;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;

/**
 * @author: Jiang
 * @date: 2020/12/21
 */

public abstract class AbsHandler {

    protected LoginBean loginBean;
    protected CsTree tree;

    protected String schemaName;
    protected String tableName;

    public abstract void resolve(HTreeNode treeNode) throws Exception;

    public AbsHandler setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
        return this;
    }

    public void setCsTree(CsTree tree) {
        this.tree = tree;
    }

    /**
     * 发送消息到对应插件
     *
     * @param mgr    目标插件
     * @param resMsg 消息
     */
    public void sendMsg(CsMgrEnum mgr, JsonObject resMsg) {
        System.out.println(mgr.toString() + "---" + resMsg.toPrettyString());
        if (StartUtil.eng != null) {
            StartUtil.eng.doPush(mgr, resMsg);
        }
    }

    public void refreshWithNode(HTreeNode treeNode) {
        String id = StartUtil.eng.push2SharedMap(treeNode);
        sendMsg(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_DELETE_NODE).add(TreeMgr.PARAM_NODE_OBJ_ID, id));
    }

    public static String getLang(String key) {
        return LangMgr2.getValue(TreeComp.DOMAIN_NAME, key);
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getTableName() {
        return tableName;
    }
}
