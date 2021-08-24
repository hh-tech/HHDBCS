package com.hh.hhdb_admin.test.index;

import com.hh.frame.common.base.JdbcBean;
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

/**
 * @author yangxianhui
 */
public class IndexMgrTest {

    public static void main(String[] args) throws Exception {
        //初始化自定义UI
        HHSwingUi.init();
        IconFileUtil.setIconBaseDir(new File("etc/icon/"));
        String jStr = ClassLoadUtil.loadTextRes(IndexMgrTest.class, "conf.json");
        JsonObject jObj = Json.parse(jStr).asObject();
        GuiEngine eng = new GuiEngine(CsMgrEnum.class, jObj);
        StartUtil.eng = eng;
        JdbcBean jdbcBean = MgrTestUtil.getJdbcBean();
        if (jdbcBean != null) {
            LoginBean loginBean = new LoginBean();
            loginBean.setJdbc(jdbcBean);
            loginBean.setConn(ConnUtil.getConn(jdbcBean));
            LoginComp.loginBean = loginBean;
            LoginMgr.loginBeanId = StartUtil.eng.push2SharedMap(loginBean);
            eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainTestMgr.CMD_SHOW));
        }
    }
}
