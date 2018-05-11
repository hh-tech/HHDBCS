package com.hhdb.csadmin.plugin.tree.service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.TreePath;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.csv.writer.CsvWriter;
import com.hh.frame.dbobj.hhdb.HHdbSession;
import com.hh.frame.dbobj.hhdb.HHdbTable;
import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.common.bean.SqlBean;
import com.hhdb.csadmin.common.dao.ConnService;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.CSVUtil;
import com.hhdb.csadmin.common.util.HHSqlUtil;
import com.hhdb.csadmin.common.util.StartUtil;
import com.hh.frame.swingui.util.XmlFileUtil;
import com.hhdb.csadmin.plugin.cmd.console.CommonsHelper;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.been.MetaTreeNodeBean;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;
import com.hhdb.csadmin.plugin.tree.util.TreeNodeUtil;

/**
 * 处理树插件的业务逻辑
 * 
 * @author 胡圆锥
 * 
 */
public class TreeService {
	public String copySchemaName;
	public String copyTableName;
	public String copyViewName;
	public int copyViewId;
	public List<Map<String, Object>> copyfunctionlist;

	private HTree htree;

	public TreeService(HTree htree) {
		this.htree = htree;
	}

	/**
	 * 执行SQL语句
	 * 
	 * @param sql
	 * @throws Exception
	 */
	public void executeSql(String sql) throws Exception {
		String toID = "com.hhdb.csadmin.plugin.conn";
		CmdEvent event = new CmdEvent(htree.PLUGIN_ID, toID,
				"ExecuteUpdateBySqlEvent");
		event.addProp("sql_str", sql);
		HHEvent ev = htree.sendEvent(event);
		if (ev instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) ev).getErrorMessage());
		}
	}

	/**
	 * 执行DQL语句
	 * 
	 * @param sql
	 * @throws Exception
	 */
	public void executeDQL(String sql) throws Exception {
		String toID = "com.hhdb.csadmin.plugin.conn";
		CmdEvent event = new CmdEvent(htree.PLUGIN_ID, toID,
				"ExecuteQueryDQLBySqlEvent");
		event.addProp("sql_str", sql);
		HHEvent ev = htree.sendEvent(event);
		if (ev instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) ev).getErrorMessage());
		}
	}

	/**
	 * 执行SQL语句获得结果集
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public List<List<String>> getListStrBySql(String sql) throws Exception {
		String toID = "com.hhdb.csadmin.plugin.conn";
		CmdEvent event = new CmdEvent(htree.PLUGIN_ID, toID,
				"ExecuteCSVBySqlEvent");
		event.addProp("sql_str", sql);
		HHEvent ev = htree.sendEvent(event);
		if (ev instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) ev).getErrorMessage());
		}
		String csv = ev.getValue("csv");
		return CSVUtil.cSV2List(csv);
	}

	/**
	 * 执行SQL语句获得结果集
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getListMapBySql(String sql)
			throws Exception {
		String toID = "com.hhdb.csadmin.plugin.conn";
		CmdEvent event = new CmdEvent(htree.PLUGIN_ID, toID,
				"ExecuteListMapBySqlEvent");
		event.addProp("sql_str", sql);
		HHEvent ev = htree.sendEvent(event);
		if (ev instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) ev).getErrorMessage());
		}
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) ev
				.getObj();
		return list;
	}

	/**
	 * 判断表名在这个模式下是否存在，如果存在，构造一个新的不存在的表名返回，不存在就返回表名
	 * 
	 * @param tablename
	 * @param schemaId
	 * @return
	 * @throws Exception
	 */
	public String pdTableName(String tableName, int schemaId, int flag)
			throws Exception {
		String tablename;
		if (flag == 0) {
			tablename = tableName;
		} else {
			tablename = tableName + "_copy" + flag;
		}
		String s = "select count(*) from " + StartUtil.prefix
				+ "_class where relname='" + tablename + "' and relnamespace="
				+ schemaId;
		List<List<String>> list = getListStrBySql(s);
		if (!list.get(1).get(0).equals("0")) {
			flag++;
			return pdTableName(tableName, schemaId, flag);
		} else {
			return tablename;
		}
	}

	/**
	 * 获取模式的oid 和name
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getSchemaIdName() throws Exception {
		CmdEvent getsbEvent = new CmdEvent(htree.PLUGIN_ID,
				"com.hhdb.csadmin.plugin.conn", "GetServerBean");
		HHEvent revent = htree.sendEvent(getsbEvent);
		ServerBean serverbean = (ServerBean) revent.getObj();
		Connection conn = ConnService.createConnection(serverbean);

		SqlBean sb = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.SCHEMA,
				"prop_coll");
		List<Map<String, Object>> rs = SqlQueryUtil.select(conn, sb.getSql());
		ConnService.closeConn(conn);
		return rs;
	}

	/**
	 * 获取表的oid 和name
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getTableIdName(String schemaname)
			throws Exception {
		Object[] params = new Object[1];
		params[0] = "'" + schemaname + "'";

		SqlBean sb = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.TABLE,
				"prop_coll");
		List<Map<String, Object>> rs = getListMapBySql(sb.replaceParams(params));
		return rs;
	}

	/**
	 * 获取视图的oid 和name
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getViewIdName(String schemaname)
			throws Exception {
		Object[] params = new Object[1];
		params[0] = "'" + schemaname + "'";
		SqlBean sb = HHSqlUtil
				.getSqlBean(HHSqlUtil.ITEM_TYPE.VIEW, "prop_coll");
		List<Map<String, Object>> rs = getListMapBySql(sb.replaceParams(params));
		return rs;
	}

	/**
	 * 获取函数的oid 和name
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getFunctionIdName(String schemaname)
			throws Exception {
		Object[] params = new Object[1];
		params[0] = "'" + schemaname + "'";
		SqlBean sb = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.FUNCTION,
				"prop_coll");
		List<Map<String, Object>> rs = getListMapBySql(sb.replaceParams(params));
		return rs;
	}

	/**
	 * 获取序列的oid 和name
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getSequencesIdName(String schemaname)
			throws Exception {
		Object[] params = new Object[1];
		params[0] = "'" + schemaname + "'";
		SqlBean sb = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.SEQUENCE,
				"prop_coll");
		List<Map<String, Object>> rs = getListMapBySql(sb.replaceParams(params));
		return rs;
	}

	/**
	 * 获取类型的oid 和name
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getTypesIdName(int schemaId)
			throws Exception {
		Object[] params = new Object[2];
		params[0] = schemaId;
		params[1] = schemaId;
		SqlBean sb = HHSqlUtil
				.getSqlBean(HHSqlUtil.ITEM_TYPE.TYPE, "prop_coll");
		List<Map<String, Object>> rs = getListMapBySql(sb.replaceParams(params));
		return rs;
	}

	/**
	 * 发送给属性事件
	 * 
	 * @param csvuEventStr
	 *            属性值
	 * @param createStr
	 *            右下方显示sql
	 * @param ico
	 *            是否显示图片（字符串true显示图片）
	 */
	public void sendAttr(String csvuEventStr, String ico) {
		CmdEvent event = new CmdEvent(htree.PLUGIN_ID,
				"com.hhdb.csadmin.plugin.attribute", "Show");
		event.addProp("csvuEventStr", csvuEventStr);
		event.addProp("ico", ico);
		htree.sendEvent(event);
	}

	/**
	 * 发送创建语句给output插件
	 * 
	 * @param createStr
	 */
	public void sendCreateSql(String createStr) {
		CmdEvent event = new CmdEvent(htree.PLUGIN_ID,
				"com.hhdb.csadmin.plugin.output", "Show");
		event.addProp("createStr", createStr);
		htree.sendEvent(event);
	}

	/**
	 * 获取属性根据map集合转换成csv
	 * 
	 * @param map
	 * @return
	 * @throws IOException
	 */
	public String getAttrByMap(Map<String, String> map) throws IOException {
		if (map.size() > 0) {
			Collection<String[]> data = new ArrayList<>();
			String[] str1 = new String[2];
			str1[0] = "属性";
			str1[1] = "值";
			data.add(str1);

			for (String key : map.keySet()) {
				String[] str2 = new String[2];
				str2[0] = key;
				str2[1] = map.get(key);
				data.add(str2);
			}
			CsvWriter csvWriter = new CsvWriter();
			StringBuffer sbf = new StringBuffer();
			csvWriter.writeSbf(sbf, data);
			return sbf.toString();
		} else {
			return "";
		}
	}

	/**
	 * 根据sqlbean 获取csv 字符串(集合使用)
	 * 
	 * @param rs
	 * @param sb
	 * @return
	 * @throws Exception
	 */
	public String getcsvAttrCollection(List<Map<String, Object>> rs, SqlBean sb)
			throws Exception {
		if (rs.size() > 0) {
			Collection<String[]> data = new ArrayList<>();
			String[] str1 = new String[sb.getColumnKeys().size()];
			for (int i = 0; i < sb.getColumnKeys().size(); i++) {
				str1[i] = sb.getColumnNames().get(i);
			}
			data.add(str1);

			for (Map<String, Object> m : rs) {
				String[] str2 = new String[sb.getColumnKeys().size()];
				for (int i = 0; i < sb.getColumnKeys().size(); i++) {
					str2[i] = m.get(sb.getColumnKeys().get(i)) != null ? m.get(
							sb.getColumnKeys().get(i)).toString() : "";
				}
				data.add(str2);
			}

			CsvWriter csvWriter = new CsvWriter();
			StringBuffer sbf = new StringBuffer();
			csvWriter.writeSbf(sbf, data);
			return sbf.toString();
		} else {
			return "";
		}
	}

	/**
	 * 获取模式集合属性csv字符串
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getAttrCollectionSchema() throws Exception {
		SqlBean sb = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.SCHEMA,
				"prop_coll");
		List<Map<String, Object>> rs = getListMapBySql(sb.getSql());
		return getcsvAttrCollection(rs, sb);
	}

	/**
	 * 获取表集合属性csv字符串
	 * 
	 * @param schemaname
	 * @return
	 * @throws Exception
	 */
	public String getAttrCollectionTable(String schemaname) throws Exception {
		Object[] params = new Object[1];
		params[0] = "'" + schemaname + "'";
		SqlBean sb = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.TABLE,
				"prop_coll");
		List<Map<String, Object>> rs = getListMapBySql(sb.replaceParams(params));
		return getcsvAttrCollection(rs, sb);
	}

	/**
	 * 获取视图集合属性csv字符串
	 * 
	 * @param schemaname
	 * @return
	 * @throws Exception
	 */
	public String getAttrCollectionView(String schemaname) throws Exception {
		Object[] params = new Object[1];
		params[0] = "'" + schemaname + "'";
		SqlBean sb = HHSqlUtil
				.getSqlBean(HHSqlUtil.ITEM_TYPE.VIEW, "prop_coll");
		List<Map<String, Object>> rs = getListMapBySql(sb.replaceParams(params));
		return getcsvAttrCollection(rs, sb);
	}

	/**
	 * 获取函数集合属性csv字符串
	 * 
	 * @param schemaname
	 * @return
	 * @throws Exception
	 */
	public String getAttrCollectionFuction(String schemaname) throws Exception {
		Object[] params = new Object[1];
		params[0] = "'" + schemaname + "'";
		SqlBean sb = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.FUNCTION,
				"prop_coll");
		List<Map<String, Object>> rs = getListMapBySql(sb.replaceParams(params));
		return getcsvAttrCollection(rs, sb);
	}

	/**
	 * 获取类型集合属性csv字符串
	 * 
	 * @param schemaname
	 * @return
	 * @throws Exception
	 */
	public String getAttrCollectionTypes(int schemaoid) throws Exception {
		Object[] params = new Object[2];
		params[0] = schemaoid;
		params[1] = schemaoid;
		SqlBean sb = HHSqlUtil
				.getSqlBean(HHSqlUtil.ITEM_TYPE.TYPE, "prop_coll");
		List<Map<String, Object>> rs = getListMapBySql(sb.replaceParams(params));
		return getcsvAttrCollection(rs, sb);
	}

	/**
	 * 获取序列集合属性csv字符串
	 * 
	 * @param schemaname
	 * @return
	 * @throws Exception
	 */
	public String getAttrCollectionSequence(String schemaname) throws Exception {
		Object[] params = new Object[1];
		params[0] = "'" + schemaname + "'";
		SqlBean sb = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.SEQUENCE,
				"prop_coll");
		List<Map<String, Object>> rs = getListMapBySql(sb.replaceParams(params));
		return getcsvAttrCollection(rs, sb);
	}

	/**
	 * 获取表中列集合属性csv字符串
	 * 
	 * @param schemaname
	 * @return
	 * @throws Exception
	 */
	public String getAttrCollectionCol(int tableoid) throws Exception {
		Object[] params = new Object[1];
		params[0] = tableoid;
		SqlBean sb = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.COLUMNS,
				"prop_coll");
		List<Map<String, Object>> rs = getListMapBySql(sb.replaceParams(params));
		return getcsvAttrCollection(rs, sb);
	}

	/**
	 * 获取表中约束集合属性csv字符串
	 * 
	 * @param schemaname
	 * @return
	 * @throws Exception
	 */
	public String getAttrCollectionConstraints(int tableoid) throws Exception {
		Object[] params = new Object[1];
		params[0] = tableoid;
		SqlBean sb = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.CONSTRAINTS,
				"prop_coll");
		List<Map<String, Object>> rs = getListMapBySql(sb.replaceParams(params));
		return getcsvAttrCollection(rs, sb);
	}

	/**
	 * 获取表中索引集合属性csv字符串
	 * 
	 * @param schemaname
	 * @return
	 * @throws Exception
	 */
	public String getAttrCollectionIndex(int tableoid) throws Exception {
		Object[] params = new Object[1];
		params[0] = tableoid;
		SqlBean sb = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.INDEXS,
				"prop");
		List<Map<String, Object>> rs = getListMapBySql(sb.replaceParams(params));
		return getcsvAttrCollection(rs, sb);
	}

	/**
	 * \ 获取表中触发器集合属性csv字符串
	 * 
	 * @param schemaname
	 * @return
	 * @throws Exception
	 */
	public String getAttrCollectionTrigger(int tableoid) throws Exception {
		Object[] params = new Object[1];
		params[0] = tableoid;
		SqlBean sb = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.TABLE_TRIGGERS,
				"prop_coll");
		List<Map<String, Object>> rs = getListMapBySql(sb.replaceParams(params));
		return getcsvAttrCollection(rs, sb);
	}

	/**
	 * 获取表中规则集合属性csv字符串
	 * 
	 * @param schemaname
	 * @return
	 * @throws Exception
	 */
	public String getAttrCollectionRule(int tableoid) throws Exception {
		Object[] params = new Object[1];
		params[0] = tableoid;
		SqlBean sb = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.RULES,
				"prop_coll");
		List<Map<String, Object>> rs = getListMapBySql(sb.replaceParams(params));
		return getcsvAttrCollection(rs, sb);
	}

	/**
	 * 点击类型节点时获取属性csv字符串
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getAttrType(int schemaoid, int typeoid) throws Exception {
		Object[] params = new Object[42];
		params[0] = schemaoid;
		params[1] = typeoid;
		params[2] = schemaoid;
		params[3] = typeoid;
		SqlBean sb = HHSqlUtil
				.getSqlBean(HHSqlUtil.ITEM_TYPE.TYPE, "prop_item");
		List<Map<String, Object>> rs = getListMapBySql(sb.replaceParams(params));
		Map<String, String> map = new HashMap<String, String>();
		Map<String, Object> m = rs.get(0);
		for (int i = 0; i < sb.getColumnKeys().size(); i++) {
			map.put(sb.getColumnNames().get(i),
					m.get(sb.getColumnKeys().get(i)) != null ? m.get(
							sb.getColumnKeys().get(i)).toString() : "");
		}
		return getAttrByMap(map);
	}

	/**
	 * 点击类型节点时获取创建语句
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getCreateSqlType(String schemaName, int schemaoid, int typeoid)
			throws Exception {
		Object[] params = new Object[42];
		params[0] = schemaoid;
		params[1] = typeoid;
		params[2] = schemaoid;
		params[3] = typeoid;
		SqlBean sb = HHSqlUtil
				.getSqlBean(HHSqlUtil.ITEM_TYPE.TYPE, "prop_item");
		List<Map<String, Object>> rs = getListMapBySql(sb.replaceParams(params));
		Map<String, Object> m = rs.get(0);
		String result = "";
		String typeName = m.get("name").toString();
		String typeKind = m.get("type_kind").toString();
		String name = schemaName + ".\"" + typeName + "\"";

		if (typeKind.equalsIgnoreCase("c")) {
			result = getCompositeCode(typeoid, name);
		} else if (typeKind.equalsIgnoreCase("d")) {
			result = getbasicCode(typeoid, name);
		} else if (typeKind.equalsIgnoreCase("e")) {
			result = getEnumCode(typeoid, name);
		}
		return result;
	}

	private String getCompositeCode(int typeoid, String name) throws Exception {
		Object[] params = new Object[1];
		params[0] = typeoid;
		SqlBean sb = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.TYPE,
				"compositesource");
		List<Map<String, Object>> rs = getListMapBySql(sb.replaceParams(params));
		String result = "-- DROP TYPE " + name + ";\n";
		result = result + "\n";
		String atts = "";
		for (Map<String, Object> map : rs) {
			atts = atts + "\t" + (String) map.get("attname") + "\t"
					+ (String) map.get("datatype") + ",\n";
		}
		if (!atts.equals("")) {
			atts = atts.substring(0, atts.length() - 2);
			result = result + "CREATE TYPE " + name + " AS ( \n";
			result = result + atts + "\n)";
		}
		return result;
	}

	private String getbasicCode(int typeoid, String name) throws Exception {
		Object[] params = new Object[1];
		params[0] = typeoid;
		SqlBean sb = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.TYPE,
				"basicsource");
		List<Map<String, Object>> rs = getListMapBySql(sb.replaceParams(params));
		String result = "-- DROP DOMAIN " + name + ";\n";
		result = result + "\n";
		for (Map<String, Object> map : rs) {
			result = result + "CREATE DOMAIN " + name + " AS "
					+ map.get("base_type") + "\n";
			if ((String) map.get("typdefault") != null
					&& (String) map.get("typdefault") != "")
				result = result + "\tDEFAULT " + map.get("typdefault") + "\n";
			if (!(Boolean) map.get("typnotnull"))
				result = result + "\tNOT NULL\n";
			if (map.get("check") != null && map.get("check") != "")
				result = result + "\t" + map.get("check");
		}
		return result;
	}

	private String getEnumCode(int typeoid, String name) throws Exception {
		Object[] params = new Object[1];
		params[0] = typeoid;
		SqlBean sb = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.TYPE,
				"enumsource");
		List<Map<String, Object>> rs = getListMapBySql(sb.replaceParams(params));
		String result = "-- DROP TYPE " + name + ";\n";
		result = result + "\n";
		String enums = "";
		for (Map<String, Object> map : rs) {
			enums = enums + "'" + map.get("enumlabel") + "',";
		}
		if (!enums.equals("")) {
			enums = enums.substring(0, enums.length() - 1);
			result = result + "CREATE TYPE " + name + " AS ENUM \n";
			result = result + "\t(" + enums + ")";
		}
		return result;
	}

	/**
	 * 获得tree对象
	 * 
	 * @param xmlpath
	 *            相对于项目的相对路径
	 * @return
	 * @throws Exception
	 */
	public BaseTreeNode[] InitTreeNode(String xmlpath) throws Exception {
		Document doc = XmlFileUtil.getResXmlDoc(xmlpath);

		NodeList treeList = doc.getElementsByTagName("tree");
		Element treeElement = (Element) treeList.item(0);

		NodeList nodechidrens = treeElement.getChildNodes();
		List<BaseTreeNode> treenodelist = new ArrayList<BaseTreeNode>();

		if (nodechidrens.getLength() > 0) {
			for (int i = 0; i < nodechidrens.getLength(); i++) {
				if (nodechidrens.item(i).getNodeName().equals("node")) {
					Element nodechidren = (Element) nodechidrens.item(i);
					treenodelist.add(recursionTreeNode(null, nodechidren, null,
							null));
				}
			}
		}

		BaseTreeNode[] treenodes = new BaseTreeNode[treenodelist.size()];
		for (int i = 0; i < treenodelist.size(); i++) {
			treenodes[i] = treenodelist.get(i);
		}
		return treenodes;
	}

	/**
	 * 递归获得treeNode
	 * 
	 * @param parent
	 * @param node
	 * @return
	 * @throws Exception
	 */
	public BaseTreeNode recursionTreeNode(BaseTreeNode parent, Element node,
			String rename, String id) throws Exception {
		BaseTreeNode treenode = new BaseTreeNode(htree);
		MetaTreeNodeBean mtn = new MetaTreeNodeBean();
		mtn.setName(node.getAttribute("name"));
		mtn.setOpenIcon(node.getAttribute("open-icon"));
		mtn.setCloseIcon(node.getAttribute("close-icon"));
		mtn.setType(node.getAttribute("type"));
		mtn.setUnique(node.getAttribute("isUnique").equals("true") ? true
				: false);
		treenode.setParentBaseTreeNode(parent);

		if (mtn.getType().equals(TreeNodeUtil.DB_ITEM_TYPE)) {

			CmdEvent getsbEvent = new CmdEvent(htree.PLUGIN_ID,
					"com.hhdb.csadmin.plugin.conn", "GetServerBean");
			HHEvent revent = htree.sendEvent(getsbEvent);
			ServerBean serverbean = (ServerBean) revent.getObj();
			mtn.setName(serverbean.getDBName());

		} else if (mtn.getType().equals(TreeNodeUtil.SCHEMA_ITEM_TYPE)
				|| mtn.getType().equals(TreeNodeUtil.TABLE_ITEM_TYPE)) {
			mtn.setName(rename);
			mtn.setId(Integer.parseInt(id));
		}
		treenode.setMetaTreeNodeBean(mtn);
		NodeList nodechidrens = node.getChildNodes();
		if (mtn.getType().equals(TreeNodeUtil.SCHEMA_TYPE)) {
			Element nodechidren = null;
			for (int i = 0; i < nodechidrens.getLength(); i++) {
				if (nodechidrens.item(i).getNodeName().equals("node")) {
					nodechidren = (Element) nodechidrens.item(i);
					break;
				}
			}
			List<Map<String, Object>> schemaNames = getSchemaIdName();
			for (Map<String, Object> m : schemaNames) {
				treenode.addChildNode(recursionTreeNode(treenode, nodechidren,
						m.get("name").toString(), m.get("id").toString()));
			}
		} else if (mtn.getType().equals(TreeNodeUtil.TABLE_TYPE)) {
		} else {
			if (nodechidrens.getLength() > 0) {
				for (int i = 0; i < nodechidrens.getLength(); i++) {
					if (nodechidrens.item(i).getNodeName().equals("node")) {
						Element nodechidren = (Element) nodechidrens.item(i);
						treenode.addChildNode(recursionTreeNode(treenode,
								nodechidren, null, null));
					}
				}
			}
		}
		return treenode;
	}

	/**
	 * 刷新数据库集合
	 * 
	 * @param treeNode
	 * @throws Exception
	 */
	public void refreshDBCollection(BaseTreeNode treeNode) throws Exception {
		try {
			Connection conn = null;
			CmdEvent getconnEvent = new CmdEvent(htree.PLUGIN_ID,
					"com.hhdb.csadmin.plugin.conn", "GetConn");
			HHEvent refevent = htree.sendEvent(getconnEvent);
			if (!(refevent instanceof ErrorEvent)) {
				conn = (Connection) refevent.getObj();
			}
			HHdbSession hs = new HHdbSession(conn, StartUtil.prefix);
			Set<String> dbnames = hs.getUserDbNames();
			treeNode.removeAllChildren();
			for (String name : dbnames) {
				BaseTreeNode dbtreenode = new BaseTreeNode(htree);
				MetaTreeNodeBean dbmtn = new MetaTreeNodeBean();
				dbmtn.setName(name);
				dbmtn.setOpenIcon("database.png");
				dbmtn.setType(TreeNodeUtil.DB_ITEM_TYPE);
				dbmtn.setUnique(false);
				dbtreenode.setMetaTreeNodeBean(dbmtn);
				dbtreenode.setParentBaseTreeNode(treeNode);
				// ---------------------------------------
				BaseTreeNode extendtreenode = new BaseTreeNode(htree);
				MetaTreeNodeBean extendmtn = new MetaTreeNodeBean();
				extendmtn.setName("扩展");
				extendmtn.setOpenIcon("extend.png");
				extendmtn.setType(TreeNodeUtil.EXTENSION_TYPE);
				extendmtn.setUnique(false);
				extendtreenode.setMetaTreeNodeBean(extendmtn);
				extendtreenode.setParentBaseTreeNode(dbtreenode);
				dbtreenode.addChildNode(extendtreenode);
				// ---------------------------------------
				treeNode.addChildNode(dbtreenode);
			}
			// 加上这两句是可以让节点刷新后直接展开
			TreePath path = new TreePath(htree.getTree().getTreeModel()
					.getPathToRoot(treeNode));
			htree.getTree().expandPath(path);
			// 重新加载节点
			htree.getTree().getTreeModel().reload(treeNode);

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 刷新表空间集合
	 * 
	 * @param treeNode
	 * @throws Exception
	 */
	public void refreshTablespaceCollection(BaseTreeNode treeNode)
			throws Exception {
		try {
			SqlBean sb = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.TABLESPACE,
					"prop_coll");
			List<Map<String, Object>> rs = getListMapBySql(sb.getSql());
			treeNode.removeAllChildren();
			for (Map<String, Object> m : rs) {
				BaseTreeNode tn = new BaseTreeNode(htree);
				MetaTreeNodeBean mtn = new MetaTreeNodeBean();
				mtn.setName(m.get("spcname").toString());
				mtn.setOpenIcon("tablespace.png");
				mtn.setType(TreeNodeUtil.TAB_SPACE_ITEM_TYPE);
				mtn.setUnique(false);
				tn.setMetaTreeNodeBean(mtn);
				tn.setParentBaseTreeNode(treeNode);

				treeNode.addChildNode(tn);
			}
			// 加上这两句是可以让节点刷新后直接展开
			TreePath path = new TreePath(htree.getTree().getTreeModel()
					.getPathToRoot(treeNode));
			htree.getTree().expandPath(path);
			// 重新加载节点
			htree.getTree().getTreeModel().reload(treeNode);

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 刷新用户集合
	 * 
	 * @param treeNode
	 * @throws Exception
	 */
	public void refreshUserCollection(BaseTreeNode treeNode) throws Exception {
		try {
			SqlBean sb = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.USER,
					"prop_coll");
			List<Map<String, Object>> rs = getListMapBySql(sb.getSql());
			treeNode.removeAllChildren();
			for (Map<String, Object> m : rs) {
				BaseTreeNode tn = new BaseTreeNode(htree);
				MetaTreeNodeBean mtn = new MetaTreeNodeBean();
				mtn.setName(m.get("rolname").toString());
				mtn.setId(Integer.parseInt(m.get("id").toString()));
				mtn.setOpenIcon("quser.png");
				mtn.setType(TreeNodeUtil.LOGIN_ROLE_ITEM_TYPE);
				mtn.setUnique(false);
				tn.setMetaTreeNodeBean(mtn);
				tn.setParentBaseTreeNode(treeNode);

				treeNode.addChildNode(tn);
			}
			// 加上这两句是可以让节点刷新后直接展开
			TreePath path = new TreePath(htree.getTree().getTreeModel()
					.getPathToRoot(treeNode));
			htree.getTree().expandPath(path);
			// 重新加载节点
			htree.getTree().getTreeModel().reload(treeNode);

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 刷新模式集合
	 * 
	 * @param treeNode
	 * @throws Exception
	 */
	public void refreshSchemaCollection(BaseTreeNode treeNode) throws Exception {
		try {
			List<Map<String, Object>> schemaidnames = getSchemaIdName();
			treeNode.removeAllChildren();
			for (Map<String, Object> m : schemaidnames) {
				BaseTreeNode schematreenode = new BaseTreeNode(htree);
				MetaTreeNodeBean schemamtn = new MetaTreeNodeBean();
				schemamtn.setName(m.get("name").toString());
				schemamtn.setId(Integer.parseInt(m.get("id").toString()));
				schemamtn.setOpenIcon("schema.png");
				schemamtn.setType(TreeNodeUtil.SCHEMA_ITEM_TYPE);
				schemamtn.setUnique(false);
				schematreenode.setMetaTreeNodeBean(schemamtn);
				schematreenode.setParentBaseTreeNode(treeNode);

				treeNode.addChildNode(schematreenode);
			}
			// 加上这两句是可以让节点刷新后直接展开
			TreePath path = new TreePath(htree.getTree().getTreeModel()
					.getPathToRoot(treeNode));
			htree.getTree().expandPath(path);
			// 重新加载节点
			htree.getTree().getTreeModel().reload(treeNode);

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 刷新模式
	 * 
	 * @param treeNode
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public void refreshSchema(BaseTreeNode treeNode) throws URISyntaxException,
			ParserConfigurationException, SAXException, IOException {
		Document doc = XmlFileUtil
				.getResXmlDoc("com/hhdb/csadmin/plugin/tree/xml/tree_schema.xml");

		NodeList treeList = doc.getElementsByTagName("treenode");
		Element treeElement = (Element) treeList.item(0);

		NodeList nodechidrens = treeElement.getChildNodes();

		if (nodechidrens.getLength() > 0) {
			treeNode.removeAllChildren();
			for (int i = 0; i < nodechidrens.getLength(); i++) {
				if (nodechidrens.item(i).getNodeName().equals("node")) {
					Element nodechidren = (Element) nodechidrens.item(i);

					BaseTreeNode tmptreenode = new BaseTreeNode(htree);
					MetaTreeNodeBean mtn = new MetaTreeNodeBean();
					mtn.setName(nodechidren.getAttribute("name"));
					mtn.setOpenIcon(nodechidren.getAttribute("open-icon"));
					mtn.setCloseIcon(nodechidren.getAttribute("close-icon"));
					mtn.setType(nodechidren.getAttribute("type"));
					mtn.setUnique(nodechidren.getAttribute("isUnique").equals(
							"true") ? true : false);
					tmptreenode.setParentBaseTreeNode(treeNode);
					tmptreenode.setMetaTreeNodeBean(mtn);

					treeNode.addChildNode(tmptreenode);
				}
			}
		}
		// 加上这两句是可以让节点刷新后直接展开
		TreePath path = new TreePath(htree.getTree().getTreeModel()
				.getPathToRoot(treeNode));
		htree.getTree().expandPath(path);
		// 重新加载节点
		htree.getTree().getTreeModel().reload(treeNode);
	}

	/**
	 * 刷新表
	 * 
	 * @param treeNode
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void refreshTable(BaseTreeNode treeNode) throws Exception {
		Document doc = XmlFileUtil
				.getResXmlDoc("com/hhdb/csadmin/plugin/tree/xml/tree_table.xml");

		NodeList treeList = doc.getElementsByTagName("treenode");
		Element treeElement = (Element) treeList.item(0);

		NodeList nodechidrens = treeElement.getChildNodes();

		try {
			List<Map<String, Object>> tableIdNames = getTableIdName(((BaseTreeNode) treeNode
					.getParentBaseTreeNode()).getMetaTreeNodeBean().getName());
			treeNode.removeAllChildren();
			for (Map<String, Object> m : tableIdNames) {
				BaseTreeNode tabletreenode = new BaseTreeNode(htree);
				MetaTreeNodeBean tablemtn = new MetaTreeNodeBean();
				tablemtn.setName(m.get("name").toString());
				tablemtn.setId(Integer.parseInt(m.get("id").toString()));
				tablemtn.setOpenIcon("poptables.png");
				tablemtn.setType(TreeNodeUtil.TABLE_ITEM_TYPE);
				tablemtn.setUnique(false);
				tabletreenode.setMetaTreeNodeBean(tablemtn);
				tabletreenode.setParentBaseTreeNode(treeNode);
				if (nodechidrens.getLength() > 0) {
					for (int i = 0; i < nodechidrens.getLength(); i++) {
						if (nodechidrens.item(i).getNodeName().equals("node")) {
							Element nodechidren = (Element) nodechidrens
									.item(i);

							BaseTreeNode tmptreenode = new BaseTreeNode(htree);
							MetaTreeNodeBean mtn = new MetaTreeNodeBean();
							mtn.setName(nodechidren.getAttribute("name"));
							mtn.setOpenIcon(nodechidren
									.getAttribute("open-icon"));
							mtn.setCloseIcon(nodechidren
									.getAttribute("close-icon"));
							mtn.setType(nodechidren.getAttribute("type"));
							mtn.setUnique(nodechidren.getAttribute("isUnique")
									.equals("true") ? true : false);
							tmptreenode.setParentBaseTreeNode(tabletreenode);
							tmptreenode.setMetaTreeNodeBean(mtn);

							tabletreenode.addChildNode(tmptreenode);
						}
					}
				}
				treeNode.addChildNode(tabletreenode);
			}
			// 加上这两句是可以让节点刷新后直接展开
			TreePath path = new TreePath(htree.getTree().getTreeModel()
					.getPathToRoot(treeNode));
			htree.getTree().expandPath(path);
			// 重新加载节点
			htree.getTree().getTreeModel().reload(treeNode);

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 刷新视图
	 * 
	 * @param treeNode
	 */
	public void refreshView(BaseTreeNode treeNode) throws Exception {
		try {
			List<Map<String, Object>> viewidnames = getViewIdName(((BaseTreeNode) treeNode
					.getParentBaseTreeNode()).getMetaTreeNodeBean().getName());

			treeNode.removeAllChildren();
			for (Map<String, Object> m : viewidnames) {
				BaseTreeNode viewtreenode = new BaseTreeNode(htree);
				MetaTreeNodeBean tablemtn = new MetaTreeNodeBean();
				tablemtn.setName(m.get("name").toString());
				tablemtn.setId(Integer.parseInt(m.get("id").toString()));
				tablemtn.setOpenIcon("view.png");
				tablemtn.setType(TreeNodeUtil.VIEW_ITEM_TYPE);
				tablemtn.setUnique(false);
				viewtreenode.setMetaTreeNodeBean(tablemtn);
				viewtreenode.setParentBaseTreeNode(treeNode);

				treeNode.addChildNode(viewtreenode);
			}
			// 加上这两句是可以让节点刷新后直接展开
			TreePath path = new TreePath(htree.getTree().getTreeModel()
					.getPathToRoot(treeNode));
			htree.getTree().expandPath(path);
			// 重新加载节点
			htree.getTree().getTreeModel().reload(treeNode);

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 刷新函数
	 * 
	 * @param treeNode
	 */
	public void refreshFunction(BaseTreeNode treeNode) throws Exception {
		try {
			List<Map<String, Object>> functionidnames = getFunctionIdName(((BaseTreeNode) treeNode
					.getParentBaseTreeNode()).getMetaTreeNodeBean().getName());

			treeNode.removeAllChildren();
			for (Map<String, Object> m : functionidnames) {
				BaseTreeNode functiontreenode = new BaseTreeNode(htree);
				MetaTreeNodeBean tablemtn = new MetaTreeNodeBean();
				tablemtn.setName(m.get("proname").toString() + "("
						+ m.get("arguments").toString() + ")");
				tablemtn.setId(Integer.parseInt(m.get("id").toString()));
				tablemtn.setOpenIcon("function.png");
				tablemtn.setType(TreeNodeUtil.FUN_ITEM_TYPE);
				tablemtn.setUnique(false);
				functiontreenode.setMetaTreeNodeBean(tablemtn);
				functiontreenode.setParentBaseTreeNode(treeNode);

				treeNode.addChildNode(functiontreenode);
			}
			// 加上这两句是可以让节点刷新后直接展开
			TreePath path = new TreePath(htree.getTree().getTreeModel()
					.getPathToRoot(treeNode));
			htree.getTree().expandPath(path);
			// 重新加载节点
			htree.getTree().getTreeModel().reload(treeNode);

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 刷新序列
	 * 
	 * @param treeNode
	 */
	public void refreshSequence(BaseTreeNode treeNode) throws Exception {
		try {
			List<Map<String, Object>> sequenceidnames = getSequencesIdName(((BaseTreeNode) treeNode
					.getParentBaseTreeNode()).getMetaTreeNodeBean().getName());

			treeNode.removeAllChildren();
			for (Map<String, Object> m : sequenceidnames) {
				BaseTreeNode sequencetreenode = new BaseTreeNode(htree);
				MetaTreeNodeBean tablemtn = new MetaTreeNodeBean();
				tablemtn.setName(m.get("name").toString());
				tablemtn.setId(Integer.parseInt(m.get("id").toString()));
				tablemtn.setOpenIcon("sequence.png");
				tablemtn.setType(TreeNodeUtil.SEQ_ITEM_TYPE);
				tablemtn.setUnique(false);
				sequencetreenode.setMetaTreeNodeBean(tablemtn);
				sequencetreenode.setParentBaseTreeNode(treeNode);

				treeNode.addChildNode(sequencetreenode);
			}
			// 加上这两句是可以让节点刷新后直接展开
			TreePath path = new TreePath(htree.getTree().getTreeModel()
					.getPathToRoot(treeNode));
			htree.getTree().expandPath(path);
			// 重新加载节点
			htree.getTree().getTreeModel().reload(treeNode);

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 刷新类型
	 * 
	 * @param treeNode
	 */
	public void refreshType(BaseTreeNode treeNode) throws Exception {
		try {
			List<Map<String, Object>> typeidnames = getTypesIdName(((BaseTreeNode) treeNode
					.getParentBaseTreeNode()).getMetaTreeNodeBean().getId());

			treeNode.removeAllChildren();
			for (Map<String, Object> m : typeidnames) {
				BaseTreeNode typetreenode = new BaseTreeNode(htree);
				MetaTreeNodeBean tablemtn = new MetaTreeNodeBean();
				tablemtn.setName(m.get("name").toString());
				tablemtn.setId(Integer.parseInt(m.get("id").toString()));
				tablemtn.setOpenIcon("dbtype.png");
				tablemtn.setType(TreeNodeUtil.TYPE_ITEM_TYPE);
				tablemtn.setUnique(false);
				typetreenode.setMetaTreeNodeBean(tablemtn);
				typetreenode.setParentBaseTreeNode(treeNode);

				treeNode.addChildNode(typetreenode);
			}
			// 加上这两句是可以让节点刷新后直接展开
			TreePath path = new TreePath(htree.getTree().getTreeModel()
					.getPathToRoot(treeNode));
			htree.getTree().expandPath(path);
			// 重新加载节点
			htree.getTree().getTreeModel().reload(treeNode);

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 刷新查询
	 * 
	 * @param treeNode
	 */
	public void refreshQuery(BaseTreeNode treeNode) throws Exception {
		try {
			CmdEvent getsbEvent = new CmdEvent(htree.PLUGIN_ID,
					"com.hhdb.csadmin.plugin.conn", "GetServerBean");
			HHEvent revent = htree.sendEvent(getsbEvent);
			ServerBean serverbean = (ServerBean) revent.getObj();

			File file = new File(CommonsHelper.getClassPath()
					+ "/db/servers/"
					+ serverbean.getHost()
					+ "/"
					+ serverbean.getDBName()
					+ "/"
					+ treeNode.getParentBaseTreeNode().getMetaTreeNodeBean()
							.getName() + "/");

			if (!file.exists()) { //
				file.mkdirs();
			}
			treeNode.removeAllChildren();
			for (File fl : file.listFiles()) {
				String name = fl.getName().substring(0,
						fl.getName().indexOf('.'));
				BaseTreeNode typetreenode = new BaseTreeNode(htree);
				MetaTreeNodeBean tablemtn = new MetaTreeNodeBean();
				tablemtn.setName(name);
				tablemtn.setOpenIcon("querys.png");
				tablemtn.setType(TreeNodeUtil.SELECT_ITEM_TYPE);
				tablemtn.setUnique(false);
				typetreenode.setMetaTreeNodeBean(tablemtn);
				typetreenode.setParentBaseTreeNode(treeNode);
				treeNode.addChildNode(typetreenode);
			}
			// 加上这两句是可以让节点刷新后直接展开
			TreePath path = new TreePath(htree.getTree().getTreeModel()
					.getPathToRoot(treeNode));
			htree.getTree().expandPath(path);
			// 重新加载节点
			htree.getTree().getTreeModel().reload(treeNode);

		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 刷新约束集合
	 * 
	 * @param treeNode
	 */
	public void refreshConstraint(BaseTreeNode treeNode) throws Exception {
		try {
			HHdbTable ht = (HHdbTable)treeNode.getParentBaseTreeNode().getNodeObject();
			treeNode.removeAllChildren();
			String pkName = ht.getPkName();
			Set<String> fkNames = ht.getFkNameSet();
			Set<String> ukNames = ht.getUkNameSet();
			Set<String> ckNames = ht.getCkNameSet();
			Set<String> xkNames = ht.getXkNameSet();
			for (String name : fkNames) {
				BaseTreeNode childtreenode = new BaseTreeNode(htree);
				MetaTreeNodeBean childemtn = new MetaTreeNodeBean();
				childemtn.setName(name);
				childemtn.setOpenIcon("constraints.png");
				childemtn.setType(TreeNodeUtil.CONSTRAINT_FK_ITEM_TYPE);
				childemtn.setUnique(false);
				childtreenode.setMetaTreeNodeBean(childemtn);
				childtreenode.setParentBaseTreeNode(treeNode);
				treeNode.addChildNode(childtreenode);
			}
			for (String name : ukNames) {
				BaseTreeNode childtreenode = new BaseTreeNode(htree);
				MetaTreeNodeBean childemtn = new MetaTreeNodeBean();
				childemtn.setName(name);
				childemtn.setOpenIcon("constraints.png");
				childemtn.setType(TreeNodeUtil.CONSTRAINT_UK_ITEM_TYPE);
				childemtn.setUnique(false);
				childtreenode.setMetaTreeNodeBean(childemtn);
				childtreenode.setParentBaseTreeNode(treeNode);
				treeNode.addChildNode(childtreenode);
			}
			for (String name : ckNames) {
				BaseTreeNode childtreenode = new BaseTreeNode(htree);
				MetaTreeNodeBean childemtn = new MetaTreeNodeBean();
				childemtn.setName(name);
				childemtn.setOpenIcon("constraints.png");
				childemtn.setType(TreeNodeUtil.CONSTRAINT_CK_ITEM_TYPE);
				childemtn.setUnique(false);
				childtreenode.setMetaTreeNodeBean(childemtn);
				childtreenode.setParentBaseTreeNode(treeNode);
				treeNode.addChildNode(childtreenode);
			}
			for (String name : xkNames) {
				BaseTreeNode childtreenode = new BaseTreeNode(htree);
				MetaTreeNodeBean childemtn = new MetaTreeNodeBean();
				childemtn.setName(name);
				childemtn.setOpenIcon("constraints.png");
				childemtn.setType(TreeNodeUtil.CONSTRAINT_XK_ITEM_TYPE);
				childemtn.setUnique(false);
				childtreenode.setMetaTreeNodeBean(childemtn);
				childtreenode.setParentBaseTreeNode(treeNode);
				treeNode.addChildNode(childtreenode);
			}
			if(pkName!=null && !pkName.trim().isEmpty()){
				BaseTreeNode childtreenode = new BaseTreeNode(htree);
				MetaTreeNodeBean childemtn = new MetaTreeNodeBean();
				childemtn.setName(pkName);
				childemtn.setOpenIcon("constraints.png");
				childemtn.setType(TreeNodeUtil.CONSTRAINT_PK_ITEM_TYPE);
				childemtn.setUnique(false);
				childtreenode.setMetaTreeNodeBean(childemtn);
				childtreenode.setParentBaseTreeNode(treeNode);
				treeNode.addChildNode(childtreenode);
			}
			// 加上这两句是可以让节点刷新后直接展开
			TreePath path = new TreePath(htree.getTree().getTreeModel()
					.getPathToRoot(treeNode));
			htree.getTree().expandPath(path);
			// 重新加载节点
			htree.getTree().getTreeModel().reload(treeNode);

		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 刷新索引集合
	 * 
	 * @param treeNode
	 */
	public void refreshIndex(BaseTreeNode treeNode) throws Exception {
		try {
			HHdbTable ht = (HHdbTable)treeNode.getParentBaseTreeNode().getNodeObject();
			Set<String> indexsNames = ht.getIndexNameSet();
			treeNode.removeAllChildren();
			for (String name : indexsNames) {
				BaseTreeNode childtreenode = new BaseTreeNode(htree);
				MetaTreeNodeBean childemtn = new MetaTreeNodeBean();
				childemtn.setName(name);
				childemtn.setOpenIcon("indexs.png");
				childemtn.setType(TreeNodeUtil.INDEX_ITEM_TYPE);
				childemtn.setUnique(false);
				childtreenode.setMetaTreeNodeBean(childemtn);
				childtreenode.setParentBaseTreeNode(treeNode);
				treeNode.addChildNode(childtreenode);
			}
			// 加上这两句是可以让节点刷新后直接展开
			TreePath path = new TreePath(htree.getTree().getTreeModel()
					.getPathToRoot(treeNode));
			htree.getTree().expandPath(path);
			// 重新加载节点
			htree.getTree().getTreeModel().reload(treeNode);

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据treeNode获取此节点所在的模式节点的MataTreeNodeBean
	 * 
	 * @param treeNode
	 * @return
	 */
	public MetaTreeNodeBean getSchemaMetaTreeNodeBean(BaseTreeNode treeNode) {
		if (treeNode.getType().equals(TreeNodeUtil.SCHEMA_ITEM_TYPE)) {
			return treeNode.getMetaTreeNodeBean();
		}
		if (treeNode.getParentBaseTreeNode() == null) {
			return null;
		} else {
			return getSchemaMetaTreeNodeBean(treeNode.getParentBaseTreeNode());
		}
	}

	/**
	 * 根据模式名和需要刷新的节点类型，来刷新树节点
	 * 
	 * @param schemaName
	 * @param type
	 * @throws Exception
	 */
	public void refreshTreeNodeBySchemaName(String schemaName, String type)
			throws Exception {
		BaseTreeNode rootTreeNode = htree.getTree().getRootTreeNode();
		BaseTreeNode tempSchemaNode = null;

		if (type.trim().equalsIgnoreCase("database")) {
			for (BaseTreeNode tNode : rootTreeNode.getChildNode()) {
				if (tNode.getType().trim().equals(TreeNodeUtil.DB_TYPE)) {
					refreshDBCollection(tNode);
					break;
				}
			}
			return;
		}
		if (type.trim().equalsIgnoreCase("tablespace")) {
			for (BaseTreeNode tNode : rootTreeNode.getChildNode()) {
				if (tNode.getType().trim().equals(TreeNodeUtil.TAB_SPACE_TYPE)) {
					refreshTablespaceCollection(tNode);
					break;
				}
			}
			return;
		}
		if (type.trim().equalsIgnoreCase("user")) {
			for (BaseTreeNode tNode : rootTreeNode.getChildNode()) {
				if (tNode.getType().trim().equals(TreeNodeUtil.LOGIN_ROLE_TYPE)) {
					refreshUserCollection(tNode);
					break;
				}
			}
			return;
		}
		e: for (BaseTreeNode dbNode : rootTreeNode.getChildNode()) {
			if (dbNode.getType().trim().equals(TreeNodeUtil.DB_ITEM_TYPE)) {
				for (BaseTreeNode schemasNode : dbNode.getChildNode()) {
					if (schemasNode.getType().trim()
							.equals(TreeNodeUtil.SCHEMA_TYPE)) {
						for (BaseTreeNode schemaNode : schemasNode
								.getChildNode()) {
							if (schemaNode.getMetaTreeNodeBean().getName()
									.trim().equals(schemaName)) {
								tempSchemaNode = schemaNode;
								break e;
							}
						}
					}
				}
			}
		}
		if (tempSchemaNode != null) {
			if (type.trim().equalsIgnoreCase("schema")) {
				refreshSchemaCollection(tempSchemaNode.getParentBaseTreeNode());
				return;
			} else if (type.trim().equalsIgnoreCase("table")) {
				for (BaseTreeNode tableTreeNode : tempSchemaNode.getChildNode()) {
					if (tableTreeNode.getType().equalsIgnoreCase(
							TreeNodeUtil.TABLE_TYPE)) {
						refreshTable(tableTreeNode);
						return;
					}
				}
			} else if (type.trim().equalsIgnoreCase("view")) {
				for (BaseTreeNode viewTreeNode : tempSchemaNode.getChildNode()) {
					if (viewTreeNode.getType().equalsIgnoreCase(
							TreeNodeUtil.VIEW_TYPE)) {
						refreshView(viewTreeNode);
						return;
					}
				}
			} else if (type.trim().equalsIgnoreCase("function")) {
				for (BaseTreeNode functionTreeNode : tempSchemaNode
						.getChildNode()) {
					if (functionTreeNode.getType().equalsIgnoreCase(
							TreeNodeUtil.FUN_TYPE)) {
						refreshFunction(functionTreeNode);
						return;
					}
				}
			} else if (type.trim().equalsIgnoreCase("sequence")) {
				for (BaseTreeNode sequenceTreeNode : tempSchemaNode
						.getChildNode()) {
					if (sequenceTreeNode.getType().equalsIgnoreCase(
							TreeNodeUtil.SEQ_TYPE)) {
						refreshSequence(sequenceTreeNode);
						return;
					}
				}
			} else if (type.trim().equalsIgnoreCase("type")) {
				for (BaseTreeNode typeTreeNode : tempSchemaNode.getChildNode()) {
					if (typeTreeNode.getType().equalsIgnoreCase(
							TreeNodeUtil.TYPE_TYPE)) {
						refreshType(typeTreeNode);
						return;
					}
				}
			} else if (type.trim().equalsIgnoreCase("query")) {
				for (BaseTreeNode queryTreeNode : tempSchemaNode.getChildNode()) {
					if (queryTreeNode.getType().equalsIgnoreCase(
							TreeNodeUtil.SELECT_TYPE)) {
						refreshQuery(queryTreeNode);
						return;
					}
				}
			}
		}
	}

	/**
	 * 刷新工具栏
	 */
	public void refreshToolBar(String showBar) {
		String[] toolButtonNames = { "库监控", "CPU监控", "内存监控", "硬盘监控", "网络监控" };
		for (String bname : toolButtonNames) {
			CmdEvent toolbarShowEvent = new CmdEvent(htree.PLUGIN_ID,
					"com.hhdb.csadmin.plugin.tool_bar", "ToolbarShowEvent");
			toolbarShowEvent.addProp("toolbarName", bname);
			toolbarShowEvent.addProp("showBar", showBar);
			htree.sendEvent(toolbarShowEvent);
		}

	}

	/**
	 * 查询器执行drop操作，来刷新树节点
	 * 
	 * @param schemaName
	 * @param type
	 * @throws Exception
	 */
	public void refreshTreeNodeByQueryDrop() throws Exception {
		BaseTreeNode rootTreeNode = htree.getTree().getRootTreeNode();
		List<BaseTreeNode> tempSchemaNodes = new ArrayList<BaseTreeNode>();

		for (BaseTreeNode dbNode : rootTreeNode.getChildNode()) {
			if (dbNode.getType().trim().equals(TreeNodeUtil.DB_ITEM_TYPE)) {
				for (BaseTreeNode schemasNode : dbNode.getChildNode()) {
					if (schemasNode.getType().trim()
							.equals(TreeNodeUtil.SCHEMA_TYPE)) {
						for (BaseTreeNode schemaNode : schemasNode
								.getChildNode()) {
							tempSchemaNodes.add(schemaNode);
						}
					}
				}
			}
		}
		for (BaseTreeNode tempSchemaNode : tempSchemaNodes) {

			for (BaseTreeNode treeNode : tempSchemaNode.getChildNode()) {
				if (treeNode.getType()
						.equalsIgnoreCase(TreeNodeUtil.TABLE_TYPE)) {
					if (!htree.getTree().isExpanded(
							new TreePath(treeNode.getPath()))) {
						treeNode.removeAllChildren();
					} else {
						refreshTable(treeNode);
					}
				}

				if (treeNode.getType().equalsIgnoreCase(TreeNodeUtil.VIEW_TYPE)) {
					if (!htree.getTree().isExpanded(
							new TreePath(treeNode.getPath()))) {
						treeNode.removeAllChildren();
					} else {
						refreshView(treeNode);
					}

				}

				if (treeNode.getType().equalsIgnoreCase(TreeNodeUtil.FUN_TYPE)) {
					if (!htree.getTree().isExpanded(
							new TreePath(treeNode.getPath()))) {
						treeNode.removeAllChildren();
					} else {
						refreshFunction(treeNode);
					}

				}

				if (treeNode.getType().equalsIgnoreCase(TreeNodeUtil.SEQ_TYPE)) {
					if (!htree.getTree().isExpanded(
							new TreePath(treeNode.getPath()))) {
						treeNode.removeAllChildren();
					} else {
						refreshSequence(treeNode);
					}

				}

				if (treeNode.getType().equalsIgnoreCase(TreeNodeUtil.TYPE_TYPE)) {
					if (!htree.getTree().isExpanded(
							new TreePath(treeNode.getPath()))) {
						treeNode.removeAllChildren();
					} else {
						refreshType(treeNode);
					}

				}
			}
		}
	}
}
