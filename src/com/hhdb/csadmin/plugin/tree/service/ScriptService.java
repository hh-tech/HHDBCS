package com.hhdb.csadmin.plugin.tree.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hhdb.csadmin.common.bean.ServerBean;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.HSQL_Util;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;
import com.hhdb.csadmin.plugin.tree.util.TreeNodeUtil;

public class ScriptService {
	public static String tableName = "SCRIPTTAB";
	public static String tabletype = "table";
	public static String schematype = "schema";
	public static String dbtype = "database";
	public static String[] initpros = new String[] { "database", "schema",
			"table" };

	public static String getInitProValue(HTree htree, String proName,
			BaseTreeNode treeNode) {
		if (proName.equals("host")) {
			CmdEvent getsbEvent = new CmdEvent(htree.PLUGIN_ID,
					"com.hhdb.csadmin.plugin.conn", "GetServerBean");
			HHEvent revent = htree.sendEvent(getsbEvent);
			ServerBean serverbean = (ServerBean) revent.getObj();
			return serverbean.getHost();
		} else if (proName.equals("port")) {
			CmdEvent getsbEvent = new CmdEvent(htree.PLUGIN_ID,
					"com.hhdb.csadmin.plugin.conn", "GetServerBean");
			HHEvent revent = htree.sendEvent(getsbEvent);
			ServerBean serverbean = (ServerBean) revent.getObj();
			return serverbean.getPort();
		} else if (proName.equals("username")) {
			CmdEvent getsbEvent = new CmdEvent(htree.PLUGIN_ID,
					"com.hhdb.csadmin.plugin.conn", "GetServerBean");
			HHEvent revent = htree.sendEvent(getsbEvent);
			ServerBean serverbean = (ServerBean) revent.getObj();
			return serverbean.getUserName();
		} else if (proName.equals("password")) {
			CmdEvent getsbEvent = new CmdEvent(htree.PLUGIN_ID,
					"com.hhdb.csadmin.plugin.conn", "GetServerBean");
			HHEvent revent = htree.sendEvent(getsbEvent);
			ServerBean serverbean = (ServerBean) revent.getObj();
			return serverbean.getPassword();
		} else if (proName.equals("database")) {
			if (treeNode.getType().equals(TreeNodeUtil.DB_ITEM_TYPE)) {
				return treeNode.getMetaTreeNodeBean().getName();
			} else if (treeNode.getType().equals(TreeNodeUtil.SCHEMA_ITEM_TYPE)) {
				return treeNode.getParentBaseTreeNode().getParentBaseTreeNode()
						.getMetaTreeNodeBean().getName();
			} else if (treeNode.getType().equals(TreeNodeUtil.TABLE_ITEM_TYPE)) {
				return treeNode.getParentBaseTreeNode().getParentBaseTreeNode()
						.getParentBaseTreeNode().getParentBaseTreeNode()
						.getMetaTreeNodeBean().getName();
			} else {
				return "";
			}
		} else if (proName.equals("schema")) {
			if (treeNode.getType().equals(TreeNodeUtil.TABLE_ITEM_TYPE)) {
				return treeNode.getParentBaseTreeNode().getParentBaseTreeNode()
						.getMetaTreeNodeBean().getName();
			} else if (treeNode.getType().equals(TreeNodeUtil.SCHEMA_ITEM_TYPE)) {
				return treeNode.getMetaTreeNodeBean().getName();
			} else {
				return "";
			}
		} else if (proName.equals("table")) {
			if (treeNode.getType().equals(TreeNodeUtil.TABLE_ITEM_TYPE)) {
				return treeNode.getMetaTreeNodeBean().getName();
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

	/**
	 * 初始化脚本表
	 * 
	 * @throws SQLException
	 */
	public static void initTable() throws SQLException {
		String sql = "create table " + tableName + "(id identity,"
				+ "name varchar(60) not null," + "txt longvarchar not null,"
				+ "type varchar(20) not null)";
		SqlExeUtil.executeUpdate(HSQL_Util.getConnection(), sql);
	}

	/**
	 * 判断脚本表是否存在
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static boolean checkTableEst() throws SQLException {
		String checkSql = String
				.format("SELECT * FROM   INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = '%s'",
						tableName);
		boolean existData = SqlQueryUtil.existData(HSQL_Util.getConnection(),
				checkSql);
		return existData;
	}

	public static List<List<String>> listScriptByType(String type)
			throws SQLException {
		String sql = String.format(
				"SELECT name 脚本名称 FROM SCRIPTTAB where type='%s'", type);
		return SqlQueryUtil.selectStrList(HSQL_Util.getConnection(), sql);
	}

	public static boolean addScript(String name, String txt, String type)
			throws SQLException {
		String sql = String
				.format("INSERT INTO SCRIPTTAB( NAME, TXT, TYPE )VALUES (  '%s', '%s', '%s')",
						name, txt, type);
		SqlExeUtil.executeUpdate(HSQL_Util.getConnection(), sql);
		return true;
	}

	public static boolean updateScript(String name, String txt, String type,
			String oldname) throws SQLException {
		String sql = String
				.format("UPDATE SCRIPTTAB SET NAME='%s',TXT='%s' where name='%s' and type='%s'",
						name, txt, oldname, type);
		SqlExeUtil.executeUpdate(HSQL_Util.getConnection(), sql);
		return true;
	}

	public static boolean delScript(String name, String type)
			throws SQLException {
		String sql = String.format(
				"DELETE FROM SCRIPTTAB where name='%s' and type='%s'", name,
				type);
		SqlExeUtil.executeUpdate(HSQL_Util.getConnection(), sql);
		return true;
	}

	public static Map<String, Object> queryScriptByNameAndType(String name,
			String type) throws SQLException {
		String sql = String
				.format("select id ID,name NAME,txt TXT,type TYPE from scripttab where name='%s' and type='%s'",
						name, type);
		if (SqlQueryUtil.existData(HSQL_Util.getConnection(), sql)) {
			return SqlQueryUtil.selectOne(HSQL_Util.getConnection(), sql);
		} else {
			return null;
		}
	}

	public static boolean extScriptByNameAndType(String name, String type)
			throws SQLException {
		String sql = String.format(
				"select * from scripttab where name='%s' and type='%s'", name,
				type);
		return SqlQueryUtil.existData(HSQL_Util.getConnection(), sql);
	}
}
