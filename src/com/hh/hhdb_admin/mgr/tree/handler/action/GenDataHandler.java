package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.dbtask2.TaskType;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.TaskAddBean;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;

/**
 * 生成表格数据
 */
public class GenDataHandler extends AbsHandler {
    
    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        TaskAddBean bean = new TaskAddBean();
        bean.setTaskType(TaskType.GEN_TAB_DATA);
        bean.setSchema(getSchemaName());
        bean.setName(getTableName());
        sendMsg(CsMgrEnum.DB_TASK, bean);
    }
    
}
