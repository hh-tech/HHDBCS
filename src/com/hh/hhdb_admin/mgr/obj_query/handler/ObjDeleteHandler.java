package com.hh.hhdb_admin.mgr.obj_query.handler;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.obj_query.ObjQueryComp;
import com.hh.hhdb_admin.mgr.tree.TreeComp;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;
import com.hh.hhdb_admin.mgr.tree.handler.action.DeleteHandler;

import javax.swing.*;

/**
 * @author ouyangxu
 * @date 2021-07-29 0029 14:17:36
 */
public class ObjDeleteHandler extends DeleteHandler {
	protected ObjQueryComp queryComp;

	public ObjDeleteHandler(ObjQueryComp queryComp) {
		this.queryComp = queryComp;
	}

	@Override
	public void resolve(HTreeNode treeNode) throws Exception {
		if (!isMulti) {
			int res = JOptionPane.showConfirmDialog(null, TreeComp.getLang("sure_delete"), getLang("hint"), JOptionPane.YES_NO_OPTION);
			if (res != 0) {
				return;
			}
		}
		doDel(treeNode);
		if (queryComp != null) {
			queryComp.search();
			//刷新树节点
			StartUtil.eng.doPush(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH)
					.add(TreeMgr.PARAM_NODE_TYPE, TreeMrType.TABLE_GROUP.name())
					.add(StartUtil.PARAM_SCHEMA, getSchemaName()));
		}
	}

	public ObjQueryComp getQueryComp() {
		return queryComp;
	}

	public void setQueryComp(ObjQueryComp queryComp) {
		this.queryComp = queryComp;
	}
}
