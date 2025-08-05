package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.dbtask2.TaskType;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.redis.db.ResdiDb;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.bean.TaskAddBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;


public class CleanDbHandler extends AbsHandler {
    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        TreeMrType nodeType = treeNode.getType();
        
        if (CsVarUtil.getDbtype() == DBTypeEnum.redis) {
            if (PopDlgUtil.confirm(CsVarUtil.parentFrame, LangMgr.sLang(CsL.CLEAN_DB) + "?")) {
                new ResdiDb(CsVarUtil.conn, treeNode.getMrNode().getSchemaName()).clean();
                CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(treeNode.getId()));
                PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.lang(Word4.OPERATE_SUCCESSFULLY));
            }
        } else {
            TaskAddBean bean = new TaskAddBean();
            bean.setTaskType(TaskType.SCHEMA_CLEAN);
            bean.setSchema(treeNode.getName());
            bean.setIsSchema(nodeType == TreeMrType.SCHEMA);
            CsVarUtil.eng2.doCall(CsMgrEnum.DB_TASK, bean);
        }
    }

}
