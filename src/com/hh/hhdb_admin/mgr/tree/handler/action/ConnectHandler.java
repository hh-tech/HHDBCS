package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;

/**
 * 连接
 */
public class ConnectHandler extends AbsHandler {

    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        if (!CsVarUtil.isConnected()) {
        	CsVarUtil.doConn();
        }
    }
}
