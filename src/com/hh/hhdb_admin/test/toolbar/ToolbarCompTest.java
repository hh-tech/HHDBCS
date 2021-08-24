package com.hh.hhdb_admin.test.toolbar;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.treeMr.base.ViewType;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.toolbar.ToolbarComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

import java.io.File;
import java.sql.Connection;

/**
 * @author: Jiang
 * @date: 2020/12/23
 */

public class ToolbarCompTest {

    public static void main(String[] args) throws Exception {
        HHSwingUi.init();

        IconFileUtil.setIconBaseDir(new File("etc/icon/"));
        HDialog dialog = StartUtil.getMainDialog();
        dialog.setSize(1000, 240);
        dialog.setWindowTitle("工具栏测试");

        HPanel rootPanel = new HPanel();

        JdbcBean jdbcBean = MgrTestUtil.getJdbcBean();
        Connection conn = ConnUtil.getConn(jdbcBean);

        HPanel dbaPanel = new HPanel();
        HPanel userPanel = new HPanel();
        LoginBean dbaLogin = new LoginBean();
        dbaLogin.setViewType(ViewType.DBA);
        dbaLogin.setConnName("管理员工具栏");
        dbaLogin.setJdbc(jdbcBean);
        dbaLogin.setConn(conn);

        LoginBean userLogin = new LoginBean();
        userLogin.setConnName("用户工具栏");
        userLogin.setJdbc(jdbcBean);
        userLogin.setConn(conn);

        HBarLayout layout = new HBarLayout();
        layout.setAlign(AlignEnum.LEFT);
        dbaPanel.add(new ToolbarComp(layout));
        dbaPanel.setTitle("DBA工具栏");
        userPanel.add(new ToolbarComp(layout));
        userPanel.setTitle("USER工具栏");

        rootPanel.add(dbaPanel);
        rootPanel.add(userPanel);
        dialog.setRootPanel(rootPanel);
        dialog.show();
    }

}
