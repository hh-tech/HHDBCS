package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.dbobj2.ora.OraSessionEnum;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.column.ColumnMgr;
import com.hh.hhdb_admin.mgr.constraint.ConstraintMgr;
import com.hh.hhdb_admin.mgr.database.DatabaseMgr;
import com.hh.hhdb_admin.mgr.dblink.DblinkMgr;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import com.hh.hhdb_admin.mgr.index.IndexMgr;
import com.hh.hhdb_admin.mgr.pack.PackageMgr;
import com.hh.hhdb_admin.mgr.rule.RuleMgr;
import com.hh.hhdb_admin.mgr.schema.SchemaMgr;
import com.hh.hhdb_admin.mgr.sequence.SequenceMgr;
import com.hh.hhdb_admin.mgr.synonym.SynonymMgr;
import com.hh.hhdb_admin.mgr.table.TableMgr;
import com.hh.hhdb_admin.mgr.tablespace.TableSpaceMgr;
import com.hh.hhdb_admin.mgr.trigger.TriggerMgr;
import com.hh.hhdb_admin.mgr.type.TypeMgr;
import com.hh.hhdb_admin.mgr.usr.UsrMgr;
import com.hh.hhdb_admin.mgr.view.ViewMgr;

/**
 * 处理新增事件
 *
 * @author: Jiang
 * @date: 2020/9/15
 */

