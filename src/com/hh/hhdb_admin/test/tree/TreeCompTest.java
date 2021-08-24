package com.hh.hhdb_admin.test.tree;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.treeMr.base.ViewType;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.tree.TreeComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

import java.io.File;
import java.util.Date;

/**
 * @author: Jiang
 * @date: 2020/12/21
 */

public class TreeCompTest {

    private SelectBox viewType;
    private HGridPanel viewTypeGrid;
    private final LastPanel rootPanel = new LastPanel(false);

    public static void main(String[] args) throws Exception {
        IconFileUtil.setIconBaseDir(new File("etc/icon/"));
        new TreeCompTest().show();
    }

    private void show() throws Exception {
        HHSwingUi.init();
        HDialog dialog = StartUtil.getMainDialog();
        dialog.setSize(400, 800);
        dialog.setWindowTitle("Tree插件测试");
        viewType = getViewTypeSel();
        viewTypeGrid = new HGridPanel(new HGridLayout(GridSplitEnum.C4));
        viewTypeGrid.setComp(1, new LabelInput("视图类型"));
        viewTypeGrid.setComp(2, viewType);
        load();
        HPanel panel = new HPanel();
        panel.setLastPanel(rootPanel);
        dialog.setRootPanel(panel);
        dialog.show();
    }

    private void load() {
        TreeComp treeComp = loadTree();
        LastPanel panel = new LastPanel();
        panel.setHead(viewTypeGrid.getComp());
        panel.set(treeComp.getComp());
        rootPanel.set(panel.getComp());
        rootPanel.getComp().validate();
        rootPanel.getComp().repaint();
    }

    private TreeComp loadTree() {
        ViewType view = ViewType.valueOf(viewType.getValue());
        LoginBean loginBean;
        try {
            loginBean = geneLoginBean(view);
            return TreeComp.newTreeInstance(loginBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private SelectBox getViewTypeSel() {
        SelectBox selectBox = new SelectBox();
        selectBox.addOption("管理员视图", ViewType.DBA.name());
        selectBox.addOption("普通用户视图", ViewType.USER.name());
        selectBox.addListener(e -> load());
        return selectBox;
    }

    private LoginBean geneLoginBean(ViewType viewType) throws Exception {
        JdbcBean jdbcBean = MgrTestUtil.getJdbcBean();
        LoginBean loginBean = new LoginBean();
        loginBean.setConnName("root");
        loginBean.setConn(ConnUtil.getConn(jdbcBean));
        loginBean.setLoginDate(new Date());
        loginBean.setJdbc(jdbcBean);
        loginBean.setViewType(viewType);
        return loginBean;
    }
}
