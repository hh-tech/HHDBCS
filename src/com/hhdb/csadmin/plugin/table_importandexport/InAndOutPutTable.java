package com.hhdb.csadmin.plugin.table_importandexport;

import java.awt.Component;
import java.util.List;

import javax.swing.JOptionPane;

import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.EventUtil;
import com.hhdb.csadmin.plugin.table_importandexport.util.ExportUtil;

/**
 * @createTime:2017年12月13日14:35:46
 * @remark:表格导入导出
 * @author hwj
 * @version 1.0
 */
public class InAndOutPutTable extends AbstractPlugin {
	public String PLUGIN_ID = InAndOutPutTable.class.getPackage().getName();
	
	
	public InAndOutPutTable() {
		ExportUtil.inAndOutPutTable = this;
	}

	/**
	 * 事件类型判断以及确认取消方法的调用
	 * 
	 * @param HHEvent
	 *            事件
	 * @return HHEvent 事件
	 */
	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent hevent= EventUtil.getReplyEvent(InAndOutPutTable.class, event);
		if(event.getType().equals(EventTypeEnum.CMD.name())){
			try {
				if(event.getValue("CMD").equals("TableExportEvent")){   
					ImportAndExportPanel panel = new ImportAndExportPanel(1);
					Object[] options = { "导出", "取消" };
					String title = "导出向导";
					// 对话面板
					BaseOptionPaneInstance baseoption = new BaseOptionPaneInstance(
							null, panel, options, title, 300, 160);
					// 获取用户选择
					String value = (String) baseoption.getValue();
					if ("导出".equals(value)) {
						panel.excuteData(event);
					} else if ("取消".equals(value)) {
						baseoption.dispose();
					}
				}else if(event.getValue("CMD").equals("TableImportEvent")){  
					ImportAndExportPanel panel = new ImportAndExportPanel(2);
					Object[] options = { "导入", "取消" };
					String title = "导入向导";
					BaseOptionPaneInstance baseoption = new BaseOptionPaneInstance(
							null, panel, options, title, 300, 160);
					String value = (String) baseoption.getValue();
					if ("导入".equals(value)) {
						panel.excuteData(event);
					} else if ("取消".equals(value)) {
						baseoption.dispose();
					}
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,"错误信息：" + e.getMessage(), "错误",JOptionPane.ERROR_MESSAGE);
				return hevent;
			}
			return hevent;
		}else{
			ErrorEvent errorEvent = new ErrorEvent(PLUGIN_ID,event.getFromID(),ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
			errorEvent.setErrorMessage(PLUGIN_ID + "不能接受如下事件:\n"+ event.toString());
			return errorEvent;
		}
	}

	@Override
	public Component getComponent() {
		return null;
	}

	/**
	 * 得到需要导出的表数据
	 * 
	 * @param event
	 * @return
	 * @throws Exception
	 */
	public List<List<Object>> getDBtable(HHEvent event) throws Exception {
		// sql 查询参数中表所有数据
		String sql = "select * from " + event.getPropMap().get("schemaName")
				+ "." + event.getPropMap().get("tableName");
		String fromID = event.getToID();
		String toID = "com.hhdb.csadmin.plugin.conn";
		CmdEvent sendSqlEvent = new CmdEvent(fromID, toID, "ExecuteListBySqlEvent");
		sendSqlEvent.addProp("sql_str", sql);
		HHEvent rsEvent = sendEvent(sendSqlEvent);// 接收返回结果集
		// 将csv格式转换成DBTable格式
		@SuppressWarnings("unchecked")
		List<List<Object>> dbTable = (List<List<Object>>) rsEvent.getObj();
		return dbTable;
	}

}
