package com.hhdb.csadmin.plugin.table_operate;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.collections4.map.HashedMap;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.EventUtil;

/**
 * 操作数据表
 */
public class TableEdit extends AbstractPlugin {
	public String PLUGIN_ID = TableEdit.class.getPackage().getName();
	public Map<String, Object> map = new HashedMap<>();

	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent hevent= EventUtil.getReplyEvent(TableEdit.class, event);
		if(event.getType().equals(EventTypeEnum.CMD.name())){
			try {
				if(event.getValue("CMD").equals("RemovePanelEvent")){   //窗口关闭
					
				}else if(event.getValue("CMD").equals("TableCreateEvent")){ //新建表
					TableEditPanel tab = new TableEditPanel(this,event.getPropMap().get("schemaName"),true);
					JPanel jp = new JPanel();
					jp.setLayout(new GridBagLayout());
					jp.add(tab,new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
					String id = event.getPropMap().get("tableName")+event.getPropMap().get("schemaName") + "create";
					tab.sqls.getTabPanelTable(id,"新建表("+event.getPropMap().get("schemaName")+")", jp);
					map.put(id, jp);
				}else if(event.getValue("CMD").equals("TableEditMainEvent")){ //修改表
					TableEditPanel tab = new TableEditPanel(this,event.getPropMap().get("schemaName"),event.getPropMap().get("tableName"),event.getPropMap().get("tableoId"),false);
					JPanel jp = new JPanel();
					jp.setLayout(new GridBagLayout());
					jp.add(tab,new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
					String id = event.getPropMap().get("tableName")+event.getPropMap().get("schemaName") + "edit";
					tab.sqls.getTabPanelTable(id,"设计表("+event.getPropMap().get("tableName")+" "+event.getPropMap().get("schemaName")+")", jp);
					map.put(id, jp);
				}
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
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
	
	/**
	 * 刷新页面,更新数据
	 * @param tableName
	 * @param schemaName
	 * @param tableoId
	 * @param type
	 * @param bool
	 */
	public void refresh(String tableName,String schemaName,String tableoId,String type,Boolean bool) {
		String id = tableName+schemaName+type;
		for (String key : map.keySet()) {
			if(key.equals(id)){
				try {
					JPanel jp = (JPanel) map.get(key);
					TableEditPanel tab;
					if(null != tableoId && !tableoId.equals("")){
						tab = new TableEditPanel(this,schemaName,tableName,tableoId,bool);
					}else{
						tab = new TableEditPanel(this,schemaName,bool);
					}
					jp.removeAll();
					jp.add(tab,new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
					jp.updateUI();
					jp.repaint();  //重绘页面
					map.put(id, jp);
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					JOptionPane.showMessageDialog(null,"错误信息：" + e.getMessage(), "错误",JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	@Override
	public Component getComponent() {
		return null;
	}
}
