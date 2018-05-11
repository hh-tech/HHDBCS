package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.common.dao.ConnService;
import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.ExtendXmlLoader;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;

public class ExtendMenu extends BasePopupMenu {

	private static final long serialVersionUID = 1L;

	private BaseTreeNode treeNode;

	private HTree htree = null;

	public ExtendMenu(HTree htree) {
		this.htree = htree;
		add(createMenuItem("安装", "install", this));
		add(createMenuItem("卸载", "drop", this));
	}

	public ExtendMenu getInstance(BaseTreeNode node) {
		treeNode = node;
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		CmdEvent getsbEvent = new CmdEvent(htree.PLUGIN_ID,
				"com.hhdb.csadmin.plugin.conn", "GetServerBean");
		HHEvent revent = htree.sendEvent(getsbEvent);
		ServerBean serverbean = (ServerBean) revent.getObj();
		serverbean.setDBName(treeNode.getParentBaseTreeNode()
				.getParentBaseTreeNode().getMetaTreeNodeBean().getName());
		Connection conn = null;
		try {
			conn = ConnService.createConnection(serverbean);
		} catch (Exception e1) {
			LM.error(LM.Model.CS.name(), e1);
			JOptionPane.showMessageDialog(null, e1.getMessage(), "错误",
					JOptionPane.ERROR_MESSAGE);
		}
		ExtendXmlLoader exl = new ExtendXmlLoader(conn);

		if (actionCmd.equals("install")) {
			try {
				exl.installExtend(treeNode.getMetaTreeNodeBean().getName());
				exl.refreshExtendCollection(treeNode.getParentBaseTreeNode());
			} catch (Exception e2) {
				LM.error(LM.Model.CS.name(), e2);
				JOptionPane.showMessageDialog(null, e2.getMessage(), "错误",
						JOptionPane.ERROR_MESSAGE);
			} finally {
				try {
					conn.close();
				} catch (SQLException e1) {
					LM.error(LM.Model.CS.name(), e1);
				}
			}
		} else if (actionCmd.equals("drop")) {
			try {
				exl.dropExtend(treeNode.getMetaTreeNodeBean().getName());
				exl.refreshExtendCollection(treeNode.getParentBaseTreeNode());
			} catch (Exception e2) {
				LM.error(LM.Model.CS.name(), e2);
				JOptionPane.showMessageDialog(null, e2.getMessage(), "错误",
						JOptionPane.ERROR_MESSAGE);
			} finally {
				try {
					conn.close();
				} catch (SQLException e1) {
					LM.error(LM.Model.CS.name(), e1);
				}
			}
		}
	}
}
