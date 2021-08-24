package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: Jiang
 * @date: 2020/9/15
 */

public class AuthHandler extends AbsHandler {
    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        String schemaName =getSchemaName();
        TreeMrType nodeType = TreeMrType.valueOf(treeNode.getType().toUpperCase());
    
        switch (nodeType) {
            case FUNCTION:
                sendMsg(CsMgrEnum.FUNCTION, GuiJsonUtil.toJsonCmd(FunctionMgr.EXAMINE_FUNCTION)
                        .add(FunctionMgr.PARAM_FUNC_NAME, treeNode.getName())
                        .add(FunctionMgr.PARAM_FUNC_ID, StringUtils.isBlank(treeNode.getId()) ? "" : treeNode.getId())
                        .add(StartUtil.PARAM_SCHEMA, schemaName).add(FunctionMgr.TYPE, nodeType.toString()));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + nodeType);
        }
    }
}
