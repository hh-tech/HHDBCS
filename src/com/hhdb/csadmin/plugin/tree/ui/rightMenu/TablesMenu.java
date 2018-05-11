package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.hh.frame.swingui.event.CmdEvent;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.service.ScriptService;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;
import com.hhdb.csadmin.plugin.tree.ui.script.MarScriptPanel;


/**
 * 表集合右键菜单
 * 
 * @author huyuanzhui
 * 
 */
public class TablesMenu extends BasePopupMenu  implements TableInterface{

	private static final long serialVersionUID = 1L;
	private  BaseTreeNode treeNode;

	private HTree htree = null;
	
	public TablesMenu(HTree htree){		
		this.htree = htree;
		add(createMenuItem("脚本管理", "scriptmar",  this));
		addSeparator();
		add(createMenuItem("新建表", CREATETABLE, this));
		add(createMenuItem("粘贴表", PASTETABLE,  this));		
		add(createMenuItem("刷新", REFRESH, this));
	}
	
	public  TablesMenu getInstance(BaseTreeNode node) {
		treeNode = node;
		return this;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		if (actionCmd.equals(REFRESH)) {
			try {
				htree.treeService.refreshTable(treeNode);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "错误",
						JOptionPane.ERROR_MESSAGE);
			}
		}else if(actionCmd.equals(PASTETABLE)){
			if(htree.treeService.copyTableName==null||htree.treeService.copyTableName.trim().equals("")){
				JOptionPane.showMessageDialog(null, "没有复制的表", "消息", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			String tablename = htree.treeService.copyTableName;
			int schemaid = 	treeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getId();
			String schemaName = treeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getName();
			
			String sql;
			try {
				String tabnameNew = htree.treeService.pdTableName(tablename,schemaid,0);
				sql = "select * into \""+schemaName+"\".\""+tabnameNew+"\" from \""+htree.treeService.copySchemaName+"\".\""+tablename+"\"";
				htree.treeService.executeDQL(sql);
				JOptionPane.showMessageDialog(null, "粘贴成功！", "消息", JOptionPane.INFORMATION_MESSAGE);
				htree.treeService.refreshTable(treeNode);
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(null, e2.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}else if(actionCmd.equals(CREATETABLE)){
			CmdEvent event = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.table_operate", "TableCreateEvent");
			event.addProp("schemaName", treeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getName());
			htree.sendEvent(event);
		}else if(actionCmd.equals("scriptmar")){
			try {
				if(!ScriptService.checkTableEst()){
					ScriptService.initTable();
				}
				String toId = "com.hhdb.csadmin.plugin.tabpane";
				CmdEvent tabPanelEvent = new CmdEvent(htree.PLUGIN_ID, toId, "AddPanelEvent");
				tabPanelEvent.addProp("TAB_TITLE", "表脚本管理");
				tabPanelEvent.addProp("COMPONENT_ID", "script_"+ScriptService.tabletype);
				tabPanelEvent.addProp("ICO", "keys.png");
				tabPanelEvent.setObj(new MarScriptPanel(ScriptService.tabletype));
				htree.sendEvent(tabPanelEvent);
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				return;
			}
		} 
	}
}
