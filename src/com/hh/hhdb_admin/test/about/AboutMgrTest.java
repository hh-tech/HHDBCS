package com.hh.hhdb_admin.test.about;

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
import com.hh.hhdb_admin.mgr.about.AboutMgr;

public class AboutMgrTest {
    public static void main(String[] args) throws Exception {
        try {
            HHSwingUi.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        IconFileUtil.setIconBaseDir(new File("etc/icon/"));
        String jStr = ClassLoadUtil.loadTextRes(AboutMgrTest.class, "conf.json");
        JsonObject jObj = Json.parse(jStr).asObject();
        GuiEngine eng = new GuiEngine(CsMgrEnum.class, jObj);
        StartUtil.eng = eng;
        eng.doPush(CsMgrEnum.ABOUT, GuiJsonUtil.toJsonCmd(AboutMgr.CMD_SHOW_ABOUT));
        System.out.println(eng.getHelp(CsMgrEnum.ABOUT));
    }

}
