package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.event.CmdEvent;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.service.ScriptService;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;
import com.hhdb.csadmin.plugin.tree.ui.script.MarScriptPanel;


/**
 * 数据库集合右键菜单
 * 
 * @author huyuanzhui
 * 
 */
public class DBsMenu extends BasePopupMenu {
	private static final long serialVersionUID = 1L;
	private BaseTreeNode treeNode;
	
	private HTree htree = null;
	
	public DBsMenu(HTree htree){
		this.htree = htree;
		add(createMenuItem("脚本管理", "scriptmar",  this));
		addSeparator();
		add(createMenuItem("新建数据库", "adddb", this));
		add(createMenuItem("刷新", "refresh", this));
	}

	public DBsMenu getInstance(BaseTreeNode node) {
		
		treeNode = node;
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		if (actionCmd.equals("adddb")) {
			CmdEvent addevent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.database", "add");
			htree.sendEvent(addevent);
		}
		else if (actionCmd.equals("refresh")) {
			try {
				htree.treeService.refreshDBCollection(treeNode);
			} catch (Exception e1) {
				LM.error(LM.Model.CS.name(), e1);
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
				tabPanelEvent.addProp("TAB_TITLE", "数据库脚本管理");
				tabPanelEvent.addProp("COMPONENT_ID", "script_"+ScriptService.dbtype);
				tabPanelEvent.addProp("ICO", "keys.png");
				tabPanelEvent.setObj(new MarScriptPanel(ScriptService.dbtype));
				htree.sendEvent(tabPanelEvent);
			} catch (SQLException e1) {
				LM.error(LM.Model.CS.name(), e1);
				JOptionPane.showMessageDialog(null, e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				return;
			}
		} 
	}
}
