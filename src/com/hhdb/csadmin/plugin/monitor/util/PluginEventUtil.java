package com.hhdb.csadmin.plugin.monitor.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.CSVUtil;


public class PluginEventUtil {
	
	public static List<List<String>> executeSqlEvent(AbstractPlugin plugin,String fromId,String sql) throws IOException{
		CmdEvent event = new CmdEvent(fromId, "com.hhdb.csadmin.plugin.conn", "ExecuteCSVBySqlEvent");
		event.addProp("sql_str", sql);		
		HHEvent re_event=plugin.sendEvent(event);
		if(re_event instanceof ErrorEvent){
			Error e = new Error(((ErrorEvent) re_event).getErrorMessage());
			throw e;
		}
		String result =  re_event.getValue("csv");
		return CSVUtil.cSV2List(result);
	}
	
	public static List<Map<String,Object>> executeSqlListMapEvent(AbstractPlugin plugin,String fromId,String sql) throws IOException{
		CmdEvent event = new CmdEvent(fromId, "com.hhdb.csadmin.plugin.conn", "ExecuteListMapBySqlEvent");
		event.addProp("sql_str", sql);		
		HHEvent re_event=plugin.sendEvent(event);
		if(re_event instanceof ErrorEvent){
			Error e = new Error(((ErrorEvent) re_event).getErrorMessage());
			throw e;
		}
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> list =  (List<Map<String,Object>>)re_event.getObj();
		return list;
	}

}
