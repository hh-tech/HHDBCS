package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.column.ColumnMgr;
import com.hh.hhdb_admin.mgr.constraint.ConstraintMgr;
import com.hh.hhdb_admin.mgr.database.DatabaseMgr;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import com.hh.hhdb_admin.mgr.index.IndexMgr;
import com.hh.hhdb_admin.mgr.rule.RuleMgr;
import com.hh.hhdb_admin.mgr.schema.SchemaMgr;
import com.hh.hhdb_admin.mgr.sequence.SequenceMgr;
import com.hh.hhdb_admin.mgr.tablespace.TableSpaceMgr;
import com.hh.hhdb_admin.mgr.tree.TreeComp;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;
import com.hh.hhdb_admin.mgr.tree.TreeUtil;
import com.hh.hhdb_admin.mgr.trigger.TriggerMgr;
import com.hh.hhdb_admin.mgr.usr.UsrMgr;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.sql.SQLException;
import java.util.Map;

/**
 * 删除事件处理
 *
 * @author Jiang
 * @date 2020/9/15
 */

public class DeleteHandler extends AbsHandler {

	public static final String DELETE_SCHEMA_ITEM = "DROP %s %s.%s %s";
	public static final String DELETE_OR_PACKAGE = "DROP PACKAGE \"%s\"";
	public static final String DELETE_OR_PACKAGE_BODY = "DROP PACKAGE BODY \"%s\"";
	protected boolean isCascade = false;
	protected boolean isMulti = false;


	@Override
	public void resolve(HTreeNode treeNode) throws Exception {
		if (!isMulti) {
			int res = JOptionPane.showConfirmDialog(null, TreeComp.getLang("sure_delete"), getLang("hint"), JOptionPane.YES_NO_OPTION);
			if (res != 0) {
				return;
			}
		}
		doDel(treeNode);

	}

