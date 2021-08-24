package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;

/**
 * @author: Jiang
 * @date: 2020/9/15
 */

public class DebugHandler extends AbsHandler {
    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        String schemaName = getSchemaName();
        TreeMrType nodeType = TreeMrType.valueOf(treeNode.getType().toUpperCase());
        if (nodeType == TreeMrType.FUNCTION || nodeType == TreeMrType.PROCEDURE) {
            sendMsg(CsMgrEnum.FUNCTION, GuiJsonUtil.toJsonCmd(FunctionMgr.CMD_DEBUG_FUNCTION)
                    .add(StartUtil.PARAM_SCHEMA, schemaName).add(FunctionMgr.PARAM_FUNC_NAME, treeNode.getName())
                    .add(FunctionMgr.PARAM_FUNC_ID, treeNode.getId()).add(FunctionMgr.TYPE, nodeType.toString()));
        }
        
    }
}
