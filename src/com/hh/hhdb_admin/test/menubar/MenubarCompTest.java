package com.hh.hhdb_admin.test.menubar;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.treeMr.base.ViewType;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.menubar.MenubarComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

import java.sql.Connection;

/**
 * @author: Jiang
 * @date: 2020/12/23
 */

public class MenubarCompTest {

    public static void main(String[] args) throws Exception {
        HHSwingUi.init();
        HDialog dialog = StartUtil.getMainDialog();
        dialog.setSize(1000, 200);
        dialog.setWindowTitle("菜单栏测试");


        JdbcBean jdbcBean = MgrTestUtil.getJdbcBean();
        Connection conn = ConnUtil.getConn(jdbcBean);

        HPanel rootPanel = new HPanel();
        HPanel dbaPanel = new HPanel();
        HPanel userPanel = new HPanel();
        LoginBean dbaLogin = new LoginBean();
        dbaLogin.setViewType(ViewType.DBA);
        dbaLogin.setConnName("管理员菜单栏");
        dbaLogin.setJdbc(jdbcBean);
        dbaLogin.setConn(conn);

        LoginBean userLogin = new LoginBean();
        userLogin.setViewType(ViewType.USER);
        userLogin.setConnName("用户菜单栏");
        userLogin.setJdbc(jdbcBean);
        userLogin.setConn(conn);

        dbaPanel.add(new MenubarComp());
        dbaPanel.setTitle("DBA菜单栏");
        userPanel.add(new MenubarComp());
        userPanel.setTitle("USER菜单栏");

        rootPanel.add(dbaPanel);
        rootPanel.add(userPanel);

        dialog.setRootPanel(rootPanel);
        dialog.show();
    }

}
