package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.dbtask.TaskType;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.db_task.TaskMgr;

/**
 * @author: Jiang
 * @date: 2020/12/10
 */

public class TruncateDbHandler extends AbsHandler {

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        StartUtil.eng.doPush(CsMgrEnum.DB_TASK, GuiJsonUtil.toJsonCmd(TaskMgr.CMD_ADD_TASK)
                .add(TaskMgr.PARAM_TASK_TYPE, TaskType.CLEAN.name())
                .add("schema", treeNode.getName())
                .add("isSchema", String.valueOf(treeNode.getType().equals(TreeMrType.SCHEMA.name())))
                .add(TaskMgr.PARAM_AUTO_START, true));
    }

}
