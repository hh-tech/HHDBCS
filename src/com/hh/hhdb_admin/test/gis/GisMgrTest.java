package com.hh.hhdb_admin.test.gis;

import com.hh.frame.common.util.ClassLoadUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.GuiEngine;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.login.LoginComp;
import com.hh.hhdb_admin.mgr.login.LoginMgr;
import com.hh.hhdb_admin.test.MainTestMgr;
import com.hh.hhdb_admin.test.MgrTestUtil;

import java.io.File;

public class GisMgrTest {

	public static void main(String[] args) throws Exception {
		IconFileUtil.setIconBaseDir(new File("etc/icon/"));
		HHSwingUi.init();
		String jStr= ClassLoadUtil.loadTextRes(GisMgrTest.class, "conf.json");
		JsonObject jObj=Json.parse(jStr).asObject();
		StartUtil.eng=new GuiEngine(CsMgrEnum.class,jObj);

		//设置测试连接
		LoginBean loginBean = new LoginBean();
		loginBean.setConn(ConnUtil.getConn(MgrTestUtil.getJdbcBean()));
		loginBean.setJdbc(MgrTestUtil.getJdbcBean());
		LoginComp.loginBean = loginBean;
		LoginMgr.loginBeanId = StartUtil.eng.push2SharedMap(loginBean);

		StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainTestMgr.CMD_SHOW));
	}

}
