package com.hh.hhdb_admin.test.sql_book;

import java.io.File;

import com.hh.frame.common.util.ClassLoadUtil;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.GuiEngine;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.test.MainTestMgr;

public class SqlBookMgrTest {
    public static void main(String[] args) throws Exception {
        try {
            HHSwingUi.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        IconFileUtil.setIconBaseDir(new File("etc/icon/"));
        String jStr = ClassLoadUtil.loadTextRes(SqlBookMgrTest.class, "conf.json");
        JsonObject jObj = Json.parse(jStr).asObject();
        GuiEngine eng = new GuiEngine(CsMgrEnum.class, jObj);
        StartUtil.eng = eng;
        
        eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainTestMgr.CMD_SHOW));
        System.out.println(eng.getHelp(CsMgrEnum.SQL_BOOK));
    }

}
