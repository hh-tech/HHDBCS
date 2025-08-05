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
 * 处理清除回收站事件
 *
 * @author Yusai
 */

public class PurgeRecycleBinHandler extends AbsHandler {

    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        if (!PopDlgUtil.confirm(CsVarUtil.parentFrame,LangMgr.lang(Word4.SURE_PURGE))) {
            return;
        }
        SqlExeUtil.executeUpdate(CsVarUtil.conn, "PURGE RECYCLEBIN");
        PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.lang(Word4.EXECUTED_SUCCESSFULLY));
        CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(treeNode.getId()));
    }

}
