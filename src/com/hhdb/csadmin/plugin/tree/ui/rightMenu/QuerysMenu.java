package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;

/**
 * 查询下右键菜单
 * 
 * @author hyz
 * 
 */
public class QuerysMenu extends BasePopupMenu {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BaseTreeNode treeNode;
	private final String REFRESH = "refresh";
	private final String CREATEQUERY = "createquery";

	private HTree htree = null;

	public QuerysMenu(HTree htree) {
		this.htree = htree;
		add(createMenuItem("新建查询", CREATEQUERY, this));
		add(createMenuItem("刷新", REFRESH, this));
	}

	public QuerysMenu getInstance(BaseTreeNode node) {
		treeNode = node;
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		if (actionCmd.equals(REFRESH)) {// 刷新
			try {
				htree.treeService.refreshQuery(treeNode);
			} catch (Exception ee) {
				JOptionPane.showMessageDialog(null, ee.getMessage(), "错误",
						JOptionPane.ERROR_MESSAGE);
			}
		} else if (actionCmd.equals(CREATEQUERY)) {
			String toID = "com.hhdb.csadmin.plugin.query";
			HHEvent hhEvent = new HHEvent(htree.PLUGIN_ID, toID,
					EventTypeEnum.COMMON.name());
			htree.sendEvent(hhEvent);
		}
	}
}
