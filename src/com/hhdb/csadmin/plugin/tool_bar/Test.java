
package com.hhdb.csadmin.plugin.tool_bar;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.UnsupportedLookAndFeelException;

import com.hh.frame.swingui.base.IEventRoute;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.bean.HHEventRoute;
import com.hhdb.csadmin.common.util.UiUtil;

public class Test {
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UiUtil.setLookAndFeel();
		
		IEventRoute eventRoute= new HHEventRoute();
//		LoginPlugin loginPlugin=new LoginPlugin();
//		eventRoute.addPlugin("com.hhdb.csadmin.plugin.login");
//		loginPlugin.init(eventRoute);
		HHEvent loginEvent=new HHEvent("begin","com.hhdb.csadmin.plugin.tool_bar",EventTypeEnum.GET_OBJ.name());
		HHEvent reply=eventRoute.processEvent(loginEvent);	
		JFrame frame=new JFrame();
		frame.add((JComponent)reply.getObj());
		frame.setVisible(true);
		
	}
}
