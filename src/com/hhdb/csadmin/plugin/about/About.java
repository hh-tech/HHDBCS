package com.hhdb.csadmin.plugin.about;

import java.awt.Component;

import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.EventUtil;

public class About extends AbstractPlugin{

	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent replyE=EventUtil.getReplyEvent(About.class, event);
		if(event.getType().equals(EventTypeEnum.GET_OBJ.name())){
			 new AboutJFrame();
		}
		return replyE;
	}

	@Override
	public Component getComponent() {
		return null;
	}
	

}
