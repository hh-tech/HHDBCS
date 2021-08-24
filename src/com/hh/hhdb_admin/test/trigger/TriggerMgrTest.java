package com.hh.hhdb_admin.test.trigger;

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
import com.hh.hhdb_admin.mgr.trigger.TriggerMgr;
import com.hh.hhdb_admin.test.MainTestMgr;
import com.hh.hhdb_admin.test.MgrTestUtil;

import java.io.File;

public class TriggerMgrTest {
    public static void main(String[] args) {
        try {
            try {
                HHSwingUi.init();
            } catch (Exception e) {
                e.printStackTrace();
            }
            IconFileUtil.setIconBaseDir(new File("etc/icon/"));
            String jStr=ClassLoadUtil.loadTextRes(TriggerMgrTest.class, "conf.json");
            JsonObject jObj=Json.parse(jStr).asObject();
            GuiEngine eng = new GuiEngine(CsMgrEnum.class, jObj);
            StartUtil.eng = eng;
            //设置测试连接
            LoginBean loginBean = new LoginBean();
            loginBean.setConn(ConnUtil.getConn(MgrTestUtil.getJdbcBean()));
            loginBean.setJdbc(MgrTestUtil.getJdbcBean());
            String connId = eng.push2SharedMap(loginBean);
            System.out.println("新建测试连接："+loginBean.getConn().toString());
            eng.doCall(CsMgrEnum.TRIGGER, GuiJsonUtil.toJsonCmd(TriggerMgr.CMD_SET_TEST_CONN).add(TriggerMgr.PARAM_TEST_CONN_ID,connId));
            SleepUtil.sleep1000();
            eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainTestMgr.CMD_SHOW).add(MainTestMgr.PARAM_TEST_CONN_ID,connId));

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
