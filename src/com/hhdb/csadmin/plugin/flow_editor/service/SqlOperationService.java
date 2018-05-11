package com.hhdb.csadmin.plugin.flow_editor.service;

import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.common.ui.BaseFrame;
import com.hhdb.csadmin.plugin.flow_editor.HFlowEditor;
/**
 * 处理数据
 *
 */
public class SqlOperationService { 
private HFlowEditor hfe ;
	
	public SqlOperationService (HFlowEditor hfe){
		this.hfe = hfe;
	}
	
	
	/**
	 * 获取BaseFrame
	 */
	public BaseFrame getBaseFrame() throws Exception {
		BaseFrame bf = null;
		String toID = "com.hhdb.csadmin.plugin.main";       
		HHEvent event = new HHEvent(hfe.PLUGIN_ID, toID, EventTypeEnum.GET_OBJ.name());
		HHEvent ev = hfe.sendEvent(event);
		if (ev instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) ev).getErrorMessage());
		}
		bf = (BaseFrame)ev.getObj();
		return bf;
	}
	
	/**
	 * 获取ServerBean
	 * @return
	 */
	public ServerBean getServerBean() throws Exception {
		String toId = "com.hhdb.csadmin.plugin.conn";
		CmdEvent obtainRowsEvent = new CmdEvent(hfe.PLUGIN_ID, toId,"GetServerBean");
		HHEvent rowEvent = hfe.sendEvent(obtainRowsEvent);
		ServerBean sb = null;
		if (rowEvent instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) rowEvent).getErrorMessage());
		} else {
			sb = (ServerBean) rowEvent.getObj();
		}
		return sb;
	}
	
	/**
	 * 发送事件执行增，删，改sql的事件
	 * @return
	 */
	public String sqlOperation(String sql) throws Exception{
		String toId = "com.hhdb.csadmin.plugin.conn";
		CmdEvent obtainRowsEvent = new CmdEvent(hfe.PLUGIN_ID, toId,"ExecuteUpdateBySqlEvent");
		obtainRowsEvent.addProp("sql_str", sql);
		HHEvent rowEvent = hfe.sendEvent(obtainRowsEvent);
		String rowStr = null;
		if (rowEvent instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) rowEvent).getErrorMessage());
		} else {
			rowStr = rowEvent.getValue("res");
		}
		return rowStr;
	}
	
	/**
	 * 刷新表格
	 * @param componentId  打开表面板id
	 * @throws Exception
	 */
	public void refreshTable(String componentId) throws Exception{
		String toId = "com.hhdb.csadmin.plugin.table_open";
		CmdEvent obtainRowsEvent = new CmdEvent(hfe.PLUGIN_ID, toId,"refreshtable");
		obtainRowsEvent.addProp("componentId", componentId);
		HHEvent rowEvent = hfe.sendEvent(obtainRowsEvent);
		if (rowEvent instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) rowEvent).getErrorMessage());
		} 
	}
}
