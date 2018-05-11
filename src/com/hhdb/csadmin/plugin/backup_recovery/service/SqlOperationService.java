package com.hhdb.csadmin.plugin.backup_recovery.service;

import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.common.ui.BaseFrame;
import com.hhdb.csadmin.plugin.backup_recovery.HBackupRecovery;
/**
 * 处理数据
 *
 */
public class SqlOperationService {
	public HBackupRecovery bkr ;
	
	public SqlOperationService (HBackupRecovery bkr){
		this.bkr = bkr;
	}
	
	/**
	 * 获取BaseFrame
	 */
	public BaseFrame getBaseFrame() throws Exception {
		BaseFrame bf = null;
		String toID = "com.hhdb.csadmin.plugin.main";       
		HHEvent event = new HHEvent(bkr.PLUGIN_ID, toID, EventTypeEnum.GET_OBJ.name());
		HHEvent ev = bkr.sendEvent(event);
		if (ev instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) ev).getErrorMessage());
		}
		bf = (BaseFrame)ev.getObj();
		return bf;
	}
	
	/**
	 * 获取Serverbean
	 */
	public ServerBean getServerbean() throws Exception {
		ServerBean sb = null;
		String toID = "com.hhdb.csadmin.plugin.conn";       
		CmdEvent event = new CmdEvent(bkr.PLUGIN_ID, toID, "GetServerBean");
		HHEvent ev = bkr.sendEvent(event);
		if (ev instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) ev).getErrorMessage());
		}
		sb = (ServerBean)ev.getObj();
		return sb;
	}
	
	
	
	
	
	
	
	
	
	
//	/**
//	 * 刷新树节点
//	 * @throws Exception
//	 */
//	public void refresh() throws Exception {
//		String toId = "com.hhdb.csadmin.plugin.tree";
//		CmdEvent tabPanelEvent = new CmdEvent(hView.PLUGIN_ID, toId, "RefreshAddTreeNodeEvent");
//		tabPanelEvent.addProp("schemaName", hView.smName);
//		tabPanelEvent.addProp("treenode_type", "view");
//		HHEvent ev = hView.sendEvent(tabPanelEvent);
//		if (ev instanceof ErrorEvent) {
//			throw new Exception(((ErrorEvent) ev).getErrorMessage());
//		}
//	}
//	
//	/**
//	 * 根据类别获取信息
//	 * @return
//	 * @throws Exception
//	 */
//	public List<Map<String, Object>> getNameByType(HHSqlUtil.ITEM_TYPE type,String sqlType)throws Exception {
//		Object[] params = new Object[1];
//		params[0] = "'" + hView.smName + "'";
//		SqlBean sb = HHSqlUtil.getSqlBean(type,sqlType);
//		List<Map<String, Object>> rs = CSVUtil.cSV2DBTable(getCsvBySql(sb.replaceParams(params))).getRows();
//		return rs;
//	}
//	
//	/**
//	 * 发送事件获取分页面板
//	 * @param tabName
//	 * @param viewPanel
//	 * @param id
//	 * @param bool  针对新建页面打开多个
//	 * @throws Exception
//	 */
//	public void getTabPanelTable(String tabName, ViewOpenPanel viewPanel,int id,Boolean bool) throws Exception {
//		String toId = "com.hhdb.csadmin.plugin.tabpane";
//		CmdEvent tabPanelEvent = new CmdEvent(hView.PLUGIN_ID, toId, "AddPanelEvent");
//		tabPanelEvent.setObj(viewPanel);
//		tabPanelEvent.addProp("ICO", "poptables.png");
//		tabPanelEvent.addProp("TAB_TITLE", tabName);
//		if(bool){
//			tabPanelEvent.addProp("COMPONENT_ID", id+"");
//		}else{
//			tabPanelEvent.addProp("COMPONENT_ID",tabName);
//		}
//		HHEvent ev = hView.sendEvent(tabPanelEvent);
//		if (ev instanceof ErrorEvent) {
//			throw new Exception(((ErrorEvent) ev).getErrorMessage());
//		}
//	}
//	
//	/**
//	 * 发送事件得到csv格式的字符串数据
//	 * @return
//	 */
//	public String getCsvBySql(String sql) throws Exception {
//		String toId = "com.hhdb.csadmin.plugin.conn";
//		CmdEvent obtainRowsEvent = new CmdEvent(hView.PLUGIN_ID, toId,"ExecuteCSVBySqlEvent");
//		obtainRowsEvent.addProp("sql_str", sql);
//		HHEvent rowEvent = hView.sendEvent(obtainRowsEvent);
//		String rowStr = null;
//		if (rowEvent instanceof ErrorEvent) {
//			throw new Exception(((ErrorEvent) rowEvent).getErrorMessage());
//		} else {
//			rowStr = rowEvent.getValue("csv");
//		}
//		return rowStr;
//	}
//	
//	/**
//	 * 发送事件执行增，删，改sql的事件
//	 * @return
//	 */
//	public String sqlOperation(String sql) throws Exception{
//		String toId = "com.hhdb.csadmin.plugin.conn";
//		CmdEvent obtainRowsEvent = new CmdEvent(hView.PLUGIN_ID, toId,"ExecuteUpdateBySqlEvent");
//		obtainRowsEvent.addProp("sql_str", sql);
//		HHEvent rowEvent = hView.sendEvent(obtainRowsEvent);
//		String rowStr = null;
//		if (rowEvent instanceof ErrorEvent) {
//			throw new Exception(((ErrorEvent) rowEvent).getErrorMessage());
//		} else {
//			rowStr = rowEvent.getValue("res");
//		}
//		return rowStr;
//	}
	
}
