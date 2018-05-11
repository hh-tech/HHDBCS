package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import com.hh.frame.swingui.event.CmdEvent;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;


/**
 * 用户集合右键菜单
 * 
 * @author huyuanzhui
 * 
 */
public class UsersMenu extends BasePopupMenu {
	private static final long serialVersionUID = 1L;
	private BaseTreeNode treeNode;
	
	private HTree htree = null;
	
	public UsersMenu(HTree htree){
		this.htree = htree;
		add(createMenuItem("新建用户", "adduser", this));
		add(createMenuItem("刷新", "refresh", this));
	}

	public UsersMenu getInstance(BaseTreeNode node) {
		
		treeNode = node;
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		if (actionCmd.equals("adduser")) {
			CmdEvent addevent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.user_create", "add");
			htree.sendEvent(addevent);
		}
		else if (actionCmd.equals("refresh")) {
			try {
				htree.treeService.refreshUserCollection(treeNode);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "错误",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
