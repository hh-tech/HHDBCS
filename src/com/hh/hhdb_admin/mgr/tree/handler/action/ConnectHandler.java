package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;

/**
 * 连接
 */
public class ConnectHandler extends AbsHandler {

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        if (!ConnUtil.isConnected(loginBean.getConn())) {
            LoginBean loginBean = StartUtil.getLoginBean();
            loginBean.setConn(ConnUtil.getConn(loginBean.getJdbc()));
        }
    }
}
