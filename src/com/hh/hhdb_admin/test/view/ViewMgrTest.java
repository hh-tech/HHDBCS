package com.hh.hhdb_admin.test.view;

import com.hh.frame.common.util.ClassLoadUtil;
import com.hh.frame.common.util.SleepUtil;
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
import com.hh.hhdb_admin.mgr.view.ViewMgr;
import com.hh.hhdb_admin.test.MainTestMgr;
import com.hh.hhdb_admin.test.MgrTestUtil;

import java.io.File;
import java.sql.Connection;

public class ViewMgrTest {
    public static void main(String[] args) {
        try {
            HHSwingUi.init();
            IconFileUtil.setIconBaseDir(new File("etc/icon/"));
            String jStr = ClassLoadUtil.loadTextRes(ViewMgrTest.class, "conf.json");
            JsonObject jObj = Json.parse(jStr).asObject();
            GuiEngine eng = new GuiEngine(CsMgrEnum.class, jObj);
            StartUtil.eng = eng;
            //设置测试连接
            LoginBean loginBean = new LoginBean();
            Connection conn = ConnUtil.getConn(MgrTestUtil.getJdbcBean());
            loginBean.setConn(conn);
            System.out.println("新建测试连接：" + conn.toString());
            loginBean.setJdbc(MgrTestUtil.getJdbcBean());
            String connId = eng.push2SharedMap(loginBean);
            eng.doCall(CsMgrEnum.VIEW, GuiJsonUtil.toJsonCmd(ViewMgr.CMD_SET_TEST_CONN).add(ViewMgr.PARAM_TEST_CONN_ID, connId));


            SleepUtil.sleep1000();
            eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainTestMgr.CMD_SHOW).add(MainTestMgr.PARAM_TEST_CONN_ID, connId));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
