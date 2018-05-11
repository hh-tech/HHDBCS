package com.hhdb.csadmin.plugin.cmd;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.hh.frame.swingui.base.IEventRoute;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.bean.HHEventRoute;
import com.hhdb.csadmin.common.util.UiUtil;

public class Test {
	public static void main(String[] args) throws Exception{
		UiUtil.setLookAndFeel();		
		IEventRoute eventRoute= new HHEventRoute();
		CmdEvent setsbEvent = new CmdEvent("begin", "com.hhdb.csadmin.plugin.conn", "SetConn");
		setsbEvent.addProp("host_str", "118.190.201.243");
		setsbEvent.addProp("port_str", "1432");
		setsbEvent.addProp("dbname_str", "hhdb");
		setsbEvent.addProp("username_str", "dba");
		setsbEvent.addProp("pass_str", "123456");
		setsbEvent.addProp("superuser_value", "false");
		eventRoute.processEvent(setsbEvent);
		
		HHEvent cmdEvent=new HHEvent("begin","com.hhdb.csadmin.plugin.cmd",EventTypeEnum.GET_OBJ.name());
		HHEvent reply=eventRoute.processEvent(cmdEvent);	
		JFrame frame=new JFrame();
		frame.add((JComponent)reply.getObj());
		frame.setVisible(true);
	}
}
