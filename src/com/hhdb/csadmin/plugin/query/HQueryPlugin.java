package com.hhdb.csadmin.plugin.query;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.common.util.EventUtil;
import com.hhdb.csadmin.plugin.cmd.SqlCmdPlugin;

public class HQueryPlugin extends AbstractPlugin{
	public String PLUGIN_ID = HQuery.class.getPackage().getName();
	private Map<String,HQuery> queryMap = new HashMap<String,HQuery>();
	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent relEvent = EventUtil.getReplyEvent(HQuery.class, event);
		if(event.getType().equals(EventTypeEnum.COMMON.name())){
			CmdEvent getsbEvent = new CmdEvent(PLUGIN_ID,
					"com.hhdb.csadmin.plugin.conn", "GetServerBean");
			HHEvent revent = sendEvent(getsbEvent);
			ServerBean serverbean = (ServerBean) revent.getObj();
			
			HQuery query = new HQuery(this);
			String toID="com.hhdb.csadmin.plugin.tabpane";
			CmdEvent addPanelEvent=new CmdEvent(PLUGIN_ID, toID,"AddPanelEvent");
			addPanelEvent.addProp("ICO", "querys.png");;
			addPanelEvent.setObj(query.getjPanel());
			addPanelEvent.addProp("TAB_TITLE", "查询器("+serverbean.getDBName()+")");
			String component_id = System.currentTimeMillis()+"";
			addPanelEvent.addProp("COMPONENT_ID", component_id);
			queryMap.put(component_id, query);
			sendEvent(addPanelEvent);
			
		}else if(event.getType().equals(EventTypeEnum.CMD.name())){
			if (event.getValue(EventTypeEnum.CMD.name()).equals("RemovePanelEvent")) {
				String componentId = event.getValue("COMPONENT_ID");
				HQuery q = queryMap.get(componentId);
				if(q!=null){
					q.closeConn();
					queryMap.remove(componentId);
				}
			} if(event.getValue(EventTypeEnum.CMD.name()).equals("initText")){
				String text = event.getValue("Text");
				CmdEvent getsbEvent = new CmdEvent(PLUGIN_ID,
						"com.hhdb.csadmin.plugin.conn", "GetServerBean");
				HHEvent revent = sendEvent(getsbEvent);
				ServerBean serverbean = (ServerBean) revent.getObj();
				
				HQuery query = new HQuery(this);
				query.getQueryUi().setText(text);
				String toID="com.hhdb.csadmin.plugin.tabpane";
				CmdEvent addPanelEvent=new CmdEvent(PLUGIN_ID, toID,"AddPanelEvent");
				addPanelEvent.addProp("ICO", "querys.png");;
				addPanelEvent.setObj(query.getjPanel());
				addPanelEvent.addProp("TAB_TITLE", "查询器("+serverbean.getDBName()+")");
				String component_id = System.currentTimeMillis()+"";
				addPanelEvent.addProp("COMPONENT_ID", component_id);
				queryMap.put(component_id, query);
				sendEvent(addPanelEvent);
			}
			else{
				ErrorEvent errorEvent = new ErrorEvent(SqlCmdPlugin.class.getPackage().getName(),
						event.getFromID(),
						ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
				errorEvent.setErrorMessage(PLUGIN_ID + "不能接受如下类型的事件:\n"
						+ event.toString());
				return errorEvent;
			}
		}else{
			ErrorEvent errorEvent = new ErrorEvent(SqlCmdPlugin.class.getPackage().getName(),
					event.getFromID(),
					ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
			errorEvent.setErrorMessage(PLUGIN_ID + "不能接受如下类型的事件:\n"
					+ event.toString());
			return errorEvent;
		}
		return relEvent;
	}
	@Override
	public Component getComponent() {
		return null;
	}
}
