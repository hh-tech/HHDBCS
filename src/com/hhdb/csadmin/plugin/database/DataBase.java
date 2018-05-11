package com.hhdb.csadmin.plugin.database;

import java.awt.Component;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.common.util.EventUtil;

public class DataBase extends AbstractPlugin{
	public final String PLUGIN_ID = this.getClass().getPackage().getName();
	
	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent replyE=EventUtil.getReplyEvent(DataBase.class, event);
		if(event.getType().equals(EventTypeEnum.CMD.name())){
			CmdEvent cmd = (CmdEvent)event;
			//添加事件
			if(cmd.getCmd().equals("add")){
				CreateDatabase creatdatabase = new CreateDatabase();
				creatdatabase.Createdatabase(this);
			}
		}
		
		return replyE;
	}

	@Override
	public Component getComponent() {
		
		return null;
	}

	public ServerBean getServerBean(){
		//获取ServerBean 
		CmdEvent getsbEvent = new CmdEvent(PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "GetServerBean");
		HHEvent revent = sendEvent(getsbEvent);
		ServerBean sb = (ServerBean)revent.getObj();
		return sb;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findData(String usql){
		CmdEvent getcfEvent = new CmdEvent(PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "ExecuteListMapBySqlEvent");
		getcfEvent.addProp("sql_str", usql);
		HHEvent ev = sendEvent(getcfEvent);
		if(ev instanceof ErrorEvent){
			throw new RuntimeException(((ErrorEvent) ev).getErrorMessage());
		}
		return (List<Map<String, Object>>) ev.getObj();
	}
	
	/**
	 * 刷新数据库信息
	 */
	public void refreshData(){
		CmdEvent refreshData = new CmdEvent(PLUGIN_ID, "com.hhdb.csadmin.plugin.tree", "RefreshAddTreeNodeEvent");
		refreshData.addProp("treenode_type", "database");
		HHEvent ev = sendEvent(refreshData);
		if(ev instanceof ErrorEvent){
			JOptionPane.showMessageDialog(null, "请刷新数据库集合信息");
		}
	}
	
	
}
