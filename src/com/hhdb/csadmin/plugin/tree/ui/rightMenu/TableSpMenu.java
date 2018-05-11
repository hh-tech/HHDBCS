package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;
import com.hh.frame.swingui.event.CmdEvent;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;


/**
 * 表空间实例右键菜单
 * 
 * @author huyuanzhui
 * 
 */
public class TableSpMenu extends BasePopupMenu {
	private static final long serialVersionUID = 1L;
	private BaseTreeNode treeNode;
	
	private HTree htree = null;
	
	public TableSpMenu(HTree htree){
		this.htree = htree;
		add(createMenuItem("删除表空间", "delete", this));
	}

	public TableSpMenu getInstance(BaseTreeNode node) {
		
		treeNode = node;
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		if (actionCmd.equals("delete")) {
			CmdEvent delevent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.table_space", "del");
			delevent.addProp("tablespace", treeNode.getMetaTreeNodeBean().getName());
			htree.sendEvent(delevent);
		}
	}
}
