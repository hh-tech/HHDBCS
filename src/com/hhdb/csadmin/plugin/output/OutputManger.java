package com.hhdb.csadmin.plugin.output;

import java.awt.Component;

import javax.swing.JScrollPane;

import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.ui.textEdit.QueryTextPane;
import com.hhdb.csadmin.common.util.EventUtil;

public class OutputManger extends AbstractPlugin {
	private JScrollPane jPanel;
	private QueryTextPane textArea;

	public OutputManger() {
		textArea = new QueryTextPane();
		//textArea.setEditable(false);
		jPanel = new JScrollPane(textArea);
	}
  
	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent replyE = EventUtil.getReplyEvent(OutputManger.class, event);
		if (event.getType().equals(EventTypeEnum.GET_OBJ.name())) {
			replyE.setObj(jPanel);
			return replyE;
		} else if (event.getType().equals(EventTypeEnum.CMD.name())) {
			if (event.getValue("CMD").equals("Show")) {
				String createStr = event.getValue("createStr");
				textArea.setText(createStr);
			}
			return replyE;
		} else {
			ErrorEvent errorEvent = new ErrorEvent(OutputManger.class
					.getPackage().getName(), event.getFromID(),
					ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
			errorEvent.setErrorMessage(OutputManger.class.getPackage()
					.getName() + "不能接受如下事件:\n" + event.toString());
			return errorEvent;
		}
	}

	@Override
	public Component getComponent() {
		// TODO Auto-generated method stub
		return jPanel;
	}

}
