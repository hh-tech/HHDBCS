package com.hh.hhdb_admin.test.function;

import java.io.File;
import java.io.IOException;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.ClassLoadUtil;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.engine.GuiEngine;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.login.LoginComp;
import com.hh.hhdb_admin.mgr.login.LoginMgr;
import com.hh.hhdb_admin.test.MainTestMgr;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class FunMgrTest {

	public static void main(String[] args) throws Exception {
		IconFileUtil.setIconBaseDir(new File("etc/icon/"));
		try {
            LangMgr2.loadMerge(FunctionMgr.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
		HHSwingUi.init();
		String jStr= ClassLoadUtil.loadTextRes(FunMgrTest.class, "conf.json");
		JsonObject jObj=Json.parse(jStr).asObject();
		StartUtil.eng=new GuiEngine(CsMgrEnum.class,jObj);

		//设置测试连接
		JdbcBean jb = MgrTestUtil.getJdbcBean();
		LoginBean loginBean = new LoginBean();
		loginBean.setConn(ConnUtil.getConn(jb));
		
		DBTypeEnum dbType = DriverUtil.getDbType(jb);
		if (dbType.equals(DBTypeEnum.oracle)) jb.setSchema(jb.getUser());
		
		loginBean.setJdbc(jb);
		LoginComp.loginBean = loginBean;
		LoginMgr.loginBeanId = StartUtil.eng.push2SharedMap(loginBean);

		StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainTestMgr.CMD_SHOW));
	}

}
