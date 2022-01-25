package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;


/**
 * 处理安装事件
 *
 * @author: Jiang
 * @date: 2020/9/17
 */

public class InstallHandler extends AbsHandler {

    public static final String INSTALL_EXTENSION = "create extension \"%s\"";

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        String extensionName = treeNode.getName();
        SqlExeUtil.executeUpdate(loginBean.getConn(), String.format(INSTALL_EXTENSION, extensionName));
        PopPaneUtil.info(StartUtil.parentFrame.getWindow(), getLang("installationSucceeded"));

//        DBTypeEnum dbTypeEnum = DriverUtil.getDbTypeOp(loginBean.getJdbc()).orElse(DBTypeEnum.hhdb);
        String schemaName = getSchemaName();
        sendMsg(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH)
                .add(StartUtil.PARAM_SCHEMA, schemaName)
                .add(TreeMgr.PARAM_NODE_TYPE, TreeMrType.DATA_MODEL_EXTENSION_GROUP.name()));

    }

}
