package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.hhdb_admin.mgr.tree.CsFolderBean;

public class RefreshHandler extends AbsHandler {

	@Override
	public void resolve(CsFolderBean treeNode) throws Exception {
		if (tree != null && tree.getLeftDoubleHandler() != null) {
			treeNode.getMrNode().setFilter("");
			treeNode.getMrNode().setToolTip(null);
			tree.getLeftDoubleHandler().refreshNode(treeNode.getId());
		}
	}
}
