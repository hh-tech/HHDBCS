package com.hh.hhdb_admin.mgr.obj_query.handler;

import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.attribute.AttributeComp;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.handler.action.AttributeHandler;

/**
 * @author ouyangxu
 * @date 2021-07-30 0030 16:53:24
 */
public class ObjAttributeHandler extends AttributeHandler {
	@Override
	public void resolve(CsFolderBean treeNode) throws Exception {
		AttributeComp comp = new AttributeComp();
		comp.showAttr(initJsonObject(treeNode), CsVarUtil.jdbc, CsVarUtil.conn);
	}
}
