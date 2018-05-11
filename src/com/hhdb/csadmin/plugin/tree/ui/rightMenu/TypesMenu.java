package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;


public class TypesMenu extends BasePopupMenu {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  BaseTreeNode treeNode;
	private final String REFRESH ="refresh";
	private final String TYPE ="type";
	
	private HTree htree = null;
	
	public TypesMenu(HTree htree){
		this.htree = htree;
		add(createMenuItem("新建类型", TYPE, this));
		add(createMenuItem("刷新", REFRESH, this));
	}
	
	public  TypesMenu getInstance(BaseTreeNode node){
		treeNode = node;
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		if (actionCmd.equals(REFRESH)) {
			try {
				htree.treeService.refreshType(treeNode);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "错误",
						JOptionPane.ERROR_MESSAGE);
			}
		} else if (actionCmd.equals(TYPE)) {
			CmdEvent event = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.type_create", "TypeCreateMainEvent");
			event.addProp("schemaId", treeNode.getParentBaseTreeNode().
					getMetaTreeNodeBean().getId()+"");
			event.addProp("schemaName", treeNode.getParentBaseTreeNode().
					getMetaTreeNodeBean().getName());
			HHEvent re = htree.sendEvent(event);
			if(re instanceof ErrorEvent){
				JOptionPane.showMessageDialog(null, ((ErrorEvent) re).getErrorMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
	
}
