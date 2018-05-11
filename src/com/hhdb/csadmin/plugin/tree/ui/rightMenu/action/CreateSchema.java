package com.hhdb.csadmin.plugin.tree.ui.rightMenu.action;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.hhdb.csadmin.common.bean.ServerBean;
import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.ui.BaseDialog;
import com.hhdb.csadmin.plugin.tree.ui.BaseTextArea;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;
import com.hhdb.csadmin.plugin.tree.util.BaseChangeInterface;
import com.hhdb.csadmin.plugin.tree.util.TreeNodeUtil;

/**
 * 创建Schema
 * 
 * @author huyuanzhui
 * 
 */
public class CreateSchema extends JPanel implements BaseChangeInterface {

	private static final long serialVersionUID = -974885363090566734L;
	private JTextField jschema = new JTextField();
	//private JComboBox<String> jowner = new JComboBox<String>();
	private BaseTextArea comment;
	private BaseTreeNode treeNode;
	private HTree htree;

	public CreateSchema(final BaseTreeNode treeNode,HTree htree) {
		this.htree = htree;
		this.treeNode = treeNode;
		
		CmdEvent getsbEvent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "GetServerBean");
		HHEvent rsbevent = htree.sendEvent(getsbEvent);
		ServerBean serverbean = (ServerBean)rsbevent.getObj();
		
		setLayout(new BorderLayout());
		initCompant(serverbean);
		BaseDialog baseDialog = new BaseDialog(null, this, "新建模式", "");
		baseDialog.setSize(310, 280);
		baseDialog.showDialog();
	}

	private void initCompant(ServerBean serverBean) {
		comment = new BaseTextArea(100);
		comment.setRowAsColumn(3, 6);
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setPreferredSize(new Dimension(280, 180));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(new JLabel("名称："), gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0.5;
		panel.add(jschema, gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		panel.add(new JLabel("注释："), gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.weightx = 0.5;
		panel.add(comment, gbc);
		add(panel, BorderLayout.NORTH);
		repaint();
	}

	@Override
	public boolean execute() {
		String schemaName = jschema.getText();
		if (schemaName.trim().equals("")) {
			JOptionPane.showMessageDialog(null, "模式名不能为空！！", "提示", JOptionPane.INFORMATION_MESSAGE);
		} else if(schemaName.indexOf("\"")>0){
			JOptionPane.showMessageDialog(null, "模式名不能含有此类字符！", "提示", JOptionPane.INFORMATION_MESSAGE);
		} else if(schemaName.indexOf("\'")>0){
			JOptionPane.showMessageDialog(null, "模式名不能含有此类字符！", "提示", JOptionPane.INFORMATION_MESSAGE);
		}
		
		else {	
		
			CmdEvent getsbEvent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "GetServerBean");
			HHEvent rsbevent = htree.sendEvent(getsbEvent);
			ServerBean serverbean = (ServerBean)rsbevent.getObj();
			
			String commentText = comment.getText();
			StringBuilder sql = new StringBuilder(50);
			sql.append("CREATE SCHEMA \"");
			sql.append(schemaName);
			sql.append("\"\r\n");
			sql.append("AUTHORIZATION ");
			sql.append(serverbean.getUserName()).append(";");
			if (!commentText.trim().isEmpty()) {
				sql.append("\r\n");
				sql.append("COMMENT ON SCHEMA ");
				sql.append(schemaName);
				sql.append("\r\n");
				sql.append(" IS ");
				sql.append("'").append(commentText).append("'");
			}
			String toID="com.hhdb.csadmin.plugin.conn";
			CmdEvent event = new CmdEvent(htree.PLUGIN_ID, toID, "ExecuteUpdateBySqlEvent");
			event.addProp("sql_str", sql.toString());
			HHEvent ev = htree.sendEvent(event);
			if(ev instanceof ErrorEvent){
				JOptionPane.showMessageDialog(null, ((ErrorEvent) ev).getErrorMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}else{
//				BaseTreeNode temptreenode = new BaseTreeNode();
//				MetaTreeNodeBean mtn = new MetaTreeNodeBean();
//				mtn.setName(schemaName);
//				mtn.setOpenIcon("schema.png");
//				mtn.setType(TreeNodeUtil.SCHEMA_ITEM_TYPE);
//				mtn.setUnique(true);
//				temptreenode.setMetaTreeNodeBean(mtn);
				
				if(treeNode.getType().equals(TreeNodeUtil.SCHEMA_ITEM_TYPE)){
//					temptreenode.setParentBaseTreeNode(treeNode.getParentBaseTreeNode());
//					((BaseTree)(htree.getComponent())).getTreeModel().insertNodeInto
//					(temptreenode,treeNode.getParentBaseTreeNode(),treeNode.getParentBaseTreeNode().getChildCount());
					try {
						htree.treeService.refreshSchemaCollection(treeNode.getParentBaseTreeNode());
					} catch (Exception e) {
						LM.error(LM.Model.CS.name(), e);
						JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
					}
				}else{
//					temptreenode.setParentBaseTreeNode(treeNode);
//					((BaseTree)(htree.getComponent())).getTreeModel().insertNodeInto
//					(temptreenode,treeNode,treeNode.getChildCount());
					try {
						htree.treeService.refreshSchemaCollection(treeNode);
					} catch (Exception e) {
						LM.error(LM.Model.CS.name(), e);
						JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
					}
				}
				
				
			}
		}
		return true;
	}
}
