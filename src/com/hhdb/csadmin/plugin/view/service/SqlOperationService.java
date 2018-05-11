package com.hhdb.csadmin.plugin.view.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.bean.SqlBean;
import com.hhdb.csadmin.common.util.HHSqlUtil;
import com.hhdb.csadmin.plugin.view.HView;
import com.hhdb.csadmin.plugin.view.ViewOpenPanel;
/**
 * 处理数据
 *
 */
public class SqlOperationService {
	public HView hView ;
	public ViewOpenPanel vop;
	
	public SqlOperationService (HView hView,ViewOpenPanel vop){
		this.hView = hView;
		this.vop = vop;
	}
	
	/**
	 * 刷新树节点
	 * @throws Exception
	 */
	public void refresh() throws Exception {
		String toId = "com.hhdb.csadmin.plugin.tree";
		CmdEvent tabPanelEvent = new CmdEvent(hView.PLUGIN_ID, toId, "RefreshAddTreeNodeEvent");
		tabPanelEvent.addProp("schemaName", vop.smName);
		tabPanelEvent.addProp("treenode_type", "view");
		HHEvent ev = hView.sendEvent(tabPanelEvent);
		if (ev instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) ev).getErrorMessage());
		}
	}
	
	/**
	 * 根据类别获取信息
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getNameByType(HHSqlUtil.ITEM_TYPE type,String sqlType)throws Exception {
		Object[] params = new Object[1];
		params[0] = "'" + vop.smName + "'";
		SqlBean sb = HHSqlUtil.getSqlBean(type,sqlType);
		List<Map<String, Object>> rs = getListMap(sb.replaceParams(params));
		return rs;
	}
	
	/**
	 * 发送事件获取分页面板
	 * @param tabName
	 * @param viewPanel
	 * @throws Exception
	 */
	public void getTabPanelTable(String tabName, ViewOpenPanel viewPanel) throws Exception {
		String toId = "com.hhdb.csadmin.plugin.tabpane";
		CmdEvent tabPanelEvent = new CmdEvent(hView.PLUGIN_ID, toId, "AddPanelEvent");
		tabPanelEvent.setObj(viewPanel);
		tabPanelEvent.addProp("ICO", "view.png");
		tabPanelEvent.addProp("TAB_TITLE", tabName);
		tabPanelEvent.addProp("COMPONENT_ID",tabName);
		HHEvent ev = hView.sendEvent(tabPanelEvent);
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
		CmdEvent obtainRowsEvent = new CmdEvent(hView.PLUGIN_ID, toId,"ExecuteListMapBySqlEvent");
		obtainRowsEvent.addProp("sql_str", sql);
		HHEvent rowEvent = hView.sendEvent(obtainRowsEvent);
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
		CmdEvent obtainRowsEvent = new CmdEvent(hView.PLUGIN_ID, toId,"ExecuteListBySqlEvent");
		obtainRowsEvent.addProp("sql_str", sql);
		HHEvent rowEvent = hView.sendEvent(obtainRowsEvent);
		List<List<Object>> rowStr = null;
		if (rowEvent instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) rowEvent).getErrorMessage());
		} else {
			rowStr = (List<List<Object>>) rowEvent.getObj();
		}
		return rowStr;
	}
	/**
	 * 发送事件得到List< List< Object > >格式数据,包含字段名、字段类、字段数据
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<List<Object>> getListType(String sql) throws Exception {
		String toId = "com.hhdb.csadmin.plugin.conn";
		CmdEvent obtainRowsEvent = new CmdEvent(hView.PLUGIN_ID, toId,"ObtainDataDetails");
		obtainRowsEvent.addProp("sql_str", sql);
		HHEvent rowEvent = hView.sendEvent(obtainRowsEvent);
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
	public String sqlOperation(String sql) throws Exception{
		String toId = "com.hhdb.csadmin.plugin.conn";
		CmdEvent obtainRowsEvent = new CmdEvent(hView.PLUGIN_ID, toId,"ExecuteUpdateBySqlEvent");
		obtainRowsEvent.addProp("sql_str", sql);
		HHEvent rowEvent = hView.sendEvent(obtainRowsEvent);
		String rowStr = null;
		if (rowEvent instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) rowEvent).getErrorMessage());
		} else {
			rowStr = rowEvent.getValue("res");
		}
		return rowStr;
	}
	/**
	 * 发送事件获取流数据查看面板
	 * @param value	 点击的值
	 */
	public void getDataFlowPanel(Object value) throws Exception {
		String toId = "com.hhdb.csadmin.plugin.flow_editor";
		CmdEvent tabPanelEvent = new CmdEvent(hView.PLUGIN_ID, toId, "open");
		tabPanelEvent.setObj(value);
		HHEvent ev = hView.sendEvent(tabPanelEvent);
		if (ev instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) ev).getErrorMessage());
		}
	}
	
	/**
	 * 根据字类型获取不可编辑的列
	 * @param list 数据
	 * @param bool 是否有行号列
	 * @param type 类型
	 */
	public int[] getUneditable(List<List<Object>> list,Boolean bool,String type) {
		List<Object> typelist = new ArrayList<Object>();   //数据的类型
		if(bool){
			typelist.add("");
			for (Object field : list.get(1)) {
				typelist.add(field);
			}
		}else{
			typelist.addAll(list.get(1));
		}
		List<Integer> inte = new ArrayList<Integer>();
		for(int i=0;i<typelist.size();i++){
			if(!typelist.get(i).toString().equals(type)){
				inte.add(i);   
			}
		}
		int[] a = new int[inte.size()];
		for (int i = 0; i < inte.size(); i++) {
			a[i] = inte.get(i);
		}
		return a;
	}
}
