package com.hhdb.csadmin.common.util;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import com.hh.frame.common.log.LM;
import com.hhdb.csadmin.common.bean.SqlBean;
import com.hhdb.csadmin.common.util.commonxmltool.NodeEntity;
import com.hhdb.csadmin.common.util.commonxmltool.XmlEnger;
/**
 * hh-sql.xml帮忙类
 * 
 * @author hh
 *
 */
public class HHSqlUtil {

	public static enum ITEM_TYPE {
		TABLE, VIEW, FOREIGN_TABLE, FUNCTION, SEQUENCE, TYPE, MATERIALIZED_VIEW, DATABASE, CONNECTIONS, SQLBIBLE, CATEGORYS, ROLE, USER, TABLESPACE, TRIGGER, SCHEMA, DBSTATS, SQLHISTORY, DATASERVICE, SOURCE, DBINITSERVICE, COLUMNS, CONSTRAINTS, 
		INDEXS, RULES, TABLE_TRIGGERS, SERVER_STATUS,FITTABLE,UNQIUE,CHECK,FOREIGN,DBSERVER,EXTEND,LOCK_STATUS,AUTHORIZATION
	}

	public static String configPath = StartUtil.prefix+"-sql.xml";
	private static XmlEnger enger = new XmlEnger();

	static {
		try {
	        String xmlPath = StringUtil.getXmlPath(configPath);
			enger.setXmlFile(new File(URLDecoder.decode(xmlPath,"utf-8")));
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
			throw new RuntimeException("加载[" + configPath + "]出错！", e);
		}
	}

	public static SqlBean getSqlBean(ITEM_TYPE type, String sqlType) {
		switch (type) {
		case TABLE:
			return getSqLBean("table", sqlType);
		case VIEW:
			return getSqLBean("view", sqlType);
		case FUNCTION:
			return getSqLBean("functions", sqlType);
		case TRIGGER:
			return getSqLBean("trigger", sqlType);
		case SEQUENCE:
			return getSqLBean("sequences", sqlType);
		case TYPE:
			return getSqLBean("types", sqlType);
		case DATABASE:
			return getSqLBean("database", sqlType);
		case CONNECTIONS:
			return getSqLBean("connections", sqlType);
		case SQLBIBLE:
			return getSqLBean("sqlbible", sqlType);
		case CATEGORYS:
			return getSqLBean("categorys", sqlType);
		case ROLE:
			return getSqLBean("role", sqlType);
		case USER:
			return getSqLBean("user", sqlType);
		case TABLESPACE:
			return getSqLBean("tablespace", sqlType);
		case SCHEMA:
			return getSqLBean("schema", sqlType);
		case DBSTATS:
			return getSqLBean("dbstats", sqlType);
		case SQLHISTORY:
			return getSqLBean("sqlhistory", sqlType);
		case DATASERVICE:
			return getSql("dataservice", sqlType);
		case SOURCE:
			return getSqLBean("source", sqlType);
		case DBINITSERVICE:
			return getSqLBean("dbinitservice", sqlType);
		case CONSTRAINTS:
			return getSqLBean("constraints", sqlType);
		case INDEXS:
			return getSqLBean("indexs", sqlType);
		case RULES:
			return getSqLBean("rules", sqlType);
		case COLUMNS:
			return getSqLBean("columns", sqlType);
		case TABLE_TRIGGERS:
			return getSqLBean("triggers", sqlType);
		case SERVER_STATUS:
			return getSqLBean("serverstatus", sqlType);
		case FITTABLE:
			return getSqLBean("fittable", sqlType);
		case UNQIUE:
			return getSqLBean("unqiue", sqlType);
		case CHECK:
			return getSqLBean("check", sqlType);
		case FOREIGN:
			return getSqLBean("foreignkey", sqlType);
		case DBSERVER:
			return getSqLBean("dbserver", sqlType);
		case EXTEND:
			return getSqLBean("extend", sqlType);
		case LOCK_STATUS:
			return getSqLBean("lockstatus", sqlType);	
		case AUTHORIZATION:
			return getSqLBean("authorization", sqlType);	
		default:
			break;
		}

		return null;
	}

	private static SqlBean getSqLBean(String itemType, String sqlType) {
		String itemPath = "sqls.sql." + itemType + ".";

		List<NodeEntity> nodeList = enger.getNodeList(itemPath + sqlType + ".column");
		NodeEntity sqlNode = enger.getNode(itemPath + sqlType + ".sql");
		if (sqlNode == null)
			return null;

		SqlBean sqlBean = new SqlBean();
		List<String[]> columnList = new ArrayList<String[]>();
		for (int i = 0; i < nodeList.size(); i++) {
			NodeEntity node = nodeList.get(i);
			String[] column = new String[2];
			column[0] = node.getAttri("name");
			column[1] = node.getAttri("title");
			columnList.add(column);
		}
		sqlBean.setColumnList(columnList);
		sqlBean.setSql(sqlNode.getText());
		return sqlBean;
	}

	private static SqlBean getSql(String itemType, String sqlType) {
		String itemPath = "sqls.sql." + itemType + ".";
		SqlBean sqlBean = new SqlBean();
		NodeEntity sqlNode = enger.getNode(itemPath + sqlType);
		if (sqlNode == null)
			return null;
		sqlBean.setColumnList(null);
		sqlBean.setSql(sqlNode.getText());
		return sqlBean;
	}
}
