package com.hh.hhdb_admin.test.login;

import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.BaseOpEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.login.LoginComp;


public class TestLoginMgr {

	public static void main(String[] args) throws Exception {
		CsVarUtil.init(TestLoginMgr.class,"conf.json");
		
        LoginComp comp = new LoginComp() {
            @Override
            public void inform() {
                CsVarUtil.eng2.doCall(CsMgrEnum.MAIN_FRAME, new BaseOpBean(BaseOpEnum.INIT));
            }
        };
        comp.showLogin();
	}
}
