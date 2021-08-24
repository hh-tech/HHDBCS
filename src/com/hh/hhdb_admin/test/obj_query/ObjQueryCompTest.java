package com.hh.hhdb_admin.test.obj_query;

import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangMgr;
import com.hh.frame.lang.LangUtil;
import com.hh.frame.swingui.engine.GuiEngine;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.login.LoginMgr;
import com.hh.hhdb_admin.mgr.obj_query.ObjQueryComp;
import com.hh.hhdb_admin.test.MgrTestUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.sql.Connection;

/**
 * @author Jiang
 * @date 2020/12/22
 */

public class ObjQueryCompTest {

	public static void main(String[] args) throws Exception {
		Connection conn = null;
		try {
			HFrame frame = new HFrame(800, 800);
			HHSwingUi.init();
			LangMgr.merge(ObjQueryComp.class.getName(), LangUtil.loadLangRes(ObjQueryComp.class));
			IconFileUtil.setIconBaseDir(new File("etc/icon/"));
			File configJson = new File("etc/conf.json");
			String jStr = FileUtils.readFileToString(configJson, "UTF-8");
			JsonObject jObj = Json.parse(jStr).asObject();
			StartUtil.eng = new GuiEngine(CsMgrEnum.class, jObj);

			LoginBean loginBean = new LoginBean();
			loginBean.setJdbc(MgrTestUtil.getJdbcBean());
			conn = ConnUtil.getConn(loginBean.getJdbc());
			loginBean.setConn(conn);

			LoginMgr.loginBeanId = StartUtil.eng.push2SharedMap(loginBean);

			ObjQueryComp comp = new ObjQueryComp(loginBean);
//			comp.setWindowTitle("测试");

//        comp.show();
			frame.setRootPanel(comp.getRootPanel());
			frame.show();
		} finally {
			ConnUtil.close(conn);
		}
	}
}
