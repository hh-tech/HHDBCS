package com.hh.hhdb_admin.mgr.obj_query.handler;

import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.mgr.attribute.AttributeComp;
import com.hh.hhdb_admin.mgr.tree.handler.action.AttributeHandler;

/**
 * @author ouyangxu
 * @date 2021-07-30 0030 16:53:24
 */
public class ObjAttributeHandler extends AttributeHandler {
	@Override
	public void resolve(HTreeNode treeNode) throws Exception {
		JsonObject object = initJsonObject(treeNode);
		AttributeComp comp = new AttributeComp();
		comp.showAttr(object, loginBean.isSshAuth() ? loginBean.getOriginalJdbc() : loginBean.getJdbc(), loginBean.getConn());
	}
}
