package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;

/**
 * @author: Jiang
 * @date: 2021/06/29
 * <p>
 * 处理清空事件
 */

public class AddDebugInfoHandler extends AbsHandler {

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        JsonObject jsonObject = GuiJsonUtil.toJsonCmd(FunctionMgr.ADD_DEBUG_INFO);
        jsonObject.add(FunctionMgr.PARAM_FUNC_NAME, treeNode.getName());
        jsonObject.add(FunctionMgr.TYPE, treeNode.getType());
        jsonObject.add(StartUtil.PARAM_SCHEMA,  getSchemaName());
        sendMsg(CsMgrEnum.FUNCTION, jsonObject);
    }

}
