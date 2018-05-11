package com.hhdb.csadmin.plugin.user_permission;

import java.awt.Component;

import javax.swing.JPanel;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.EventUtil;
import com.hhdb.csadmin.plugin.user_permission.panel.UserPermissionPanel;

 /**
 * 用户权限插件
 * @author Administrator
 *
 */
public class UserPermission  extends AbstractPlugin {
	public final String PLUGIN_ID = this.getClass().getPackage().getName();

	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent replyE=EventUtil.getReplyEvent(UserPermission.class, event);
		try {
		    if (event.getType().equals(EventTypeEnum.CMD.name())) {
		    	CmdEvent cmdevent = (CmdEvent) event;
		    	if (cmdevent.getCmd().equals("grantUserPermission")) {
					String userName = cmdevent.getValue("userName");
					String title = "用户权限 @" + userName ;
					UserPermissionPanel panel=new UserPermissionPanel(this, userName);
				    getTabPanel4Tab( panel ,title,userName);
		    	}else if(cmdevent.getCmd().equals("test")){
		    		String userName = cmdevent.getValue("userName");
		    		UserPermissionPanel panel=new UserPermissionPanel(this, userName);
		    		replyE.setObj(panel);
		    	}
		    	
			}else {
				ErrorEvent errorEvent = new ErrorEvent(PLUGIN_ID,event.getFromID(),ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
				errorEvent.setErrorMessage(PLUGIN_ID + "不能接受如下事件:\n"+ event.toString());
				return errorEvent;
			}
				
		} catch (Exception e) {
			ErrorEvent errorEvent=new ErrorEvent(PLUGIN_ID,event.getFromID(),ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
			errorEvent.setErrorMessage("异常:"+e.getMessage());
			LM.error(LM.Model.CS.name(), e);
			return errorEvent;
		}
			
		  return replyE;
	}
	   //面板 添加到标签页
		private void getTabPanel4Tab(JPanel panel ,String title,String COMPONENT_ID) {
			String toId = "com.hhdb.csadmin.plugin.tabpane";
			CmdEvent sequenceEvent = new CmdEvent(PLUGIN_ID,toId,"AddPanelEvent");
			sequenceEvent.addProp("TAB_TITLE", title);
			sequenceEvent.addProp("COMPONENT_ID", COMPONENT_ID);
			sequenceEvent.addProp("ICO", "usercollection.png");
			sequenceEvent.setObj(panel);
			sendEvent(sequenceEvent);
		}
	@Override
	public Component getComponent() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
