package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.swingui.view.tree.HTreeNode;

/**
 * 处理刷新事件
 *
 * @author Jiang
 * @date 2020/9/15
 */

public class RefreshHandler extends AbsHandler {

	@Override
	public void resolve(HTreeNode treeNode) throws Exception {
		treeNode.removeAllChildren();
		if (tree != null && tree.getLeftDoubleHandler() != null) {
			tree.getLeftDoubleHandler().refreshNode(treeNode);
		}
	}
}
