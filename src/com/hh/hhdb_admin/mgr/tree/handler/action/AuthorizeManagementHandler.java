package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.mgr.usr.UsrMgr;

/**
 * @author: Jiang
 * @date: 2020/9/15
 */

public class AuthorizeManagementHandler extends AbsHandler {
    @Override
    public void resolve(HTreeNode treeNode) {
        String name = treeNode.getName();
        sendMsg(CsMgrEnum.USR, GuiJsonUtil.toJsonCmd(UsrMgr.CMD_SHOW_PERMISSION).add(UsrMgr.PARAM_USR_NAME, name));
    }
}
