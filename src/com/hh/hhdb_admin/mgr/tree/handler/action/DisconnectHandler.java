package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;

/**
 * 断开
 */
public class DisconnectHandler extends AbsHandler {

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        LoginBean loginBean = StartUtil.getLoginBean();
        ConnUtil.close(loginBean.getConn());
    }

}
