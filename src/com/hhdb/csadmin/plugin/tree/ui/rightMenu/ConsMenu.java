package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import com.hh.frame.common.log.LM;
import com.hh.frame.dbobj.hhdb.HHdbCk;
import com.hh.frame.dbobj.hhdb.HHdbFk;
import com.hh.frame.dbobj.hhdb.HHdbPk;
import com.hh.frame.dbobj.hhdb.HHdbUk;
import com.hh.frame.dbobj.hhdb.HHdbXk;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;
import com.hhdb.csadmin.plugin.tree.util.TreeNodeUtil;

/**
 *约束右键菜单
 * @author 胡圆锥
 *
 */
public class ConsMenu extends BasePopupMenu{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  final String DELETE = "DELETE";
	private  BaseTreeNode treeNode;
	
	private HTree htree = null;
	
	public ConsMenu(HTree htree){
		this.htree = htree;
		add(createMenuItem("删除约束", DELETE, this));
	}
	
	public  ConsMenu getInstance(BaseTreeNode node) {
		treeNode = node;
		return this;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		if (actionCmd.equals(DELETE)) {//删除类型
			int result=JOptionPane.showConfirmDialog(null, "是否删除","删除约束",JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.YES_OPTION){
				try{
					if(treeNode.getType().equals(TreeNodeUtil.CONSTRAINT_PK_ITEM_TYPE)) {
						HHdbPk hhdbpk = (HHdbPk)treeNode.getNodeObject();
						hhdbpk.dropCascade();
					}else if(treeNode.getType().equals(TreeNodeUtil.CONSTRAINT_FK_ITEM_TYPE)) {
						HHdbFk hhdbfk = (HHdbFk)treeNode.getNodeObject();
						hhdbfk.drop();
					}else if(treeNode.getType().equals(TreeNodeUtil.CONSTRAINT_UK_ITEM_TYPE)) {
						HHdbUk hhdbuk = (HHdbUk)treeNode.getNodeObject();
						hhdbuk.dropCascade();
					}else if(treeNode.getType().equals(TreeNodeUtil.CONSTRAINT_CK_ITEM_TYPE)) {
						HHdbCk hhdbck = (HHdbCk)treeNode.getNodeObject();
						hhdbck.drop();
					}else if(treeNode.getType().equals(TreeNodeUtil.CONSTRAINT_XK_ITEM_TYPE)) {
						HHdbXk hhdbxk = (HHdbXk)treeNode.getNodeObject();
						hhdbxk.drop();
					}
					htree.treeService.refreshConstraint(treeNode.getParentBaseTreeNode());
				}catch(Exception ee){
					LM.error(LM.Model.CS.name(), ee);
					JOptionPane.showMessageDialog(null, ee.getMessage(), "错误",
							JOptionPane.ERROR_MESSAGE);
				}
			}						
		} 
	}
}
