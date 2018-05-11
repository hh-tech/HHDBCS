package com.hhdb.csadmin.plugin.user_create;

import java.awt.Component;

import javax.swing.JOptionPane;

import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.EventUtil;

public class UserCreate extends AbstractPlugin {
	public final String PLUGIN_ID = this.getClass().getPackage().getName();
	
	/**
	 * 处理事件  
	 */
	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent replyE=EventUtil.getReplyEvent(UserCreate.class, event);
		if(event.getType().equals(EventTypeEnum.CMD.name())){
			CmdEvent cmdEvent = (CmdEvent) event;
			//新建用户
			if(cmdEvent.getCmd().equals("add")){
				UserCreatePanel usercreate = new UserCreatePanel();
				usercreate.init(this,true);
				String toId = "com.hhdb.csadmin.plugin.tabpane";
				CmdEvent sequenceEvent = new CmdEvent(PLUGIN_ID,toId,"AddPanelEvent");
				sequenceEvent.addProp("TAB_TITLE", "添加用户");
				sequenceEvent.addProp("COMPONENT_ID","addUser");
				sequenceEvent.addProp("ICO", "quser.png");
				sequenceEvent.setObj(usercreate.getjPanel());
				sendEvent(sequenceEvent);
			}
			//设计用户
			else if(cmdEvent.getCmd().equals("upd")){
				String id = cmdEvent.getValue("userid");
				String username = cmdEvent.getValue("username");
				UserCreatePanel usercreate = new UserCreatePanel();
				usercreate.upduser(this,id,username);
				String toId = "com.hhdb.csadmin.plugin.tabpane";
				CmdEvent sequenceEvent = new CmdEvent(PLUGIN_ID,toId,"AddPanelEvent");
				sequenceEvent.addProp("TAB_TITLE", "设计用户");
				sequenceEvent.addProp("COMPONENT_ID",username);
				sequenceEvent.addProp("ICO", "quser.png");
				sequenceEvent.setObj(usercreate.getjPanel());
				sendEvent(sequenceEvent);
				
			}
			//删除用户
			else if(cmdEvent.getCmd().equals("del")){
				String username =cmdEvent.getValue("username");
				UserCreatePanel usercreate = new UserCreatePanel();
				usercreate.deluser(this,username);
			}
			//修改名字
			else if(cmdEvent.getCmd().equals("rename")){
				String username =cmdEvent.getValue("username");
				UserCreatePanel usercreate = new UserCreatePanel();
				usercreate.renameuser(this,username);
			}
			
			
		}
		return replyE;
	}
	@Override
	public Component getComponent() {
		return null;
	}
	/**
	 * 发送事件增、删、改用户信息
	 */
	public boolean SaveData(String sql){
		CmdEvent getcfEvent = new CmdEvent(PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "ExecuteUpdateBySqlEvent");
		getcfEvent.addProp("sql_str", sql);
		HHEvent ev = sendEvent(getcfEvent);
		if(ev instanceof ErrorEvent){
			JOptionPane.showMessageDialog(null, ((ErrorEvent) ev).getErrorMessage());
			return false;
		}
		return true;
	}
	
	
	/**
	 * 刷新节点
	 */
	public void refreshData(){
		CmdEvent refreshData = new CmdEvent(PLUGIN_ID, "com.hhdb.csadmin.plugin.tree", "RefreshAddTreeNodeEvent");
		refreshData.addProp("treenode_type", "user");
		HHEvent ev = sendEvent(refreshData);
		if(ev instanceof ErrorEvent){
			JOptionPane.showMessageDialog(null, "请刷新登录用户集合信息");
		}
	}
	

	/**
	 * 查找用户信息
	 * @param sql
	 * @return
	 */
	public void selectData(String sql){
		
		CmdEvent getcfEvent = new CmdEvent(PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "ExecuteListMapBySqlEvent");
		getcfEvent.addProp("sql_str", sql);
		HHEvent ev = sendEvent(getcfEvent);
		if(ev instanceof ErrorEvent){
			throw new RuntimeException(((ErrorEvent) ev).getErrorMessage());
		}
	}
	
	

}
