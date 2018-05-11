package com.hhdb.csadmin.plugin.database;

import javax.swing.UnsupportedLookAndFeelException;

import com.hh.frame.swingui.base.IEventRoute;
import com.hh.frame.swingui.event.CmdEvent;
import com.hhdb.csadmin.common.bean.HHEventRoute;
import com.hhdb.csadmin.common.util.UiUtil;

public class test {
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UiUtil.setLookAndFeel();
		IEventRoute eventRoute= new HHEventRoute();
		//设置ServerBean
		CmdEvent setsbEvent = new CmdEvent("begin", "com.hhdb.csadmin.plugin.conn", "SetConn");
		setsbEvent.addProp("host_str", "localhost");
		setsbEvent.addProp("port_str", "1432");
		setsbEvent.addProp("dbname_str", "hhdb");
		setsbEvent.addProp("username_str", "dba");
		setsbEvent.addProp("pass_str", "123456");
		setsbEvent.addProp("superuser_value", "true");
		eventRoute.processEvent(setsbEvent);
		CmdEvent loginEvent=new CmdEvent("begin","com.hhdb.csadmin.plugin.database","add");
		eventRoute.processEvent(loginEvent);
	}
}
