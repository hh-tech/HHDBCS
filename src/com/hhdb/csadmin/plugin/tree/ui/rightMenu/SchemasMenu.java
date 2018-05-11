package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.hh.frame.swingui.event.CmdEvent;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.service.ScriptService;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.action.CreateSchema;
import com.hhdb.csadmin.plugin.tree.ui.script.MarScriptPanel;
/**
 * Schema右键菜单
 * 
 * @author 胡圆锥
 * 
 */
public class SchemasMenu extends BasePopupMenu {
	private static final long serialVersionUID = 1L;
	private  BaseTreeNode treeNode;

	private HTree htree = null;
	
	public SchemasMenu(HTree htree){
		this.htree = htree;
		add(createMenuItem("脚本管理", "scriptmar",  this));
		addSeparator();
		add(createMenuItem("新建模式", "newschema", this));
//		add(createMenuItem("恢复模式", "restoreschmea", this));
		add(createMenuItem("刷新", "refresh", this));
	}
	
	public  SchemasMenu getInstance(BaseTreeNode node) {
		treeNode = node;
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		if (actionCmd.equals("newschema")) {
			new CreateSchema(treeNode,htree);
		} else if (actionCmd.equals("restoreschmea")) {
		} else if (actionCmd.equals("refresh")) {
			try {
				htree.treeService.refreshSchemaCollection(treeNode);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "消息",
						JOptionPane.ERROR_MESSAGE);
			}
		}else if(actionCmd.equals("scriptmar")){
			try {
				if(!ScriptService.checkTableEst()){
					ScriptService.initTable();
				}
				String toId = "com.hhdb.csadmin.plugin.tabpane";
				CmdEvent tabPanelEvent = new CmdEvent(htree.PLUGIN_ID, toId, "AddPanelEvent");
				tabPanelEvent.addProp("TAB_TITLE", "模式脚本管理");
				tabPanelEvent.addProp("COMPONENT_ID", "script_"+ScriptService.schematype);
				tabPanelEvent.addProp("ICO", "keys.png");
				tabPanelEvent.setObj(new MarScriptPanel(ScriptService.schematype));
				htree.sendEvent(tabPanelEvent);
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				return;
			}
		} 
	}
}
