package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;
/**
 * 约束集合右键菜单
 * @author HuBingBing
 *
 */
public class ConstraintsMenu extends BasePopupMenu {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  final String REFRESH = "refresh";
	private   BaseTreeNode treeNode;
	private HTree htree = null;
	
	public ConstraintsMenu(HTree htree){
		this.htree=htree;
		add(createMenuItem("刷新", REFRESH, this));
	}
	
	public ConstraintsMenu getInstance(BaseTreeNode node){
		this.treeNode = node;
		return this;
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		if (actionCmd.equals(REFRESH)) {
			try {
				htree.treeService.refreshConstraint(treeNode);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "错误",
						JOptionPane.ERROR_MESSAGE);
			}
	}

}
}
