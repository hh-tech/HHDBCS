package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.dbobj2.ora.OraSessionEnum;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.column.ColumnMgr;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import com.hh.hhdb_admin.mgr.pack.PackageMgr;
import com.hh.hhdb_admin.mgr.schema.SchemaMgr;
import com.hh.hhdb_admin.mgr.sequence.SequenceMgr;
import com.hh.hhdb_admin.mgr.trigger.TriggerMgr;
import com.hh.hhdb_admin.mgr.usr.UsrMgr;
import com.hh.hhdb_admin.mgr.view.ViewMgr;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: Jiang
 * @date: 2020/9/15
 */

public class DesignHandler extends AbsHandler {

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        String name = treeNode.getName();
        String schemaName = getSchemaName();
        String tableName = getTableName();
        TreeMrType nodeType = TreeMrType.valueOf(treeNode.getType().toUpperCase());
        switch (nodeType) {
            case USER:
                sendMsg(CsMgrEnum.USR, GuiJsonUtil.toJsonCmd(UsrMgr.CMD_SHOW_UPDATE_USER)
                        .add(UsrMgr.PARAM_USR_NAME, name));
                return;
            case ROLE:
                sendMsg(CsMgrEnum.USR, GuiJsonUtil.toJsonCmd(UsrMgr.CMD_SHOW_UPDATE_ROLE)
                        .add(UsrMgr.PARAM_USR_NAME, name));
                return;
            case VIEW:
                sendMsg(CsMgrEnum.VIEW, GuiJsonUtil.toJsonCmd(ViewMgr.CMD_SHOW_UPDATE_VIEW)
                        .add(ViewMgr.PARAM_VIEW_NAME, name).add(StartUtil.PARAM_SCHEMA, schemaName));
                break;
            case M_VIEW:
                sendMsg(CsMgrEnum.VIEW, GuiJsonUtil.toJsonCmd(ViewMgr.CMD_SHOW_UPDATE_MVIEW)
                        .add(ViewMgr.PARAM_VIEW_NAME, name).add(StartUtil.PARAM_SCHEMA, schemaName));
                break;
            case FUNCTION:
            case TRIGGER_FUNCTION:
            case PROCEDURE:
                String type = nodeType.equals(TreeMrType.PROCEDURE) ? TreeMrType.PROCEDURE.name() : TreeMrType.FUNCTION.name();
                sendMsg(CsMgrEnum.FUNCTION, GuiJsonUtil.toJsonCmd(FunctionMgr.CMD_EDIT_FUNCTION)
                        .add(StartUtil.PARAM_SCHEMA, schemaName).add(FunctionMgr.PARAM_FUNC_NAME, name)
                        .add(FunctionMgr.PARAM_FUNC_ID, StringUtils.isBlank(treeNode.getId()) ? "" : treeNode.getId())
                        .add(FunctionMgr.TYPE, type));
                break;
            case SEQUENCE:
                sendMsg(CsMgrEnum.SEQUENCE, GuiJsonUtil.toJsonCmd(SequenceMgr.CMD_DESIGN)
                        .add(StartUtil.PARAM_SCHEMA, schemaName).add(SequenceMgr.SEQ_NAME, name));
                break;
            case TRIGGER:
                sendMsg(CsMgrEnum.TRIGGER, GuiJsonUtil.toJsonCmd(TriggerMgr.CMD_UPDATE_TRIGGER)
                        .add(StartUtil.PARAM_SCHEMA, schemaName).add(StartUtil.PARAM_TABLE, tableName).add(TriggerMgr.PARAM_TRIGGER_NAME, name));
                break;
            case SCHEMA:
                sendMsg(CsMgrEnum.SCHEMA, GuiJsonUtil.toJsonCmd(SchemaMgr.CMD_DESIGN).add(StartUtil.PARAM_SCHEMA, name));
                break;
            case COLUMN:
                sendMsg(CsMgrEnum.COLUMN, GuiJsonUtil.toJsonCmd(ColumnMgr.CMD_SHOW_UPDATE_TABLE_COLUMN)
                        .add(StartUtil.PARAM_SCHEMA, schemaName)
                        .add(StartUtil.PARAM_TABLE, tableName)
                        .add(ColumnMgr.PARAM_COLUMN_NAME, name));
                break;
            case PACKAGE_HEAD:
            case PACKAGE_BODY:
                OraSessionEnum sessionEnum = nodeType.equals(TreeMrType.PACKAGE_HEAD) ? OraSessionEnum.pack : OraSessionEnum.packbody;
                String packageName = treeNode.getParentHTreeNode().getName();
                if (packageName.contains(".")) {
                    schemaName = packageName.split("\\.")[0];
                    packageName = packageName.split("\\.")[1];
                }
                sendMsg(CsMgrEnum.PACKAGE, GuiJsonUtil.toJsonCmd(PackageMgr.CMD_DESIGN)
                        .add(PackageMgr.HEAD_OR_BODY, sessionEnum.name())
                        .add(StartUtil.PARAM_SCHEMA, schemaName)
                        .add(StartUtil.PARAM_TABLE, packageName));
                break;
            default:
        }
    }
}
