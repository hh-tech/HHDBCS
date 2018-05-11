package com.hhdb.csadmin.plugin.table_space;

import java.awt.Component;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.EventUtil;

public class TableSpace extends AbstractPlugin {
	public final String PLUGIN_ID = this.getClass().getPackage().getName();
	
	
	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent replyE=EventUtil.getReplyEvent(TableSpace.class, event);
		if(event.getType().equals(EventTypeEnum.CMD.name())){
			
			CmdEvent cmd = (CmdEvent)event;
			//添加事件
			if(cmd.getCmd().equals("add")){
			TableSpaceHandle tablespace =  new TableSpaceHandle();
			tablespace.TablesSpaceHandle(this);
			String toId = "com.hhdb.csadmin.plugin.tabpane";
			CmdEvent sequenceEvent = new CmdEvent(PLUGIN_ID,toId,"AddPanelEvent");
			sequenceEvent.addProp("TAB_TITLE", "添加表空间");
			sequenceEvent.addProp("COMPONENT_ID","addTableSpace");
			sequenceEvent.addProp("ICO", "addtabspace.png");
			sequenceEvent.setObj(tablespace.getJPanel());
			sendEvent(sequenceEvent);
			}
			//删除事件
			if(cmd.getCmd().equals("del")){
				String tablespacename =cmd.getValue("tablespace");
				TableSpaceHandle tablespace =  new TableSpaceHandle();
				tablespace.delTableSpace(this,tablespacename);
			}
		}
		return replyE;
	}

	@Override
	public Component getComponent() {
		return null;
	}
	
	/***
	 * 发送查询事件给conn
	 * @param sql
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> FindData(String sql){
		CmdEvent getcfEvent = new CmdEvent(PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "ExecuteListMapBySqlEvent");
		getcfEvent.addProp("sql_str", sql);
		HHEvent ev = sendEvent(getcfEvent);
		if(ev instanceof ErrorEvent){
			throw new RuntimeException(((ErrorEvent) ev).getErrorMessage());
		}
		return (List<Map<String, Object>>) ev.getObj();
	}
	
	/**
	 * 发送事件保存
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
		refreshData.addProp("treenode_type", "tablespace");
		HHEvent ev = sendEvent(refreshData);
		if(ev instanceof ErrorEvent){
			JOptionPane.showMessageDialog(null, "请刷新表空间集合信息");
		}
	}

}
