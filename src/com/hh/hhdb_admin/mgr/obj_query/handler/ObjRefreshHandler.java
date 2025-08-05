package com.hh.hhdb_admin.mgr.obj_query.handler;

import com.hh.hhdb_admin.mgr.obj_query.comp.AbsQueryComp;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.handler.action.AbsHandler;

/**
 * @author ouyangxu
 * @date 2021-07-29 0029 9:39:30
 */
public class ObjRefreshHandler extends AbsHandler {
	protected AbsQueryComp objQueryComp;

	public ObjRefreshHandler(AbsQueryComp objQueryComp) {
		this.objQueryComp = objQueryComp;
	}

	@Override
	public void resolve(CsFolderBean treeNode) throws Exception {
		if (objQueryComp != null) {
			objQueryComp.search();
		}

	}

}
