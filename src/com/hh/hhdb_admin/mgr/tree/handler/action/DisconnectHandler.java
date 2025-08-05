package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;

/**
 * 断开
 */
public class DisconnectHandler extends AbsHandler {

    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
//        ConnUtil.close(CsVarUtil.conn);
    	CsVarUtil.closeConn();
    }

}
