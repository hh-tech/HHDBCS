package com.hhdb.csadmin.plugin.query.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.bean.SqlBean;
import com.hhdb.csadmin.common.util.HHSqlUtil;
import com.hhdb.csadmin.common.util.StartUtil;
import com.hhdb.csadmin.plugin.query.HQueryPlugin;

/**
 * @ClassName: QueryService
 * @author: qinsz
 * @Description: 处理查询插件的业务逻辑
 *  @date: 2017年11月1日 上午11:01:21
 */
public class QueryService {
	
	/**
	 * @Title:  getSchemaNameList
	 * @Description: 获取方案名列表
	 * @param: @return   
	 * @author: qinsz
	 * @return: List<String>   
	 * @throws SQLException 
	 */
	public static List<String> getSchemaNameList(Connection conn) throws SQLException {
		List<String> schemeNameList = new ArrayList<String>();
		SqlBean sb = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.SCHEMA,
				"prop_coll");
		List<Map<String, String>> rs = SqlQueryUtil.selectStrMapList(
				conn, sb.getSql());
		for (Map<String, String> m : rs) {
			schemeNameList.add( m.get("name"));
		}
		return schemeNameList;
	}
	/**
	 * 设置模式
	 * @param conn
	 * @param schema
	 * @return
	 * @throws Exception
	 */
	public static boolean  setEnvSql(Connection conn,String schema) throws Exception {
		String envSql="SET search_path = \""+schema+"\"";
		SqlExeUtil.executeUpdate(conn, envSql);
		return true;
	}
	/**
	 * 判断是否有提交
	 * @param pid
	 * @param queryPlugin
	 * @return
	 */
	public static boolean isCommit(String pid,HQueryPlugin queryPlugin){
		boolean flag = false;
		String sql = "select count(*) from "+StartUtil.prefix+"_locks where pid='"+pid+"' and  mode != 'ExclusiveLock' and mode!='AccessShareLock' or (mode='AccessShareLock' and locktype!='relation') or (mode='ExclusiveLock' and locktype!='virtualxid')";
		CmdEvent executeListBySqlEvent = new CmdEvent(queryPlugin.PLUGIN_ID,"com.hhdb.csadmin.plugin.conn","ExecuteListBySqlEvent");
		executeListBySqlEvent.addProp("sql_str",sql);
		HHEvent refevent = queryPlugin.sendEvent(executeListBySqlEvent);
		@SuppressWarnings("unchecked")
		List<List<Object>> list = (List<List<Object>>)refevent.getObj();
		int count = Integer.parseInt(list.get(1).get(0).toString());
		if(count>0){
			return true;
		}
		return flag;
	}
	/**
	 * 终止正在执行的SQL
	 * @param pid
	 * @param queryPlugin
	 * @return
	 */
	public static boolean cancelQuery(String pid,HQueryPlugin queryPlugin){
		boolean flag = false;
		String sql = "select "+StartUtil.prefix+"_cancel_backend('"+pid+"')";
		CmdEvent executeListBySqlEvent = new CmdEvent(queryPlugin.PLUGIN_ID,"com.hhdb.csadmin.plugin.conn","ExecuteListBySqlEvent");
		executeListBySqlEvent.addProp("sql_str",sql);
		HHEvent refevent = queryPlugin.sendEvent(executeListBySqlEvent);		
		if(refevent instanceof ErrorEvent){
			
		}else{
			@SuppressWarnings("unchecked")
			List<List<Object>> list = (List<List<Object>>)refevent.getObj();
			String fs = list.get(1).get(0).toString();
			if(fs.equals("true")){
				return true;
			}
		}		
		return flag;
	}
	
	/**
	 * 判断交易是否出错
	 * @param pid
	 * @param queryPlugin
	 * @return
	 */
	public static boolean isTransactionError(String pid,HQueryPlugin queryPlugin){
		boolean flag = false;
		String sql = "select state from "+StartUtil.prefix+"_stat_activity where pid='"+pid+"'";
		CmdEvent executeListBySqlEvent = new CmdEvent(queryPlugin.PLUGIN_ID,"com.hhdb.csadmin.plugin.conn","ExecuteListBySqlEvent");
		executeListBySqlEvent.addProp("sql_str",sql);
		HHEvent refevent = queryPlugin.sendEvent(executeListBySqlEvent);
		@SuppressWarnings("unchecked")
		List<List<Object>> list = (List<List<Object>>)refevent.getObj();
		String state = list.get(1).get(0).toString();
		if(state.contains("idle in transaction")){
			return true;
		}
		return flag;
	}
	
	/**
	 * 执行查询语句
	 * @param sql
	 * @param position
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static String executeQuery(String sql, int position,int pagenum) throws Exception {
		if(sql.endsWith(";")){
			sql=sql.substring(0, sql.lastIndexOf(";"));
		}
		if(position!=0){
			sql="select a.* from ("+sql+") a limit "+position + " offset "+pagenum+"*"+
		+ position;; 
		}
		return sql;
	}
	public static boolean isHasNext(String sql, int position,Connection conn) throws IOException, Exception  {
		boolean isHasNext=false;
		if(sql.endsWith(";")){
			sql=sql.substring(0, sql.lastIndexOf(";"));
		}
		if(position!=0){
			sql="select 1 from ("+sql+") a limit 1 offset "+position;
		}
		int count = SqlQueryUtil.getCountSql(conn, sql);
		if(count>0){
			isHasNext=true;
		}
		return isHasNext;
	}
//	public static int executeUpdate(String sql,HHStatement statement) throws Exception {
//		int count=statement.executeUpdate(sql);
//		return count;
//	}
//	
	/**
	 * 获取表的oid 和name
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, String>> getTableIdName(Connection conn,String schemaname)
			throws Exception {
		Object[] params = new Object[1];
		params[0] = "'" + schemaname + "'";
		SqlBean sb = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.TABLE,"prop_coll");
		List<Map<String, String>> rs = SqlQueryUtil.selectStrMapList(conn, sb.replaceParams(params));
		return rs;
	}
	
	/**
	 * 获取视图的oid 和name
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, String>> getViewIdName(Connection conn,String schemaname)
			throws Exception {
		Object[] params = new Object[1];
		params[0] = "'" + schemaname + "'";
		SqlBean sb = HHSqlUtil
				.getSqlBean(HHSqlUtil.ITEM_TYPE.VIEW, "prop_coll");
		List<Map<String, String>> rs = SqlQueryUtil.selectStrMapList(conn, sb.replaceParams(params));
		return rs;
	}
}
