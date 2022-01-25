package com.hh.hhdb_admin.test.delete;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.mgr.delete.DeleteComp;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;
import com.hh.hhdb_admin.mgr.delete.OperateType;
import com.hh.hhdb_admin.mgr.login.LoginBean;

import java.util.ArrayList;

/**
 * @Author: Jiang
 * @Date: 2021/9/16 10:12
 */
public class DeleteCompTest {

    public static void main(String[] args) throws Exception {
        HHSwingUi.init();
        NodeInfo nodeInfo = new NodeInfo("aaa", "test", null, TreeMrType.TABLE, OperateType.DEL);

        LoginBean loginBean = new LoginBean();
        JdbcBean jdbcBean = new JdbcBean();
        jdbcBean.setSchema("test");
        jdbcBean.setUser("root");
        jdbcBean.setPassword("111111");
        jdbcBean.setDbUrl("jdbc:mysql://127.0.0.1:3306/mysql?zeroDateTimeBehavior=CONVERT_TO_NULL&rewriteBatchedStatements=true&useCursorFetch=true&serverTimezone=Asia/Shanghai&&useSSL=false&allowPublicKeyRetrieval=true&useCursorFetch=true");
        jdbcBean.setClazz(DriverUtil.MYSQL_DRIVER);
        loginBean.setJdbc(jdbcBean);

        new DeleteComp(new ArrayList<NodeInfo>() {
			private static final long serialVersionUID = 1L;

		{
            add(nodeInfo);
        }}, loginBean, new HFrame());

    }
}
