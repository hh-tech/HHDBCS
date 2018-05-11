package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import com.hh.frame.swingui.event.CmdEvent;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;


/**
 * 表空间集合右键菜单
 * 
 * @author huyuanzhui
 * 
 */
public class TableSpsMenu extends BasePopupMenu {
	private static final long serialVersionUID = 1L;
	private BaseTreeNode treeNode;
	
	private HTree htree = null;
	
	public TableSpsMenu(HTree htree){
		this.htree = htree;
		add(createMenuItem("新建表空间", "addtablesp", this));
		add(createMenuItem("刷新", "refresh", this));
	}

	public TableSpsMenu getInstance(BaseTreeNode node) {
		
		treeNode = node;
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		if (actionCmd.equals("addtablesp")) {
			CmdEvent addevent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.table_space", "add");
			htree.sendEvent(addevent);
		}
		else if (actionCmd.equals("refresh")) {
			try {
				htree.treeService.refreshTablespaceCollection(treeNode);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "错误",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
