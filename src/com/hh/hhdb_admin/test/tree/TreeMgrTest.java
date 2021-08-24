package com.hh.hhdb_admin.test.tree;

import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.GuiEngine;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * @author: Jiang
 * @date: 2020/9/27
 */

public class TreeMgrTest {

    public static void main(String[] args) throws Exception {
        File jsonFile = new File("etc/conf.json");
        String jStr = FileUtils.readFileToString(jsonFile, "UTF-8");
        JsonObject jObj = Json.parse(jStr).asObject();
        StartUtil.eng = new GuiEngine(CsMgrEnum.class, jObj);
        StartUtil.eng.doPush(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_INIT_TEST));
    }

}
