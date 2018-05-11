package com.hhdb.csadmin.plugin.query.dataselect;


import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.ui.BaseFrame;
import com.hhdb.csadmin.plugin.query.HQueryPlugin;
/**
 * 处理数据
 *
 */
public class SqlOperationService {
	public HQueryPlugin hquery ;
	public SqlOperationService (HQueryPlugin hquery){
		this.hquery = hquery;
	}
	public BaseFrame getBaseFrame() throws Exception {
		BaseFrame bf = null;
		String toID = "com.hhdb.csadmin.plugin.main";       
		HHEvent event = new HHEvent(hquery.PLUGIN_ID, toID, EventTypeEnum.GET_OBJ.name());
		HHEvent ev = hquery.sendEvent(event);
		if (ev instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) ev).getErrorMessage());
		}
		bf = (BaseFrame)ev.getObj();
		return bf;
	}
	
	/**
	 * 发送事件获取流数据操作面板
	 * @param databaseName
	 * @param schemaName
	 * @param tableName
	 * @param columnName  点击的列名
	 * @param ctid   点击的列的id
	 * @param value	 点击的值
	 * @param componentId   打开的面板id
	 */
	public void getDataFlowPanel(Object value) throws Exception {
		String toId = "com.hhdb.csadmin.plugin.flow_editor";
		CmdEvent tabPanelEvent = new CmdEvent(hquery.PLUGIN_ID, toId, "open");
		tabPanelEvent.setObj(value);
		HHEvent ev = hquery.sendEvent(tabPanelEvent);
		if (ev instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) ev).getErrorMessage());
		}
	}
	
}
