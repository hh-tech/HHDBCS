package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;
import com.hh.frame.swingui.event.CmdEvent;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;


/**
 * 用户集合右键菜单
 * 
 * @author huyuanzhui
 * 
 */
public class UserMenu extends BasePopupMenu {
	private static final long serialVersionUID = 1L;
	private BaseTreeNode treeNode;
	
	private HTree htree = null;
	
	public UserMenu(HTree htree){
		this.htree = htree;
		add(createMenuItem("新建用户", "adduser", this));
		add(createMenuItem("设计用户", "upuser", this));
		add(createMenuItem("用户权限", "pouser", this));
		add(createMenuItem("重命名", "reuser", this));
		add(createMenuItem("删除用户", "deuser", this));
	}

	public UserMenu getInstance(BaseTreeNode node) {
		
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
		else if (actionCmd.equals("upuser")) {
			CmdEvent updevent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.user_create", "upd");
			updevent.addProp("userid", treeNode.getMetaTreeNodeBean().getId()+"");
			updevent.addProp("username", treeNode.getMetaTreeNodeBean().getName());
			htree.sendEvent(updevent);
		}
		else if (actionCmd.equals("pouser")) {
			CmdEvent updevent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.user_permission", "grantUserPermission");
			updevent.addProp("userName", treeNode.getMetaTreeNodeBean().getName());
			htree.sendEvent(updevent);		
		}
		else if (actionCmd.equals("reuser")) {
			CmdEvent renameevent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.user_create", "rename");
			renameevent.addProp("username", treeNode.getMetaTreeNodeBean().getName());
			htree.sendEvent(renameevent);
		}
		else if (actionCmd.equals("deuser")) {
			CmdEvent delevent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.user_create", "del");
			delevent.addProp("username", treeNode.getMetaTreeNodeBean().getName());
			htree.sendEvent(delevent);
		}
	}
}
