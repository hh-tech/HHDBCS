package com.hhdb.csadmin.plugin.view;

import java.awt.Component;

import javax.swing.JOptionPane;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.EventUtil;

/**
 * 视图处理
 * 
 * @author hhxd
 * 
 */
public class HView extends AbstractPlugin {
	public String PLUGIN_ID = HView.class.getPackage().getName();
	
	/**
	 * 重写插件接收事件
	 */
	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent hevent = EventUtil.getReplyEvent(HView.class, event);
		if (event.getType().equals(EventTypeEnum.CMD.name())) {
			try {
				if (event.getValue("CMD").equals("viewOpenEvent")) { // 打开视图
					ViewOpenPanel viewPanel = new ViewOpenPanel(this);
					viewPanel.dbName = event.getValue("databaseName");
					viewPanel.smName = event.getValue("schemaName");
					viewPanel.viewName = event.getValue("viewName");
					viewPanel.openview();
					viewPanel.sqsv.getTabPanelTable("打开视图" + "(" + viewPanel.smName + "." + event.getValue("viewName") + ")", viewPanel);// 获取分页面板
				} else if (event.getValue("CMD").equals("viewAddEvent")) { // 新建视图
					ViewOpenPanel viewPanel = new ViewOpenPanel(this);
					viewPanel.dbName = event.getValue("databaseName");
					viewPanel.smName = event.getValue("schemaName");
					viewPanel.viewsTabPanelHandle(false);
					viewPanel.sqsv.getTabPanelTable("新建视图" + "(" + viewPanel.dbName + "." + viewPanel.smName + ")", viewPanel);
				} else if (event.getValue("CMD").equals("viewEditEvent")) { // 修改视图
					ViewOpenPanel viewPanel = new ViewOpenPanel(this);
					viewPanel.dbName = event.getValue("databaseName");
					viewPanel.smName = event.getValue("schemaName");
					viewPanel.viewName = event.getValue("viewName");
					viewPanel.viewsTabPanelHandle(true);
					viewPanel.sqsv.getTabPanelTable("修改视图" + "(" + viewPanel.smName + "." + viewPanel.viewName + ")", viewPanel);// 获取分页面板
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
