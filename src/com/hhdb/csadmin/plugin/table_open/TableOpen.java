package com.hhdb.csadmin.plugin.table_open;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.EventUtil;


/**
 * 打开表操作插件
 * 
 * @author hewei
 * 
 */
public class TableOpen extends AbstractPlugin {
	// 插件ID
	public String PLUGIN_ID = TableOpen.class.getPackage().getName();
	
	private Map<String, TableOpenPanel> map = new HashMap<String, TableOpenPanel>();
	public int j = 0;
	
	/**
	 * 重写插件接收事件
	 */
	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent reply = EventUtil.getReplyEvent(TableOpen.class, event);
		if(event.getType().equals(EventTypeEnum.CMD.name())){
			CmdEvent cmdEvent=(CmdEvent)event;
			if (cmdEvent.getCmd().equalsIgnoreCase("tableOpenEvent")) {
				String databaseName = event.getValue("databaseName");
				String schemaName = event.getValue("schemaName");
				String table = event.getValue("tableName");
				// 初始化面板
				String tableNames="\""+schemaName+"\".\""+table+"\"";
				TableOpenPanel tablePanel = new TableOpenPanel(databaseName,schemaName,table,tableNames,this);
				
				// 获取分页面板
				String tabName = table + "(" + databaseName + "." + schemaName + ")";
				
				Boolean bool = true;
				for (String string : map.keySet()) {
					if(table.equals(map.get(string).table) && schemaName.equals(map.get(string).schemaName)){		//判断是否已打开
						bool = false;
						getTabPanel4Table(tabName, tablePanel,Integer.valueOf(string));
						break;
					}
				}
				if(bool){
					j ++;
					map.put(j+"", tablePanel);
					tablePanel.j = j;
					getTabPanel4Table(tabName, tablePanel,j);
				}
			}else if(cmdEvent.getCmd().equalsIgnoreCase("refreshtable")){    //刷新表格
				map.get(event.getValue("componentId")).refresh();
			}
		}else{
			ErrorEvent errorEvent = new ErrorEvent(PLUGIN_ID,event.getFromID(),ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
			errorEvent.setErrorMessage(PLUGIN_ID + "不能接受如下事件:\n"+ event.toString());
			return errorEvent;
		}
		
		return reply;
	}

	

	/**
	 * 发送事件获取分页面板
	 * @param tabName
	 * @param tablePanel
	 */
	private void getTabPanel4Table(String tabName, TableOpenPanel tablePanel, int it) {
		String toId = "com.hhdb.csadmin.plugin.tabpane";
		CmdEvent tabPanelEvent = new CmdEvent(PLUGIN_ID, toId, "AddPanelEvent");
		tabPanelEvent.addProp("TAB_TITLE", tabName);
		tabPanelEvent.addProp("COMPONENT_ID", it+"");
		tabPanelEvent.addProp("ICO", "poptables.png");
		tabPanelEvent.setObj(tablePanel);
		sendEvent(tabPanelEvent);
	}

	@Override
	public Component getComponent() {
		return null;
	}

	
}
