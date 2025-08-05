package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;


/**
 * 处理安装事件
 *
 * @author: Jiang
 * @date: 2020/9/17
 */

public class InstallHandler extends AbsHandler {

    public static final String INSTALL_EXTENSION = "create extension \"%s\"";

    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        String extensionName = treeNode.getName();
        SqlExeUtil.executeUpdate(CsVarUtil.conn, String.format(INSTALL_EXTENSION, extensionName));
        PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.lang(Word4.INSTALLATION_SUCCEEDED));
        CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(treeNode.getFolder().getId()));
    }

}
