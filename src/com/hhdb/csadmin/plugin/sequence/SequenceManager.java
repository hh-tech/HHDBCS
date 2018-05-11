package com.hhdb.csadmin.plugin.sequence;
import java.awt.Component;

import javax.swing.JPanel;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.ui.textEdit.QueryTextPane;
import com.hhdb.csadmin.common.util.EventUtil;
import com.hhdb.csadmin.plugin.sequence.panel.SequencePanel;
/**
 * 序列插件
 * @author Administrator
 *
 */
public class SequenceManager extends AbstractPlugin {
	public final String PLUGIN_ID = this.getClass().getPackage().getName();
	public SequenceManager(){
		
	}
	
	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent replyE=EventUtil.getReplyEvent(SequenceManager.class, event);
		  //获得显示sql的面板
		QueryTextPane sqlpanel = new QueryTextPane();
		try {
		    if (event.getType().equals(EventTypeEnum.CMD.name())) {
				CmdEvent cmdevent = (CmdEvent) event;
				if (cmdevent.getCmd().equals("createSequenceEvent")) {
					String databaseName = event.getValue("databaseName");
					String schemaName = event.getValue("schemaName");
					String title = "新建序列 @" + databaseName + "." + schemaName;
					SequencePanel panel = new SequencePanel(sqlpanel,
							schemaName, "",  false, this);
					getTabPanel4Tab(panel, title, title);
				} else if (cmdevent.getCmd().equals("editSequenceEvent")) {
					String databaseName = event.getValue("databaseName");
					String schemaName = event.getValue("schemaName");
					String seqName = event.getValue("seqName");
					String title = seqName + " @" + databaseName + "."
							+ schemaName;
					SequencePanel panel = new SequencePanel(sqlpanel,
							schemaName, seqName,  true, this);
					getTabPanel4Tab(panel, title, title);
				} else if (cmdevent.getCmd().equals("RemovePanelEvent")) {
					
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

	//新建序列面板 添加到标签页
	private void getTabPanel4Tab(JPanel panel ,String title,String COMPONENT_ID) {
		String toId = "com.hhdb.csadmin.plugin.tabpane";
		CmdEvent sequenceEvent = new CmdEvent(PLUGIN_ID,toId,"AddPanelEvent");
		sequenceEvent.addProp("TAB_TITLE", title);
		sequenceEvent.addProp("COMPONENT_ID", COMPONENT_ID);
		sequenceEvent.addProp("ICO", "sequenceindex.png");
		sequenceEvent.setObj(panel);
		sendEvent(sequenceEvent);
	}
	@Override
	public Component getComponent() {
		
		return null;
	}
	
	/*
	 * 序列新建成功发送事件刷新
	 */
    public void refreshTree(String schemaName){
    	String toId = "com.hhdb.csadmin.plugin.tree";
    	CmdEvent refreshEvent = new CmdEvent(PLUGIN_ID,toId,"RefreshAddTreeNodeEvent");
		refreshEvent.addProp("schemaName", schemaName);
		refreshEvent.addProp("treenode_type", "sequence");
		sendEvent(refreshEvent);
    }
}
