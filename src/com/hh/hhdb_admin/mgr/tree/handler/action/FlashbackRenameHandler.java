package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;
import org.apache.commons.lang3.StringUtils;

/**
 * 处理闪回表重命名事件
 *
 * @author Yusai
 */

public class FlashbackRenameHandler extends AbsHandler {

    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        String newName = PopDlgUtil.nameInput(CsVarUtil.parentFrame,LangMgr.sLang(CsL.INPUT_NEW_TABLE_NAME),"");
        if (StringUtils.isNotBlank(newName)) {
            SqlExeUtil.executeUpdate(CsVarUtil.conn, String.format("FLASHBACK TABLE %s TO BEFORE DROP RENAME TO %s", treeNode.getName(), newName));
            PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.lang(Word4.FLASHBACK_SUCCESS));
            CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(treeNode.getFolder().getId()));
        }
    }

}
