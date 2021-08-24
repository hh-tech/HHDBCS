package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.dbtask.TaskType;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.db_task.TaskMgr;

/**
 * @author: Jiang
 * @date: 2021/2/20
 */

public class GenDataHandler extends AbsHandler {

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        sendMsg(CsMgrEnum.DB_TASK,
                GuiJsonUtil.toJsonCmd(TaskMgr.CMD_ADD_TASK)
                        .add(TaskMgr.PARAM_TASK_TYPE, TaskType.GEN_TAB_DATA.name())
                        .add(StartUtil.PARAM_SCHEMA, getSchemaName())
                        .add(StartUtil.PARAM_TABLE, treeNode.getName())
                        .add(TaskMgr.PARAM_AUTO_START, true));
    }

}
