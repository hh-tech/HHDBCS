package com.hhdb.csadmin.plugin.table_open.service;

import java.sql.Connection;
import java.util.List;

import javax.swing.JOptionPane;

import com.hh.frame.common.log.LM;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.plugin.table_open.TableOpen;
/**
 * 处理数据
 *
 */
public class SqlOperationService {
	public TableOpen tbo;
	
	private Connection connection = null;
	
	public SqlOperationService (TableOpen tbo){
		this.tbo = tbo;
	}
	
	/**
	 * 获取Connection
	 * @return
	 */
	public Connection getConn() {		
		try {
			//及时判断conn是否有效
			if(null == connection || connection.isClosed() || !ConnUtil.isConnected(connection)){   
				String toId = "com.hhdb.csadmin.plugin.conn";
				CmdEvent obtainRowsEvent = new CmdEvent(tbo.PLUGIN_ID, toId,"GetConn");
				HHEvent rowEvent = tbo.sendEvent(obtainRowsEvent);
				if (rowEvent instanceof ErrorEvent) {
					throw new Exception(((ErrorEvent) rowEvent).getErrorMessage());
				} else {
					connection = (Connection) rowEvent.getObj();
				}
			}
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
		}
		return connection;
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
	public void getDataFlowPanel(String databaseName,String schemaName,String tableName,String columnName,String ctid,Object value,String componentId) throws Exception {
		String toId = "com.hhdb.csadmin.plugin.flow_editor";
		CmdEvent tabPanelEvent = new CmdEvent(tbo.PLUGIN_ID, toId, "manipulate");
		tabPanelEvent.addProp("databaseName", databaseName);
		tabPanelEvent.addProp("schemaName", schemaName);
		tabPanelEvent.addProp("tableName", tableName);
		tabPanelEvent.addProp("columnName", columnName);
		tabPanelEvent.addProp("ctid", ctid);
		tabPanelEvent.addProp("componentId", componentId);
		tabPanelEvent.setObj(value);
		HHEvent ev = tbo.sendEvent(tabPanelEvent);
		if (ev instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) ev).getErrorMessage());
		}
	}
	
	/**
	 * 发送事件得到List< List< Object > >格式数据
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<List<Object>> getListList(String sql) throws Exception {
		String toId = "com.hhdb.csadmin.plugin.conn";
		CmdEvent obtainRowsEvent = new CmdEvent(tbo.PLUGIN_ID, toId,"ExecuteListBySqlEvent");
		obtainRowsEvent.addProp("sql_str", sql);
		HHEvent rowEvent = tbo.sendEvent(obtainRowsEvent);
		List<List<Object>> rowStr = null;
		if (rowEvent instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) rowEvent).getErrorMessage());
		} else {
			rowStr = (List<List<Object>>) rowEvent.getObj();
		}
		return rowStr;
	}
	
	/**
	 * 发送事件获取连接数据插件，变更数据
	 * @param totalRowsSql
	 * @return
	 */
	public String sendConn4Update(String totalRowsSql) {
		String toId = "com.hhdb.csadmin.plugin.conn";
		CmdEvent obtainRowsEvent = new CmdEvent(tbo.PLUGIN_ID, toId, "ExecuteUpdateBySqlEvent");
		obtainRowsEvent.addProp("sql_str", totalRowsSql);
		HHEvent rowEvent = tbo.sendEvent(obtainRowsEvent);
		String rowStr = null;
		if (rowEvent instanceof ErrorEvent) {
			JOptionPane.showMessageDialog(null, rowEvent.getValue("MSG"), "提示", JOptionPane.ERROR_MESSAGE);
		} else {
			rowStr = rowEvent.getValue("res");
		}
		return rowStr;
	}
}
