package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.usr2.UsrBean;

/**
 * 显示用户权限
 * @author: Jiang
 * @date: 2020/9/15
 */

public class AuthorizeManagementHandler extends AbsHandler {
    @Override
    public void resolve(CsFolderBean treeNode) {
        String name = treeNode.getName();
        UsrBean us = new UsrBean(UsrBean.OprType.PERM);
        us.setName(name);
        sendMsg(CsMgrEnum.USR, us);
    }
}
