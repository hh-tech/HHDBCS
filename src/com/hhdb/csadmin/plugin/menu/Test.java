package com.hhdb.csadmin.plugin.menu;

import java.awt.Frame;

import javax.swing.JMenuBar;

import com.hh.frame.swingui.base.IEventRoute;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.bean.HHEventRoute;

public class Test {

	public static void main(String[] args) {
		
		IEventRoute eventRoute= new HHEventRoute();
		HHEvent menuEvent=new HHEvent("com.hhdb.csadmin.plugin.test","com.hhdb.csadmin.plugin.menu",EventTypeEnum.GET_OBJ.name());
		HHEvent processEvent = eventRoute.processEvent(menuEvent);
		JMenuBar menubar=(JMenuBar) processEvent.getObj();
		Frame f=new Frame();
		f.add(menubar);
		f.setVisible(true);
	}

}
