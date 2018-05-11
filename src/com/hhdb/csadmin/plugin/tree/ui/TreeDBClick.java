package com.hhdb.csadmin.plugin.tree.ui;

import java.sql.Connection;

import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.common.dao.ConnService;
import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.ExtendXmlLoader;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.util.TreeNodeUtil;

/**
 * 鼠标双击处理
 * 
 * @author 胡圆锥
 * 
 */
public class TreeDBClick {
	private HTree htree;
	public TreeDBClick(HTree htree){
		this.htree = htree;
	}
	public void execute(BaseTreeNode treeNode) {
		String type = treeNode.getType();
		
		if(type.equals(TreeNodeUtil.DB_TYPE)) {
			if(treeNode.getChildCount()==0){
				try {
					htree.treeService.refreshDBCollection(treeNode);
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		}else if(type.equals(TreeNodeUtil.DB_ITEM_TYPE)) {
			
		}else if(type.equals(TreeNodeUtil.SCHEMA_TYPE)) {			
			
		}else if(type.equals(TreeNodeUtil.SCHEMA_ITEM_TYPE)) {
			if(treeNode.getChildCount()==0){
				try {
					htree.treeService.refreshSchema(treeNode);
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
			
		}else if(type.equals(TreeNodeUtil.TABLE_TYPE)) {
			if(treeNode.getChildCount()==0||htree.getTree().isExpanded(new TreePath(treeNode.getPath()))){
				try {
					htree.treeService.refreshTable(treeNode);
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
			
		}else if(type.equals(TreeNodeUtil.TABLE_ITEM_TYPE)) {
			
			
		}else if(type.equals(TreeNodeUtil.COL_TYPE)) {

		}else if(type.equals(TreeNodeUtil.CONSTRAINT_TYPE)) {
			if(treeNode.getChildCount()==0){
				try {
					htree.treeService.refreshConstraint(treeNode);
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		}else if(type.equals(TreeNodeUtil.INDEX_TYPE)) {
			if(treeNode.getChildCount()==0){
				try {
					htree.treeService.refreshIndex(treeNode);
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		}else if(type.equals(TreeNodeUtil.RULE_TYPE)) {

		}else if(type.equals(TreeNodeUtil.TABLE_TRIGGER_TYPE)) {

		}else if(type.equals(TreeNodeUtil.VIEW_TYPE)) {
			if(treeNode.getChildCount()==0){
				try {
					htree.treeService.refreshView(treeNode);
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
			
		}else if(type.equals(TreeNodeUtil.VIEW_ITEM_TYPE)) {

		}else if(type.equals(TreeNodeUtil.SEQ_TYPE)) {
			if(treeNode.getChildCount()==0){
				try {
					htree.treeService.refreshSequence(treeNode);
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
			
		}else if(type.equals(TreeNodeUtil.SEQ_ITEM_TYPE)) {
			
		}else if(type.equals(TreeNodeUtil.FUN_TYPE)) {
			if(treeNode.getChildCount()==0){
				try {
					htree.treeService.refreshFunction(treeNode);
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
			
		}else if(type.equals(TreeNodeUtil.FUN_ITEM_TYPE)) {

		}else if(type.equals(TreeNodeUtil.TYPE_TYPE)) {
			if(treeNode.getChildCount()==0){
				try {
					htree.treeService.refreshType(treeNode);
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
			
		}else if(type.equals(TreeNodeUtil.TYPE_ITEM_TYPE)) {
			
		}else if(type.equals(TreeNodeUtil.SELECT_TYPE)) {
			if(treeNode.getChildCount()==0){
				try {
					htree.treeService.refreshQuery(treeNode);
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		}else if(type.equals(TreeNodeUtil.SELECT_ITEM_TYPE)) {
			
		}else if(type.equals(TreeNodeUtil.EXTENSION_TYPE)) {
			if(treeNode.getChildCount()==0){
				try {
					CmdEvent getsbEvent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "GetServerBean");
					HHEvent revent = htree.sendEvent(getsbEvent);
					ServerBean serverbean = (ServerBean)revent.getObj();
					serverbean.setDBName(treeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getName());
					Connection conn = ConnService.createConnection(serverbean);
					ExtendXmlLoader exl = new ExtendXmlLoader(conn);
					exl.refreshExtendCollection(treeNode);
					conn.close();
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		}else if(type.equals(TreeNodeUtil.EXTENSION_PLUGIN_TYPE)) {
			
		}else if(type.equals(TreeNodeUtil.PERFORMANCE_MONITORING_TYPE)) {
			
		}else if(type.equals(TreeNodeUtil.TAB_SPACE_TYPE)) {
			if(treeNode.getChildCount()==0){
				try {
					htree.treeService.refreshTablespaceCollection(treeNode);
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		}else if(type.equals(TreeNodeUtil.TAB_SPACE_ITEM_TYPE)) {
			
		}else if(type.equals(TreeNodeUtil.LOGIN_ROLE_TYPE)) {
			if(treeNode.getChildCount()==0){
				try {
					htree.treeService.refreshUserCollection(treeNode);;
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		}else if(type.equals(TreeNodeUtil.LOGIN_ROLE_ITEM_TYPE)) {
			
		}else if(type.equals(TreeNodeUtil.GROUP_TYPE)) {
			
		}else if(type.equals(TreeNodeUtil.CPU_MONITORING_TYPE)) {
			
		}else if(type.equals(TreeNodeUtil.DISK_MONITORING_TYPE)) {
			
		}else if(type.equals(TreeNodeUtil.MEMORY_MONITORING_TYPE)) {
			
		}else if(type.equals(TreeNodeUtil.COURSE_MONITORING_TYPE)) {
			
		}else if(type.equals(TreeNodeUtil.NETWORK_MONITORING_TYPE)) {
			
		} 
	}
}
