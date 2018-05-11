package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;
import java.sql.Connection;

import javax.swing.JOptionPane;

import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.common.dao.ConnService;
import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.ExtendXmlLoader;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;

public class ExtendsMenu extends BasePopupMenu {

	private static final long serialVersionUID = 1L;

	private BaseTreeNode treeNode;

	private HTree htree = null;

	public ExtendsMenu(HTree htree) {
		this.htree = htree;
		add(createMenuItem("刷新", "ref", this));
	}

	public ExtendsMenu getInstance(BaseTreeNode node) {
		treeNode = node;
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		if (actionCmd.equals("ref")) {
			try {
				CmdEvent getsbEvent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "GetServerBean");
				HHEvent revent = htree.sendEvent(getsbEvent);
				ServerBean serverbean = (ServerBean)revent.getObj();
				serverbean.setDBName(treeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getName());
				Connection conn = ConnService.createConnection(serverbean);
				ExtendXmlLoader exl = new ExtendXmlLoader(conn);
				exl.refreshExtendCollection(treeNode);
				conn.close();
			} catch (Exception ee) {
				LM.error(LM.Model.CS.name(), ee);
				JOptionPane.showMessageDialog(null, ee.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		} 
	}
}
