package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.dbtask.TaskType;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.mgr.db_task.TaskMgr;

/**
 * @author: Jiang
 * @date: 2020/9/15
 */

public class RestoreHandler extends AbsHandler {

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        DBTypeEnum dbTypeEnum = DriverUtil.getDbType(loginBean.getJdbc());
        if (dbTypeEnum == null) {
            return;
        }
        boolean isDb = treeNode.getType().equals(TreeMrType.DATABASE.name());
        sendMsg(CsMgrEnum.DB_TASK, GuiJsonUtil.toJsonCmd(TaskMgr.CMD_ADD_TASK)
                .add("schema", treeNode.getName())
                .add(TaskMgr.PARAM_TASK_TYPE, TaskType.RESTORE.name())
                .add("isDb", String.valueOf(isDb))
                .add(TaskMgr.PARAM_AUTO_START, true));
    }

}
