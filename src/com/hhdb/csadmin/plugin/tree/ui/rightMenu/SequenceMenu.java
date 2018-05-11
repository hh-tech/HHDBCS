package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import com.hhdb.csadmin.common.bean.ServerBean;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;

public class SequenceMenu extends BasePopupMenu{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  final String DESIGNSEQUENCE = "designSequence";
	private  final String REFRESHSEQUENCE = "refreshSequence";
	private  final String DELETESEQUENCE = "deleteSequence";//RENAMESEQUENCE
	private  final String RENAMESEQUENCE = "renameSequence";//CREATSEQUENCE
	private  final String CREATSEQUENCE = "creatSEQUENCE";
	private  BaseTreeNode treeNode;
	
	private HTree htree = null;
	
	public SequenceMenu(HTree htree){
		this.htree = htree;
		add(createMenuItem("新建序列", CREATSEQUENCE, this));
		add(createMenuItem("设计序列", DESIGNSEQUENCE, this));
		add(createMenuItem("删除序列", DELETESEQUENCE, this));
		add(createMenuItem("重命名", RENAMESEQUENCE, this));
		add(createMenuItem("刷新", REFRESHSEQUENCE, this));
	}
	
	public  SequenceMenu getInstance(BaseTreeNode node) {
		treeNode = node;
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		String toID = "com.hhdb.csadmin.plugin.sequence";
		CmdEvent getsbEvent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "GetServerBean");
		HHEvent rsbevent = htree.sendEvent(getsbEvent);
		ServerBean serverbean = (ServerBean)rsbevent.getObj();
		
		if (actionCmd.equals(DESIGNSEQUENCE)) {//设计序列
			CmdEvent event = new CmdEvent(htree.PLUGIN_ID, toID, "editSequenceEvent");
			event.addProp("databaseName", serverbean.getDBName());
			event.addProp("schemaName", treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName());
			event.addProp("seqName", treeNode.getMetaTreeNodeBean().getName());
			event.addProp("seqOid", treeNode.getMetaTreeNodeBean().getId()+"");
			HHEvent revent = htree.sendEvent(event);
			if(revent instanceof ErrorEvent){
				JOptionPane.showMessageDialog(null, ((ErrorEvent)revent).getErrorMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}else if(actionCmd.equals(CREATSEQUENCE)) {//序列名下新建序列
			CmdEvent event = new CmdEvent(htree.PLUGIN_ID, toID, "createSequenceEvent");
			event.addProp("databaseName", serverbean.getDBName());
			event.addProp("schemaName", treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName());
			HHEvent revent = htree.sendEvent(event);
			if(revent instanceof ErrorEvent){
				JOptionPane.showMessageDialog(null, ((ErrorEvent)revent).getErrorMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(actionCmd.equals(DELETESEQUENCE)){//删除序列
			int n = JOptionPane.showConfirmDialog(null, "这将删除此序列及相关对象，你确定想要继续吗？", "温馨提示", JOptionPane.YES_NO_OPTION);
			try{
				if (n == 0) {
					String schemaName=treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName();
					String seqName=treeNode.getMetaTreeNodeBean().getName();
					String sql="DROP SEQUENCE \"" +schemaName+"\".\""+seqName+"\" cascade;";
					htree.treeService.executeSql(sql);
					htree.treeService.refreshSequence(treeNode.getParentBaseTreeNode());
				}
			}catch(Exception ex){
				JOptionPane.showMessageDialog(null, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(actionCmd.equals(RENAMESEQUENCE)){//重命名
			String schemaName=treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName();
			String seqName=treeNode.getMetaTreeNodeBean().getName();
			//弹窗重命名
			String sequenceName = JOptionPane.showInputDialog(null, "输入新序列名", "新序列名", JOptionPane.PLAIN_MESSAGE);
			try{
				if(!"".equals(sequenceName)&&!(sequenceName==null)){
					String sql="alter sequence\""+schemaName+"\".\""+seqName+"\" rename to \""+sequenceName+"\"";
					htree.treeService.executeSql(sql);
					htree.treeService.refreshSequence(treeNode.getParentBaseTreeNode());
				}
				//刷新
			}catch(Exception ex){
				JOptionPane.showMessageDialog(null, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}else if(actionCmd.equals(REFRESHSEQUENCE)){//刷新

		}
		
	}
}
