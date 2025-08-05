package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.CsTreeUtil;


public class CascadeDeleteHandler extends AbsHandler {
	private DeleteHandler deleteHandler=new DeleteHandler();
    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
//        DeleteHandler deleteHandler = (DeleteHandler) RightMenuUtil.getInstance("Delete");
//        if (deleteHandler == null) {
//            return;
//        }
        deleteHandler.setSchemaName(CsTreeUtil.getSchemaName(treeNode, CsVarUtil.getDbtype()));
        deleteHandler.setJdbcBean(jdbc);
        deleteHandler.cascadeResolve(treeNode);
    }

    public void resolveMulti(CsFolderBean treeNode) throws Exception {
//        DeleteHandler deleteHandler = (DeleteHandler) RightMenuUtil.getInstance("Delete");
//        if (deleteHandler == null) {
//            return;
//        }
        deleteHandler.setSchemaName(CsTreeUtil.getSchemaName(treeNode, CsVarUtil.getDbtype()));
        deleteHandler.setJdbcBean(jdbc);
        deleteHandler.cascadeMultiResolve(treeNode);
    }
}
