package com.hhdb.csadmin.plugin.user_permission;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.hh.frame.swingui.base.IEventRoute;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.bean.HHEventRoute;

public class Test {

	public static void main(String[] args) {
		
		       IEventRoute eventRoute= new HHEventRoute();
		        //设置ServerBean
				CmdEvent setsbEvent = new CmdEvent("begin", "com.hhdb.csadmin.plugin.conn", "SetConn");
				setsbEvent.addProp("host_str", "127.0.0.1");
				setsbEvent.addProp("port_str", "1432");
				setsbEvent.addProp("dbname_str", "hhdb");
				setsbEvent.addProp("username_str", "dba");
				setsbEvent.addProp("pass_str", "123456");
				setsbEvent.addProp("superuser_value", "true");
				eventRoute.processEvent(setsbEvent);
		CmdEvent cmd=new CmdEvent("test", "com.hhdb.csadmin.plugin.user_permission", "test");
		cmd.addProp("userName", "dba");
		HHEvent reply = eventRoute.processEvent(cmd);
		JFrame frame=new JFrame();
		frame.add((JPanel)reply.getObj());
		frame.setVisible(true);
	}

}
