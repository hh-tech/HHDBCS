package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import com.hhdb.csadmin.common.bean.ServerBean;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.HHSqlUtil;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;


public class ViewMenu extends BasePopupMenu implements TableInterface{

	private static final long serialVersionUID = 1L;
	
	private  BaseTreeNode treeNode;
	
	private HTree htree = null;
	
	public ViewMenu(HTree htree){
		this.htree = htree;
		add(createMenuItem("打开视图", OPENVIEW, this));
		add(createMenuItem("设计视图", DESIGNVIEW, this));
		addSeparator();
		add(createMenuItem("新建视图", NEWVIEW, this));
		add(createMenuItem("删除视图", DELETEVIEW, this));
		addSeparator();
		add(createMenuItem("复制视图", COPYVIEW, this));
		add(createMenuItem("粘贴视图", PASTEVIEW, this));
	}
	
	public  ViewMenu getInstance(BaseTreeNode node) {
		treeNode = node;
		return this;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		String toID = "com.hhdb.csadmin.plugin.view";
		CmdEvent getsbEvent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "GetServerBean");
		HHEvent rsbevent = htree.sendEvent(getsbEvent);
		ServerBean serverbean = (ServerBean)rsbevent.getObj();
		
		if(actionCmd.equals(OPENVIEW)){
			CmdEvent event = new CmdEvent(htree.PLUGIN_ID, toID, "viewOpenEvent");
			event.addProp("databaseName", serverbean.getDBName());
			event.addProp("schemaName", treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName());
			event.addProp("viewName", treeNode.getMetaTreeNodeBean().getName());
			event.addProp("ico", "false");
			HHEvent revent = htree.sendEvent(event);
			if(revent instanceof ErrorEvent){
				JOptionPane.showMessageDialog(null, ((ErrorEvent)revent).getErrorMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(actionCmd.equals(DESIGNVIEW)){
			CmdEvent event = new CmdEvent(htree.PLUGIN_ID, toID, "viewEditEvent");
			event.addProp("databaseName", serverbean.getDBName());
			event.addProp("schemaName", treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName());
			event.addProp("viewName", treeNode.getMetaTreeNodeBean().getName());
			HHEvent revent = htree.sendEvent(event);
			if(revent instanceof ErrorEvent){
				JOptionPane.showMessageDialog(null, ((ErrorEvent)revent).getErrorMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(actionCmd.equals(NEWVIEW)){
			CmdEvent event = new CmdEvent(htree.PLUGIN_ID, toID, "viewAddEvent");
			event.addProp("databaseName", serverbean.getDBName());
			event.addProp("schemaName", treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName());
			HHEvent revent = htree.sendEvent(event);
			if(revent instanceof ErrorEvent){
				JOptionPane.showMessageDialog(null, ((ErrorEvent)revent).getErrorMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(actionCmd.equals(DELETEVIEW)){
			int n = JOptionPane.showConfirmDialog(null, "这将删除视图，你确定想要继续吗？", "温馨提示", JOptionPane.YES_NO_OPTION);
			if (n == 0) {
				String sql = "drop VIEW \""+
						treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName()+
						"\".\""+treeNode.getMetaTreeNodeBean().getName()+ "\" CASCADE";
				try {
					htree.treeService.executeSql(sql);
					JOptionPane.showMessageDialog(null, "视图已经删除!", "消息", JOptionPane.INFORMATION_MESSAGE);
					htree.treeService.refreshView(treeNode.getParentBaseTreeNode());
				}
				 catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		else if(actionCmd.equals(COPYVIEW)){
			htree.treeService.copyViewName = treeNode.getMetaTreeNodeBean().getName();
			htree.treeService.copyViewId = treeNode.getMetaTreeNodeBean().getId();
			JOptionPane.showMessageDialog(null, htree.treeService.copyViewName+"视图已复制", "消息", JOptionPane.INFORMATION_MESSAGE);
		}
		else if(actionCmd.equals(PASTEVIEW)){
			if(htree.treeService.copyViewName==null||htree.treeService.copyViewName.trim().equals("")){
				JOptionPane.showMessageDialog(null, "没有复制的视图", "消息", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			String viewname = htree.treeService.copyViewName;
			int schemaid = 	treeNode.getParentBaseTreeNode().
					getParentBaseTreeNode().getMetaTreeNodeBean().getId();
			String schemaName = treeNode.getParentBaseTreeNode().
					getParentBaseTreeNode().getMetaTreeNodeBean().getName();
			
			try {
				String viewnameNew = htree.treeService.pdTableName(viewname,schemaid,0);
				
				Object[] params = new Object[1];
				params[0] = htree.treeService.copyViewId;

				List<Map<String, Object>> list = htree.treeService.getListMapBySql
						(HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.VIEW, "prop")
								.replaceParams(params));
				StringBuffer sb = new StringBuffer();
				if(list.size()>0){
					Map<String, Object> valueMap = list.get(0);
					String definition = valueMap.get("definition") + "";
					definition = definition.substring(definition.indexOf("SELECT",definition.indexOf("SELECT")+6));
					sb.append(String.format("CREATE OR REPLACE VIEW %s AS \n", "\""+schemaName+"\".\""+viewnameNew+"\""));
					sb.append(definition);
				}
				if(sb.length()==0){
					JOptionPane.showMessageDialog(null, "粘贴失败", "消息", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				htree.treeService.executeSql(sb.toString());;
				JOptionPane.showMessageDialog(null, "粘贴成功！", "消息", JOptionPane.INFORMATION_MESSAGE);
				htree.treeService.refreshView(treeNode.getParentBaseTreeNode());
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(null, e2.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
	}
}