	protected void doDel(HTreeNode treeNode) throws SQLException {
		String nodeName = treeNode.getName();
		TreeMrType nodeType = TreeMrType.valueOf(treeNode.getType().toUpperCase());
		String tableName = getTableName();
		String schemaName = getSchemaName();
		switch (TreeMrType.valueOf(treeNode.getType().toUpperCase())) {
			case DATABASE:
				sendMsg(CsMgrEnum.DATABASE, GuiJsonUtil.toJsonCmd(DatabaseMgr.CMD_DELETE_DATABASE)
						.add("name", nodeName));
				break;
			case TABLE_SPACE:
				sendMsg(CsMgrEnum.TABLE_SPACE, GuiJsonUtil.toJsonCmd(TableSpaceMgr.CMD_DELETE_TABLE_SPACE)
						.add("name", nodeName));
				break;
			case SCHEMA:
				sendMsg(CsMgrEnum.SCHEMA, GuiJsonUtil.toJsonCmd(SchemaMgr.CMD_DELETE).add(StartUtil.PARAM_SCHEMA, nodeName));
				break;
			case SEQUENCE:
				sendMsg(CsMgrEnum.SEQUENCE, GuiJsonUtil.toJsonCmd(SequenceMgr.CMD_DELETE).add(StartUtil.PARAM_SCHEMA, schemaName)
						.add(SequenceMgr.SEQ_NAME, nodeName));
				break;
			case VIEW:
			case TABLE:
			case PARTITION_TABLE_CHILD:
			case M_VIEW:
			case TYPE:
				DBTypeEnum dbTypeEnum = DriverUtil.getDbType(loginBean.getJdbc());
				if (dbTypeEnum == DBTypeEnum.mysql) {
					SqlExeUtil.executeUpdate(loginBean.getConn(), String.format(DELETE_SCHEMA_ITEM, TreeUtil.getDbItemType(nodeType),
							schemaName, nodeName, ""));
				} else {
					String sql = String.format(DELETE_SCHEMA_ITEM, TreeUtil.getDbItemType(nodeType),
							SqlStrUtil.dealDoubleQuote(dbTypeEnum, schemaName), SqlStrUtil.dealDoubleQuote(dbTypeEnum, nodeName), "");
					if (isCascade) {
						sql += dbTypeEnum == DBTypeEnum.oracle ? " cascade constraints" : " cascade";
					}
					SqlExeUtil.executeUpdate(loginBean.getConn(), sql);
				}
				delNode(treeNode);
				PopPaneUtil.info(StartUtil.parentFrame.getWindow(), getLang("delete_success"));
				break;
			case TRIGGER:
				sendMsg(CsMgrEnum.TRIGGER, GuiJsonUtil.toJsonCmd(TriggerMgr.CMD_DELETE)
						.add("name", nodeName).add(StartUtil.PARAM_SCHEMA, schemaName).add(StartUtil.PARAM_TABLE, tableName));
				break;
			case PROCEDURE:
			case FUNCTION:
			case TRIGGER_FUNCTION:
				sendMsg(CsMgrEnum.FUNCTION, GuiJsonUtil.toJsonCmd(FunctionMgr.CMD_DELETE_FUNCTION)
						.add(FunctionMgr.PARAM_FUNC_NAME, nodeName)
						.add(FunctionMgr.PARAM_FUNC_ID, StringUtils.isBlank(treeNode.getId()) ? "" : treeNode.getId())
						.add(StartUtil.PARAM_SCHEMA, schemaName).add(FunctionMgr.TYPE, nodeType.toString()));
				break;
			case AGGREGATE_FUNCTION:
				String sql = "DROP AGGREGATE \"%s\".\"%s\"(%s)";
				String getParamTypeSql = "SELECT COALESCE(hh_catalog.hh_get_function_identity_arguments(oid)) as arguments " +
						"FROM hh_proc " +
						"WHERE oid = %s";
				Map<String, String> paramTypeData = SqlQueryUtil.selectOneStrMap(loginBean.getConn(), String.format(getParamTypeSql, treeNode.getId()));
				if (StringUtils.isBlank(paramTypeData.get("arguments"))) {
					return;
				}
				SqlExeUtil.executeUpdate(loginBean.getConn(), String.format(sql, schemaName, nodeName, paramTypeData.get("arguments")));
				delNode(treeNode);
				PopPaneUtil.info(StartUtil.parentFrame.getWindow(), getLang("delete_success"));
				break;
			case USER:
				if (loginBean.getJdbc().getUser().equals(nodeName)) {
					PopPaneUtil.error(StartUtil.parentFrame.getWindow(), "无法删除当前登录用户");
					return;
				}
				sendMsg(CsMgrEnum.USR, GuiJsonUtil.toJsonCmd(UsrMgr.CMD_DELETE)
						.add(UsrMgr.PARAM_USR_NAME, nodeName));
				break;
			case ROLE:
				sendMsg(CsMgrEnum.USR, GuiJsonUtil.toJsonCmd(UsrMgr.CMD_DELETE_ROLE)
						.add(UsrMgr.PARAM_USR_NAME, nodeName));
				break;
			case COLUMN:
				sendMsg(CsMgrEnum.COLUMN, GuiJsonUtil.toJsonCmd(ColumnMgr.CMD_DELETE_TABLE_COLUMN)
						.add(StartUtil.PARAM_SCHEMA, schemaName).add(StartUtil.PARAM_TABLE, tableName)
						.add(ColumnMgr.PARAM_COLUMN_NAME, nodeName));
				break;
			case RULE:
				sendMsg(CsMgrEnum.RULE, GuiJsonUtil.toJsonCmd(RuleMgr.CMD_DELETE).add("schemaName", schemaName)
						.add("tableName", tableName).add("name", nodeName));
				break;
			case INDEX:
				sendMsg(CsMgrEnum.INDEX, GuiJsonUtil.toJsonCmd(IndexMgr.CMD_DELETE_TABLE_INDEX)
						.add(StartUtil.PARAM_SCHEMA, schemaName)
						.add(StartUtil.PARAM_TABLE, tableName)
						.add("indexName", nodeName));
				break;
			case FOREIGN_KEY:
			case PRIMARY_KEY:
			case CHECK_KEY:
			case UNIQUE_KEY:
				sendMsg(CsMgrEnum.CONSTRAINT, GuiJsonUtil.toJsonCmd(ConstraintMgr.CMD_DELETE_CONSTRAINT)
						.add(StartUtil.PARAM_SCHEMA, schemaName).add(StartUtil.PARAM_TABLE, tableName)
						.add("constType", treeNode.getParentHTreeNode().getType())
						.add("constName", nodeName));
				break;
			case PACKAGE:
				SqlExeUtil.executeUpdate(loginBean.getConn(), String.format(DELETE_OR_PACKAGE, nodeName));
				delNode(treeNode);
				PopPaneUtil.info(StartUtil.parentFrame.getWindow(), getLang("delete_success"));
				break;
			case PACKAGE_BODY:
				SqlExeUtil.executeUpdate(loginBean.getConn(), String.format(DELETE_OR_PACKAGE_BODY, treeNode.getParentHTreeNode().getName()));
				PopPaneUtil.info(StartUtil.parentFrame.getWindow(), getLang("delete_success"));
				break;
			case SYNONYM:
				SqlExeUtil.executeUpdate(loginBean.getConn(), String.format("drop synonym %s.%s", schemaName, nodeName));
				delNode(treeNode);
				PopPaneUtil.info(StartUtil.parentFrame.getWindow(), getLang("delete_success"));
				break;
			default:
				throw new IllegalStateException("Unexpected value: " + TreeMrType.valueOf(treeNode.getType().toUpperCase()));
		}
	}

	public void delNode(HTreeNode treeNode) {
		String objId = StartUtil.eng.push2SharedMap(treeNode);
		JsonObject msg = GuiJsonUtil.toJsonCmd(TreeMgr.CMD_DELETE_NODE);
		msg.add(TreeMgr.PARAM_NODE_OBJ_ID, objId);
		sendMsg(CsMgrEnum.TREE, msg);
	}

	public void cascadeResolve(HTreeNode treeNode) throws Exception {
		this.isCascade = true;
		resolve(treeNode);
		this.isCascade = false;
	}

	public void resolveMulti(HTreeNode treeNode) throws Exception {
		this.isMulti = true;
		resolve(treeNode);
		this.isMulti = false;
	}

	public void cascadeMultiResolve(HTreeNode treeNode) throws Exception {
		this.isMulti = this.isCascade = true;
		resolve(treeNode);
		this.isCascade = this.isMulti = false;
	}
}
