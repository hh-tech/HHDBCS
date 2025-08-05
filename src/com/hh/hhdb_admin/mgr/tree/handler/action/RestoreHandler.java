package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.dbtask2.TaskType;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.TaskAddBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;

/**
 * @author: Jiang
 * @date: 2020/9/15
 */

public class RestoreHandler extends AbsHandler {

    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        if (CsVarUtil.getDbtype() == null) {
            return;
        }
        TaskAddBean bean = new TaskAddBean();
        bean.setTaskType(TaskType.SCHEMA_IMP);
        bean.setSchema(treeNode.getName());
        bean.setIsDb(treeNode.getType() == TreeMrType.DATABASE);
        sendMsg(CsMgrEnum.DB_TASK, bean);
    }

}
