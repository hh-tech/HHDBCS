package com.hh.hhdb_admin.test.login;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.swingui.laf.LafIdEnum;
import com.hh.frame.swingui.laf.LafMgr;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.conf.CsConf;
import com.hh.hhdb_admin.mgr.login.LoginComp;


public class TestLoginComp {

    public static void main(String[] args) throws Exception {
		CsVarUtil.CONF=new CsConf();
		LangMgr.addLang(CsL.class);
		LafMgr.setLaf(LafIdEnum.FAST);
        LoginComp comp = new LoginComp() {
            @Override
            public void inform() {
            	System.out.println("登录成功,退出系统");
            	System.exit(0);
            }
        };
        comp.showLogin();
    }

}
