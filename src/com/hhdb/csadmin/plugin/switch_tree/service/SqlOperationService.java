package com.hhdb.csadmin.plugin.switch_tree.service;

import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.common.ui.BaseFrame;
import com.hhdb.csadmin.plugin.switch_tree.HSwitchTree;
/**
 * 处理数据
 *
 */
public class SqlOperationService {
	private HSwitchTree hSwitchTree ;
	
	public SqlOperationService (HSwitchTree hSwitchTree){
		this.hSwitchTree = hSwitchTree;
	}
	
	/**
	 * 获取Serverbean
	 */
	public ServerBean getServerbean() throws Exception {
		ServerBean sb = null;
		String toID = "com.hhdb.csadmin.plugin.conn";       
		CmdEvent event = new CmdEvent(hSwitchTree.PLUGIN_ID, toID, "GetServerBean");
		HHEvent ev = hSwitchTree.sendEvent(event);
		if (ev instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) ev).getErrorMessage());
		}
		sb = (ServerBean)ev.getObj();
		return sb;
	}
	
	/**
	 * 获取superuser_value
	 */
	public String getSuperuserValue() throws Exception {
		String toID = "com.hhdb.csadmin.plugin.conn";       
		CmdEvent event = new CmdEvent(hSwitchTree.PLUGIN_ID, toID, "getSuperuser");
		HHEvent ev = hSwitchTree.sendEvent(event);
		if (ev instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) ev).getErrorMessage());
		}
		return ev.getValue("superuser_value");
	}

	/**
	 * 获取BaseFrame
	 */
	public BaseFrame getBaseFrame() throws Exception {
		BaseFrame bf = null;
		String toID = "com.hhdb.csadmin.plugin.main";       
		HHEvent event = new HHEvent(hSwitchTree.PLUGIN_ID, toID, EventTypeEnum.GET_OBJ.name());
		HHEvent ev = hSwitchTree.sendEvent(event);
		if (ev instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) ev).getErrorMessage());
		}
		bf = (BaseFrame)ev.getObj();
		return bf;
	}
	
	
}
