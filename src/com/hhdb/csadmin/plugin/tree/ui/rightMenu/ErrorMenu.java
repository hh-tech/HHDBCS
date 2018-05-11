package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;


public class ErrorMenu extends BasePopupMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ErrorMenu(){
		add(createMenuItem("包含不同类型节点", "error", this));
	}
	
	public ErrorMenu getInstance(BaseTreeNode node) {
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

}
