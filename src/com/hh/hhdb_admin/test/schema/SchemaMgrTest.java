package com.hh.hhdb_admin.test.schema;

import com.hh.frame.common.util.ClassLoadUtil;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.GuiEngine;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.test.MainTestMgr;

import java.io.IOException;

/**
 * @author: Jiang
 * @date: 2020/11/11
 */

public class SchemaMgrTest {

    public static void main(String[] args) throws IOException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        try {
            HHSwingUi.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String jStr = ClassLoadUtil.loadTextRes(SchemaMgrTest.class, "conf.json");
        JsonObject jObj = Json.parse(jStr).asObject();
        StartUtil.eng = new GuiEngine(CsMgrEnum.class, jObj);
        StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainTestMgr.CMD_SHOW));
    }

}
