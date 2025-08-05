package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.treeMr.TreeMrUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;

public class RefreshDataHandler extends AbsHandler {

    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        if (PopDlgUtil.confirm(CsVarUtil.parentFrame, LangMgr.lang(Word4.SURE_UPDATE))) {
            String name = treeNode.getName();
            TreeMrUtil.refreshData(DriverUtil.getDbType(jdbc),CsVarUtil.conn, getSchemaName(), name);
            PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.lang(Word4.EXECUTED_SUCCESSFULLY));
        }
    }
}