public class AddHandler extends AbsHandler {

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        String schemaName = getSchemaName();
        String tableName = getTableName();
        TreeMrType nodeType = TreeMrType.valueOf(treeNode.getType().toUpperCase());
        switch (nodeType) {
            case TABLE_GROUP:
            case TABLE:
                sendMsg(CsMgrEnum.TABLE, GuiJsonUtil.toJsonCmd(TableMgr.CMD_SHOW_ADD_TABLE).add(StartUtil.PARAM_SCHEMA, schemaName));
                break;
            case SEQUENCE_GROUP:
            case SEQUENCE:
                sendMsg(CsMgrEnum.SEQUENCE, GuiJsonUtil.toJsonCmd(SequenceMgr.CMD_CREATE).add(StartUtil.PARAM_SCHEMA, schemaName));
                break;
            case VIEW_GROUP:
            case VIEW:
                sendMsg(CsMgrEnum.VIEW, GuiJsonUtil.toJsonCmd(ViewMgr.CMD_SHOW_CREATE_VIEW).add(StartUtil.PARAM_SCHEMA, schemaName));
                break;
            case FUNCTION_GROUP:
            case PROCEDURE_GROUP:
                String type = nodeType.equals(TreeMrType.FUNCTION_GROUP) ? TreeMrType.FUNCTION.name() : TreeMrType.PROCEDURE.name();
                sendMsg(CsMgrEnum.FUNCTION, GuiJsonUtil.toJsonCmd(FunctionMgr.CMD_ADD_FUNCTION)
                        .add(StartUtil.PARAM_SCHEMA, schemaName).add(FunctionMgr.TYPE, type));
                break;
            case M_VIEW_GROUP:
            case M_VIEW:
                sendMsg(CsMgrEnum.VIEW, GuiJsonUtil.toJsonCmd(ViewMgr.CMD_SHOW_CREATE_MVIEW).add(StartUtil.PARAM_SCHEMA, schemaName));
                break;
            case ROOT_DATABASE_GROUP:
                sendMsg(CsMgrEnum.DATABASE, GuiJsonUtil.toJsonCmd(DatabaseMgr.CMD_SHOW_ADD_DATABASE));
                break;
            case DATA_MODEL_SCHEMA_GROUP:
                sendMsg(CsMgrEnum.SCHEMA, GuiJsonUtil.toJsonCmd(SchemaMgr.CMD_ADD));
                break;
            case ROOT_TABLE_SPACE_GROUP:
                sendMsg(CsMgrEnum.TABLE_SPACE, GuiJsonUtil.toJsonCmd(TableSpaceMgr.CMD_SHOW_ADD_TABLE_SPACE));
                break;
            case TABLE_SPACE:
                break;
            case ROOT_USER_GROUP:
                sendMsg(CsMgrEnum.USR, GuiJsonUtil.toJsonCmd(UsrMgr.CMD_SHOW_ADD_USER));
                break;
            case ROOT_ROLE_GROUP:
                sendMsg(CsMgrEnum.USR, GuiJsonUtil.toJsonCmd(UsrMgr.CMD_SHOW_ADD_ROLE));
                break;
            case RULE_GROUP:
                sendMsg(CsMgrEnum.RULE, GuiJsonUtil.toJsonCmd(RuleMgr.CMD_ADD).add(StartUtil.PARAM_SCHEMA, schemaName)
                        .add(StartUtil.PARAM_TABLE, tableName));
                break;
            case COLUMN_GROUP:
                sendMsg(CsMgrEnum.COLUMN, GuiJsonUtil.toJsonCmd(ColumnMgr.CMD_SHOW_ADD_TABLE_COLUMN)
                        .add(StartUtil.PARAM_SCHEMA, schemaName)
                        .add(StartUtil.PARAM_TABLE, tableName));
                break;
            case INDEX_GROUP:
                sendMsg(CsMgrEnum.INDEX, GuiJsonUtil.toJsonCmd(IndexMgr.CMD_SHOW_ADD_TABLE_INDEX)
                        .add(StartUtil.PARAM_SCHEMA, schemaName)
                        .add(StartUtil.PARAM_TABLE, tableName));
                break;
            case TRIGGER_GROUP:
                sendMsg(CsMgrEnum.TRIGGER, GuiJsonUtil.toJsonCmd(TriggerMgr.CMD_ADD_TRIGGER).add(StartUtil.PARAM_SCHEMA, schemaName)
                        .add(StartUtil.PARAM_TABLE, tableName));
                break;
            case FOREIGN_KEY_GROUP:
                sendMsg(CsMgrEnum.CONSTRAINT, GuiJsonUtil.toJsonCmd(ConstraintMgr.CMD_SHOW_CONSTRAINT_FK_DIALOG)
                        .add("constType", nodeType.name())
                        .add(StartUtil.PARAM_SCHEMA, schemaName)
                        .add(StartUtil.PARAM_TABLE, tableName));
                break;
            case UNIQUE_KEY_GROUP:
                sendMsg(CsMgrEnum.CONSTRAINT, GuiJsonUtil.toJsonCmd(ConstraintMgr.CMD_SHOW_CONSTRAINT_UK_DIALOG)
                        .add("constType", nodeType.name())
                        .add(StartUtil.PARAM_SCHEMA, schemaName)
                        .add(StartUtil.PARAM_TABLE, tableName));
                break;
            case CHECK_KEY_GROUP:
                sendMsg(CsMgrEnum.CONSTRAINT, GuiJsonUtil.toJsonCmd(ConstraintMgr.CMD_SHOW_CONSTRAINT_CK_DIALOG)
                        .add("constType", nodeType.name())
                        .add(StartUtil.PARAM_SCHEMA, schemaName)
                        .add(StartUtil.PARAM_TABLE, tableName));
                break;
            case PRIMARY_KEY_GROUP:
                sendMsg(CsMgrEnum.CONSTRAINT, GuiJsonUtil.toJsonCmd(ConstraintMgr.CMD_SHOW_CONSTRAINT_PK_DIALOG)
                        .add("constType", nodeType.name())
                        .add(StartUtil.PARAM_SCHEMA, schemaName)
                        .add(StartUtil.PARAM_TABLE, tableName));
                break;
            case PACKAGE_GROUP:
                sendMsg(CsMgrEnum.PACKAGE, GuiJsonUtil.toJsonCmd(PackageMgr.CMD_ADD)
                        .add(StartUtil.PARAM_SCHEMA, schemaName)
                        .add(StartUtil.PARAM_TABLE, tableName));
                break;
            case DBLINK_GROUP:
                sendMsg(CsMgrEnum.DBLINK, GuiJsonUtil.toJsonCmd(DblinkMgr.CMD_SHOW)
                        .add(DblinkMgr.PARAM_NAME, ""));
                break;
            case SYNONYM_GROUP:
                sendMsg(CsMgrEnum.SYNONYM, GuiJsonUtil.toJsonCmd(SynonymMgr.CMD_SHOW)
                        .add(SynonymMgr.PARAM_NAME, ""));
                break;
            case TYPE_GROUP:
            case TYPE_BODY_GROUP:
                OraSessionEnum sessionEnum = TreeMrType.TYPE_GROUP.equals(nodeType)
                        ? OraSessionEnum.type : OraSessionEnum.typebody;
                sendMsg(CsMgrEnum.TYPE, GuiJsonUtil.toJsonCmd(TypeMgr.CMD_ADD)
                        .add(StartUtil.PARAM_SCHEMA, schemaName)
                        .add(TypeMgr.PARAM_TYPE, sessionEnum.name()));
                break;
            default:
        }
    }

}
