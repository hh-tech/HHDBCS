package com.hh.hhdb_admin.mgr.obj_query.handler;

import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.mgr.obj_query.ObjQueryComp;
import com.hh.hhdb_admin.mgr.tree.handler.action.AbsHandler;

/**
 * @author ouyangxu
 * @date 2021-07-29 0029 9:39:30
 */
public class ObjRefreshHandler extends AbsHandler {
	protected ObjQueryComp objQueryComp;

	public ObjRefreshHandler(ObjQueryComp objQueryComp) {
		this.objQueryComp = objQueryComp;
	}

	@Override
	public void resolve(HTreeNode treeNode) throws Exception {
		if (objQueryComp != null) {
			objQueryComp.search();
		}

	}

	public ObjQueryComp getObjQueryComp() {
		return objQueryComp;
	}

	public void setObjQueryComp(ObjQueryComp objQueryComp) {
		this.objQueryComp = objQueryComp;
	}
}
