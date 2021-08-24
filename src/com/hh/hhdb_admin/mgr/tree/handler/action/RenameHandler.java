package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.column.ColumnMgr;
import com.hh.hhdb_admin.mgr.sequence.SequenceMgr;
import com.hh.hhdb_admin.mgr.table.TableMgr;
import com.hh.hhdb_admin.mgr.usr.UsrMgr;

/**
 * @author: Jiang
 * @date: 2020/9/15
 */

public class RenameHandler extends AbsHandler {

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        String name = treeNode.getName();
        String schemaName = getSchemaName();
        String tableName = getTableName();

        switch (TreeMrType.valueOf(treeNode.getType().toUpperCase())) {
            case ROLE:
            case USER:
                sendMsg(CsMgrEnum.USR, GuiJsonUtil.toJsonCmd(UsrMgr.CMD_SHOW_RENAME).add(UsrMgr.PARAM_USR_NAME, name));
                break;
            case COLUMN:
                sendMsg(CsMgrEnum.COLUMN, GuiJsonUtil.toJsonCmd(ColumnMgr.CMD_SHOW_RENAME_TABLE_COLUMN)
                        .add(StartUtil.PARAM_SCHEMA, schemaName)
                        .add(ColumnMgr.PARAM_COLUMN_NAME, name)
                        .add(StartUtil.PARAM_TABLE, tableName));
                break;
            case SEQUENCE:
                sendMsg(CsMgrEnum.SEQUENCE, GuiJsonUtil.toJsonCmd(SequenceMgr.CMD_RENAME).add(StartUtil.PARAM_SCHEMA, schemaName)
                        .add(SequenceMgr.SEQ_NAME, name));
                break;
            case TABLE:
                sendMsg(CsMgrEnum.TABLE, GuiJsonUtil.toJsonCmd(TableMgr.CMD_RENAME_TABLE_NAME)
                        .add(StartUtil.PARAM_SCHEMA, schemaName)
                        .add(StartUtil.PARAM_TABLE, name));
                break;
            default:
        }
    }
}
