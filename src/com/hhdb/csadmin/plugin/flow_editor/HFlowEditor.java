package com.hhdb.csadmin.plugin.flow_editor;

import java.awt.Component;

import javax.swing.JOptionPane;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.EventUtil;

/**
 * 流数据处理
 * 
 * @author hhxd
 * 
 */
public class HFlowEditor extends AbstractPlugin {

	public String PLUGIN_ID = HFlowEditor.class.getPackage().getName();

	private FlowEditorPanel hfep;

	/**
	 * 重写插件接收事件
	 */
	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent hevent = EventUtil.getReplyEvent(HFlowEditor.class, event);
		if (event.getType().equals(EventTypeEnum.CMD.name())) { // 打开操作面板
			try {
				if(event.getValue("CMD").equals("manipulate")){   //打开操作面板
					hfep = new FlowEditorPanel(this);
					hfep.databaseName = event.getValue("databaseName");
					hfep.schemaName = event.getValue("schemaName");
					hfep.tableName = event.getValue("tableName");
					hfep.columnName = event.getValue("columnName");
					hfep.ctid = event.getValue("ctid");
					hfep.componentId = event.getValue("componentId");
					hfep.value = event.getObj();
					hfep.name = "\"" + event.getValue("schemaName") + "\".\"" + event.getValue("tableName") + "\"";
					hfep.initPanel();
					// 弹出对话框
					new JFramePanel("数据操作", 600, 600, hfep, hfep.serv.getBaseFrame());
				}else if(event.getValue("CMD").equals("open")){
					hfep = new FlowEditorPanel(this);
					hfep.edit = false;
					hfep.value = event.getObj();
					hfep.initPanel();
					// 弹出对话框
					new JFramePanel("查看数据", 600, 600, hfep, hfep.serv.getBaseFrame());
				}
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, "错误信息：" + e.getMessage(),"错误", JOptionPane.ERROR_MESSAGE);
				return hevent;
			}
			return hevent;
		} else {
			ErrorEvent errorEvent = new ErrorEvent(PLUGIN_ID,event.getFromID(),ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
			errorEvent.setErrorMessage(PLUGIN_ID + "不能接受如下事件:\n"+ event.toString());
			return errorEvent;
		}
	}

	@Override
	public Component getComponent() {
		return null;
	}
}
