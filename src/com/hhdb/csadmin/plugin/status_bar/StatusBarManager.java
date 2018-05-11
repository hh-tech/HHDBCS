package com.hhdb.csadmin.plugin.status_bar;

import java.awt.Component;

import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.EventUtil;

public class StatusBarManager extends AbstractPlugin {
	public static StatusBar statusBar = new StatusBar();
	

	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent replyE= EventUtil.getReplyEvent(StatusBarManager.class, event);
		if(event.getType().equals(EventTypeEnum.GET_OBJ.name())){
			replyE.setObj(statusBar);
			return replyE;
			
		}
		
			String value = event.getValue("STATUS");
			//将行列数据添加至label
			statusBar.label2.setText(value);
		return replyE;
	}


	@Override
	public Component getComponent() {
		return statusBar;
	}
	
	
}
