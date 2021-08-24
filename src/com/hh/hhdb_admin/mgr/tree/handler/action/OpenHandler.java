package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.table_open.TableOpenMgr;
import com.hh.hhdb_admin.mgr.view.ViewMgr;

/**
 * 处理打开事件
 *
 * @author: Jiang
 * @date: 2020/9/15
 */

public class OpenHandler extends AbsHandler {

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        String name = treeNode.getName();
        String schemaName = getSchemaName();
        switch (TreeMrType.valueOf(treeNode.getType().toUpperCase())) {
            case VIEW:
                sendMsg(CsMgrEnum.VIEW, GuiJsonUtil.toJsonCmd(ViewMgr.CMD_SHOW_OPEN_VIEW).add(ViewMgr.PARAM_VIEW_NAME, name)
                        .add(StartUtil.PARAM_SCHEMA, schemaName));
                break;
            case M_VIEW:
                sendMsg(CsMgrEnum.VIEW, GuiJsonUtil.toJsonCmd(ViewMgr.CMD_SHOW_OPEN_MVIEW).add(ViewMgr.PARAM_VIEW_NAME, name)
                        .add(StartUtil.PARAM_SCHEMA, schemaName));
                break;
            default:
                sendMsg(CsMgrEnum.TABLE_OPEN, GuiJsonUtil.toJsonCmd(TableOpenMgr.CMD_OPEN_TABLE)
                        .add(StartUtil.PARAM_SCHEMA, schemaName).add(StartUtil.PARAM_TABLE, name));
        }
    }

}
