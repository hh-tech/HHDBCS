package com.hh.hhdb_admin.test.toolbar;

import com.hh.frame.common.util.ClassLoadUtil;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.GuiEngine;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.test.MainTestMgr;

/**
 * @author: Jiang
 * @date: 2020/10/15
 */

public class ToolbarMgrTest {

    public static void main(String[] args) throws Exception {
        try {
            HHSwingUi.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String jStr = ClassLoadUtil.loadTextRes(ToolbarMgrTest.class, "conf.json");
        JsonObject jObj = Json.parse(jStr).asObject();
        StartUtil.eng = new GuiEngine(CsMgrEnum.class, jObj);
        StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainTestMgr.CMD_SHOW));
    }

}
