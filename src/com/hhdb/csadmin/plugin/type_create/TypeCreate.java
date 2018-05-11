package com.hhdb.csadmin.plugin.type_create;
import java.awt.Component;
import java.util.UUID;

import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.ui.textEdit.QueryTextPane;
import com.hhdb.csadmin.common.util.EventUtil;
/**
 * @Description: 创建类型
 * @date: 2017年12月6日
 * @Company: H2 Technology
 * @author: lidongjiao
 * @version 1.0
 */
public class TypeCreate extends AbstractPlugin{
	public final String PLUGIN_ID = this.getClass().getPackage().getName();
	public TypeCreate() {
	}

	/**
	 * 新建类型
	 */
	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent replyEvent = EventUtil.getReplyEvent(TypeCreate.class, event);
		if(event.getType().equals(EventTypeEnum.CMD.name())){
			 CmdEvent cmdEvent=(CmdEvent) event;
			 if(cmdEvent.getCmd().equals("RemovePanelEvent")){
			 }else if(cmdEvent.getCmd().equals("TypeCreateMainEvent")){
				    String schemaName=cmdEvent.getValue("schemaName");
				    createType(schemaName);
			 }
		}else{
			ErrorEvent errorEvent = new ErrorEvent(PLUGIN_ID,event.getFromID(),ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
			errorEvent.setErrorMessage(PLUGIN_ID + "不能接受如下事件:\n"+ event.toString());
			return errorEvent;
		}
		   return replyEvent;
	}
	
	/*
	 * 类型新建成功发送事件刷新树
	 */
    public void refreshTree(String schemaName){
    	String toId = "com.hhdb.csadmin.plugin.tree";
    	CmdEvent refreshEvent = new CmdEvent(PLUGIN_ID,toId,"RefreshAddTreeNodeEvent");
		refreshEvent.addProp("schemaName", schemaName);
		refreshEvent.addProp("treenode_type", "type");
		sendEvent(refreshEvent);
    }
    /**
	 * 发送事件获取sql面板
	 */
	/*private HHEvent getSqlPanel() {
			String toId = "com.hhdb.csadmin.plugin.sql_operation";
			HHEvent tabPanelEvent = new HHEvent(PLUGIN_ID, toId, EventTypeEnum.COMMON.name());
			return sendEvent(tabPanelEvent);
	}*/
	/*
	 * 新建类型
	 */
   public void createType(String schemaName){
	    QueryTextPane sqlView=new QueryTextPane();
		String toID = "com.hhdb.csadmin.plugin.tabpane";
		CmdEvent addPanelEvent = new CmdEvent(PLUGIN_ID, toID, "AddPanelEvent");
		TypeCreatePanel jPanel=new TypeCreatePanel(this, schemaName, sqlView);
		addPanelEvent.setObj(jPanel);
		addPanelEvent.addProp("ICO", "dbtype.png");
		addPanelEvent.addProp("TAB_TITLE", "创建类型"+"("+schemaName+")");
		String COMPONENT_ID=UUID.randomUUID().toString();
		addPanelEvent.addProp("COMPONENT_ID", COMPONENT_ID);
		sendEvent(addPanelEvent);
   }
   @Override
   public Component getComponent() {
	   return null;
   }
}
