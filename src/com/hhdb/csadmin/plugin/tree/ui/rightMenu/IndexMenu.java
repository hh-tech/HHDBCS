package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import com.hh.frame.common.log.LM;
import com.hh.frame.dbobj.hhdb.HHdbIndex;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;


public class IndexMenu extends BasePopupMenu{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  final String DELETE = "DELETE";
	private  BaseTreeNode treeNode;
	
	private HTree htree = null;
	
	public IndexMenu(HTree htree){
		this.htree = htree;
		add(createMenuItem("删除索引", DELETE, this));
	}
	
	public  IndexMenu getInstance(BaseTreeNode node) {
		treeNode = node;
		return this;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		if (actionCmd.equals(DELETE)) {//删除类型
			int result=JOptionPane.showConfirmDialog(null, "是否删除","删除索引",JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.YES_OPTION){
				HHdbIndex hbindex = (HHdbIndex)treeNode.getNodeObject();
				try {
					hbindex.drop();
					htree.treeService.refreshIndex(treeNode.getParentBaseTreeNode());
				} catch (Exception e1) {
					LM.error(LM.Model.CS.name(), e1);
					JOptionPane.showMessageDialog(null, e1.getMessage(), "错误",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		} 
		
	}
}
