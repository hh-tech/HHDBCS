package com.hhdb.csadmin.plugin.function.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.bean.SqlBean;
import com.hhdb.csadmin.common.util.HHSqlUtil;
import com.hhdb.csadmin.common.util.StartUtil;
import com.hhdb.csadmin.plugin.function.FunctionTab;
/**
 * 处理数据
 *
 */
public class SqlOperationService {
	public FunctionTab fit ;
	
	public SqlOperationService (FunctionTab fit){
		this.fit = fit;
	}
	
	/**
	 * 根据类别获取信息
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getNameByType(HHSqlUtil.ITEM_TYPE type,String sqlType)throws Exception {
		Object[] params = new Object[1];
		params[0] = "'" + fit.schemaName + "'";
		SqlBean sb = HHSqlUtil.getSqlBean(type,sqlType);
		List<Map<String, Object>> rs = getListMap(sb.replaceParams(params));
		return rs;
	}
	
	/**
	 * 刷新树节点
	 * @throws Exception
	 */
	public void refresh() throws Exception {
		String toId = "com.hhdb.csadmin.plugin.tree";
		CmdEvent tabPanelEvent = new CmdEvent(fit.PLUGIN_ID, toId, "RefreshAddTreeNodeEvent");
		tabPanelEvent.addProp("schemaName", fit.schemaName);
		tabPanelEvent.addProp("treenode_type", "function");
		HHEvent ev = fit.sendEvent(tabPanelEvent);
		if (ev instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) ev).getErrorMessage());
		}
	}
	
	/**
	 * 发送事件获取分页面板
	 * @param tabName
	 * @param viewPanel
	 */
	public void getTabPanelTable(String tabName, JPanel jp) throws Exception {
		String toId = "com.hhdb.csadmin.plugin.tabpane";
		CmdEvent tabPanelEvent = new CmdEvent(fit.PLUGIN_ID, toId, "AddPanelEvent");
		tabPanelEvent.setObj(jp);
		tabPanelEvent.addProp("ICO", "function.png");
		tabPanelEvent.addProp("TAB_TITLE", tabName);
		tabPanelEvent.addProp("COMPONENT_ID", tabName);
		HHEvent ev = fit.sendEvent(tabPanelEvent);
		if (ev instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) ev).getErrorMessage());
		}
	}
	
	
	/**
	 * 发送事件得到List< Map< String, Object > >格式数据
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getListMap(String sql) throws Exception {
		String toId = "com.hhdb.csadmin.plugin.conn";
		CmdEvent obtainRowsEvent = new CmdEvent(fit.PLUGIN_ID, toId,"ExecuteListMapBySqlEvent");
		obtainRowsEvent.addProp("sql_str", sql);
		HHEvent rowEvent = fit.sendEvent(obtainRowsEvent);
		List<Map<String, Object>> rowStr = null;
		if (rowEvent instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) rowEvent).getErrorMessage());
		} else {
			rowStr = (List<Map<String, Object>>) rowEvent.getObj();
		}
		return rowStr;
	}
	
	
	/**
	 * 发送事件得到List< List< Object > >格式数据
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<List<Object>> getListList(String sql) throws Exception {
		String toId = "com.hhdb.csadmin.plugin.conn";
		CmdEvent obtainRowsEvent = new CmdEvent(fit.PLUGIN_ID, toId,"ExecuteListBySqlEvent");
		obtainRowsEvent.addProp("sql_str", sql);
		HHEvent rowEvent = fit.sendEvent(obtainRowsEvent);
		List<List<Object>> rowStr = null;
		if (rowEvent instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) rowEvent).getErrorMessage());
		} else {
			rowStr = (List<List<Object>>) rowEvent.getObj();
		}
		return rowStr;
	}
	
	
	/**
	 * 发送事件执行增，删，改sql的事件
	 * @return
	 */
	public String sqlOperation(String sql) throws Exception {
		String toId = "com.hhdb.csadmin.plugin.conn";
		CmdEvent obtainRowsEvent = new CmdEvent(fit.PLUGIN_ID, toId,"ExecuteUpdateBySqlEvent");
		obtainRowsEvent.addProp("sql_str", sql);
		HHEvent rowEvent = fit.sendEvent(obtainRowsEvent);
		String rowStr = null;
		if (rowEvent instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) rowEvent).getErrorMessage());
		} else {
			rowStr = rowEvent.getValue("res");
		}
		return rowStr;
	}
	
	/**
	 * 检查
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	public String checkFunctionToStr(String oid) throws Exception{
		String sql=" SELECT count(*) as num FROM "+StartUtil.prefix+"_proc p,"+StartUtil.prefix+"_language l WHERE p.prolang = l.oid and l.lanname='plhhsql' and p.oid="+oid;
		if(getListList(sql).size()>0){
			sql=" SELECT plhhsql_check_function(p.oid) FROM "+StartUtil.prefix+"_proc p,"+StartUtil.prefix+"_language l WHERE p.prolang = l.oid and l.lanname='plhhsql' and p.oid="+oid;
			List<Map<String, Object>> list = getListMap(sql);
			Map<String, Object> map = new HashMap<String, Object>();
			for (Map<String, Object> map1 : list) {
				map.putAll(map1);
			}
			if(map.size()!=0){
				if("".equals(map.get("plhhsql_check_function"))){
					return "";
				}else{
					return map.get("plhhsql_check_function").toString();
				}
			}else{
				return "";
			}
		}else{
			throw new Exception("此函数定义语言不是PLHHSQL");
		}
	}
	
}
