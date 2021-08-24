package com.hh.hhdb_admin.test.table;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.ClassLoadUtil;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.GuiEngine;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.table.TableMgr;
import com.hh.hhdb_admin.test.MgrTestUtil;

import java.util.Date;

/**
 * @author oyx
 * @date 2020-10-16  0016 18:37:55
 */
public class TableMgrTest {
	public static void main(String[] args) throws Exception {
		//初始化自定义UI
		HHSwingUi.init();

		String conf = ClassLoadUtil.loadTextRes(TableMgrTest.class, "conf.json");
		JsonObject jObj = Json.parse(conf).asObject();
		StartUtil.eng = new GuiEngine(CsMgrEnum.class, jObj);
		JdbcBean jdbc = MgrTestUtil.getJdbcBean();
		LoginBean loginBean = new LoginBean();
		loginBean.setJdbc(jdbc);
		loginBean.setLoginDate(new Date());
		String loginId = StartUtil.eng.push2SharedMap(loginBean);

		JsonObject object = new JsonObject();
		object.add("loginId", loginId);
		object.add("cmd", TableMgr.CMD_SHOW_ADD_TABLE);
		StartUtil.eng.doPush(CsMgrEnum.TABLE, object);
	}
}
