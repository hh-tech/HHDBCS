package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.service.ScriptService;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.action.CreateSchema;
import com.hhdb.csadmin.plugin.tree.ui.script.ExescriptPanel;

/**
 * Schema右键菜单
 * 
 * @author huyuanzhui
 * 
 */
public class SchemaMenu extends BasePopupMenu {
	private static final long serialVersionUID = 1L;
	private BaseTreeNode treeNode;
	
	private HTree htree = null;
	
	public SchemaMenu(HTree htree){
		this.htree = htree;
		add(createMenuItem("执行脚本", "runScript", this));
		addSeparator();
		add(createMenuItem("新建模式", "newSchema", this));
		add(createMenuItem("删除模式", "delSchema", this));
//		addSeparator();
//		add(createMenuItem("命令列界面", "cmdpel", this));
//		add(createMenuItem("备份", "backup", this));
//		add(createMenuItem("恢复", "restore", this));
		addSeparator();
		add(createMenuItem("刷新", "refresh", this));
	}

	public SchemaMenu getInstance(BaseTreeNode node) {
		
		treeNode = node;
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		
		if(actionCmd.equals("runScript")){
			try {
				if(!ScriptService.checkTableEst()){
					ScriptService.initTable();
				}
				String flagName = treeNode.getMetaTreeNodeBean().getName();
				String toId = "com.hhdb.csadmin.plugin.tabpane";
				CmdEvent tabPanelEvent = new CmdEvent(htree.PLUGIN_ID, toId, "AddPanelEvent");
				tabPanelEvent.addProp("TAB_TITLE", "模式脚本执行("+flagName+")");
				tabPanelEvent.addProp("COMPONENT_ID", "script_"+ScriptService.schematype+flagName);
				tabPanelEvent.addProp("ICO", "start.png");
				tabPanelEvent.setObj(new ExescriptPanel(htree,treeNode,ScriptService.schematype));
				htree.sendEvent(tabPanelEvent);
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}		
		else if (actionCmd.equals("newSchema")) {
			new CreateSchema(treeNode,htree);
		} else if (actionCmd.equals("delSchema")) {
			if(treeNode.getMetaTreeNodeBean().getName().equals("public")){
				JOptionPane.showMessageDialog(null,"public模式不能删除", "警告",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			int n = JOptionPane.showConfirmDialog(null, "您确定要删除此模式？", "消息",
					JOptionPane.YES_NO_OPTION);
			if (n == 0) {

				StringBuffer sql = new StringBuffer("DROP SCHEMA \""
						+ treeNode.getMetaTreeNodeBean().getName());
				sql.append("\" CASCADE");
				String toID = "com.hhdb.csadmin.plugin.conn";
				CmdEvent event = new CmdEvent(
						htree.PLUGIN_ID, toID,
						"ExecuteUpdateBySqlEvent");
				event.addProp("sql_str", sql.toString());
				HHEvent ev = htree.sendEvent(event);
				if (ev instanceof ErrorEvent) {
					JOptionPane.showMessageDialog(null,
							((ErrorEvent) ev).getErrorMessage(), "消息",
							JOptionPane.ERROR_MESSAGE);
					return;
				} else {
					try {
						htree.treeService.refreshSchemaCollection(treeNode.getParentBaseTreeNode());
					} catch (Exception e1) {
						LM.error(LM.Model.CS.name(), e1);
					}
//					TreePath currentSelection = ((BaseTree) (htree
//							.getComponent())).getSelectionPath();
//					if (currentSelection != null) {
//						DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection
//								.getLastPathComponent());
//						MutableTreeNode parent = (MutableTreeNode) (currentNode
//								.getParent());
//						if (parent != null) {
//							((BaseTree) (htree
//									.getComponent())).getTreeModel()
//									.removeNodeFromParent(currentNode);
//							return;
//						}
//					}
				}

			}
		} else if (actionCmd.equals("backup")) {

		} else if (actionCmd.equals("refresh")) {
			try {
				htree.treeService.refreshSchema(treeNode);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "消息",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
