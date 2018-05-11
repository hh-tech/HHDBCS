package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;


/**
 * 函数集合右键菜单
 * 
 * @author huyuanzhui
 * 
 */
public class FunctionsMenu extends BasePopupMenu {

	private static final long serialVersionUID = 1L;
	private  final String REFRESH = "refresh";
	private  final String CREATEFUNCTION= "createFunction";
	private  final String CheckFuncs= "CheckFuncs";
	private  BaseTreeNode treeNode;

	private HTree htree = null;
	
	public FunctionsMenu(HTree htree){
		this.htree = htree;
//		add(createMenuItem("验证所有函数", CheckFuncs, this));
		add(createMenuItem("新建函数", CREATEFUNCTION, this));
		add(createMenuItem("刷新", REFRESH, this));
	}
	
	public FunctionsMenu getInstance(BaseTreeNode node) {
		treeNode = node;
		return this;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		if (actionCmd.equals(REFRESH)) {
			try {
				htree.treeService.refreshFunction(treeNode);
			} catch (Exception e1) {
				LM.error(LM.Model.CS.name(), e1);
				JOptionPane.showMessageDialog(null, e1.getMessage(), "错误",
						JOptionPane.ERROR_MESSAGE);
			}
		} else if (actionCmd.equals(CREATEFUNCTION)) {
			String toID = "com.hhdb.csadmin.plugin.function";
			CmdEvent event = new CmdEvent(htree.PLUGIN_ID, toID, "FunctionCreateMainEvent");
			event.addProp("schemaName", treeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getName());
			HHEvent revent = htree.sendEvent(event);
			if(revent instanceof ErrorEvent){
				JOptionPane.showMessageDialog(null, ((ErrorEvent)revent).getErrorMessage(), "错误",
						JOptionPane.ERROR_MESSAGE);
			}
		}else if (actionCmd.equals(CheckFuncs)) {
		}
	}
}
