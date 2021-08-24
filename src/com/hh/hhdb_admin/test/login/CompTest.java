package com.hh.hhdb_admin.test.login;

import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.login.LoginComp;

import java.io.File;

public class CompTest {

    public static void main(String[] args) throws Exception {
        HHSwingUi.init();
        IconFileUtil.setIconBaseDir(new File("etc/icon/"));
        LoginComp comp = new LoginComp() {
            @Override
            public void inform(LoginBean loginBean) {
                System.out.println(loginBean);
            }
        };
        comp.showLogin();
    }

}
