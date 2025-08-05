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
 * 处理卸载事件
 */
public class UninstallHandler extends AbsHandler {

    public static final String UNINSTALL_EXTENSION = "drop extension %s cascade";

    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        String extensionName = treeNode.getName();
        SqlExeUtil.executeUpdate(CsVarUtil.conn, String.format(UNINSTALL_EXTENSION, extensionName));
        PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.lang(Word4.EXECUTED_SUCCESSFULLY));
        CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(treeNode.getFolder().getId()));
    }
}
