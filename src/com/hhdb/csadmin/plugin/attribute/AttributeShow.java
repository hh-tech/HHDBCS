package com.hhdb.csadmin.plugin.attribute;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.IOException;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.TableColumn;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hh.frame.swingui.swingcontrol.displayTable.TablePanelUtil;
import com.hhdb.csadmin.common.util.CSVUtil;
import com.hhdb.csadmin.common.util.EventUtil;
import com.hhdb.csadmin.plugin.table_open.ui.HHTableColumnCellRenderer;

public class AttributeShow extends AbstractPlugin {
	private JPanel attribute;

	public AttributeShow() {
	}

	@Override
	public HHEvent receEvent(HHEvent event) {
		if (event.getType().equals(EventTypeEnum.CMD.name())) {
			if(event.getValue("CMD").equals("Show")){
				// 先请求面板
				String fromID = AttributeShow.class.getPackage().getName();
				String toID = "com.hhdb.csadmin.plugin.tabpane";

				CmdEvent requestComEvent = new CmdEvent(fromID, toID,
						"flushAttributeEvent");
				HHEvent hhevent = sendEvent(requestComEvent);
				attribute = (JPanel) hhevent.getObj();

				// 直接显示属性表格
				String csvStr = event.getValue("csvuEventStr");
				if (!csvStr.equals("")) {
					List<List<String>> list;
					try {
						list = CSVUtil.cSV2List(csvStr);
					} catch (IOException e) {
						LM.error(LM.Model.CS.name(), e);
						ErrorEvent errorEvent = new ErrorEvent(AttributeShow.class.getPackage().getName(),
								event.getFromID(),
								ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
						errorEvent.setErrorMessage(e.getMessage());
						return errorEvent;
					}
					//获取表面板
					TablePanelUtil btp = new TablePanelUtil(false,null, false, true);
					btp.addLineNumberStr(list);
					btp.nterlacedDiscoloration(true,null,null);
					btp.highlight(true,true,null);
					
					//设置序号行样式
					TableColumn index = btp.getBaseTable().getColumnModel().getColumn(0);					
					int len = ((list.size()-1)+"").length();
					index.setMaxWidth(len*10);
					index.setMinWidth(len*10);
					index.setCellRenderer(new HHTableColumnCellRenderer());
					
					attribute.setLayout(new BorderLayout());
					attribute.removeAll();
					attribute.add(btp, BorderLayout.CENTER);
					attribute.updateUI();
				} else {
					attribute.setLayout(new FlowLayout());
					attribute.removeAll();
					attribute.add(new JLabel("没有属性数据"));
					attribute.updateUI();
				}
			}
			return EventUtil.getReplyEvent(AttributeShow.class, event);
		}else{
			ErrorEvent errorEvent = new ErrorEvent(AttributeShow.class.getPackage().getName(),
					event.getFromID(),
					ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
			errorEvent.setErrorMessage(AttributeShow.class.getPackage().getName() + "不能接受如下类型的事件:\n"
					+ event.toString());
			return errorEvent;
		}
	}

	@Override
	public Component getComponent() {
		return null;
	}

}
