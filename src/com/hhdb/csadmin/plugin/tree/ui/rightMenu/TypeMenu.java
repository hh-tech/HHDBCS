package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;


public class TypeMenu extends BasePopupMenu{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  final String DELETE = "design";
	private  final String CASCADE = "cascade";
	private  BaseTreeNode treeNode;
	
	private HTree htree = null;
	
	public TypeMenu(HTree htree){
		this.htree = htree;
		add(createMenuItem("删除类型", DELETE, this));
		add(createMenuItem("删除类型(级联)", CASCADE, this));
	}
	
	public  TypeMenu getInstance(BaseTreeNode node) {
		treeNode = node;
		return this;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		if (actionCmd.equals(DELETE)) {//删除类型
			int result=JOptionPane.showConfirmDialog(null, "是否删除","删除类型",JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.YES_OPTION){
				dropType(treeNode,false);
			}
			
			
		} else if (actionCmd.equals(CASCADE)) {//删除类型(级联)
			int result=JOptionPane.showConfirmDialog(null, "是否删除", "删除类型(级联)", JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.YES_OPTION){
				dropType(treeNode,true);
			}
			
		} 
		
	}
	
	
	public  void dropType(BaseTreeNode treeNode, boolean cascade){
		try {
			String name = treeNode.getMetaTreeNodeBean().getName();
			String schemaName = ((BaseTreeNode)treeNode.getParent().getParent()).getMetaTreeNodeBean().getName();
			
			StringBuffer command = new StringBuffer("DROP TYPE \""+schemaName+"\".\""+ name+"\"");
			if (cascade)
				command.append(" CASCADE");			
			htree.treeService.executeSql(command.toString());
			htree.treeService.refreshType((BaseTreeNode)treeNode.getParent());
		} catch (Exception e) {
			if(e.getMessage()!=null&&!"".equals(e.getMessage().trim())){
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}
