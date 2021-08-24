package com.hh.hhdb_admin.test.main_frame;

import com.hh.frame.common.util.ClassLoadUtil;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.GuiEngine;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.main_frame.MainFrameMgr;

import javax.swing.*;

/**
 * @author: Jiang
 * @date: 2020/10/12
 */

public class MainFrameMgrTest {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrameMgrTest::init);
    }

    private static void init() {
        try {
            String jStr = ClassLoadUtil.loadTextRes(MainFrameMgrTest.class, "conf.json");
            JsonObject jObj = Json.parse(jStr).asObject();
            StartUtil.eng = new GuiEngine(CsMgrEnum.class, jObj);
            StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainFrameMgr.CMD_SHOW));

            //设置content
            HPanel contentPanel = new HPanel(new HDivLayout(GridSplitEnum.C6));
            contentPanel.add(new HButton("隐藏Toolbar") {
                @Override
                protected void onClick() {
                    JsonObject jsonObject = GuiJsonUtil.toJsonCmd(MainFrameMgr.SET_TOOLBAR_VISIBLE);
                    jsonObject.set("visible", false);
                    StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, jsonObject);
                }
            });
            contentPanel.add(new HButton("显示Toolbar") {
                @Override
                protected void onClick() {
                    JsonObject jsonObject = GuiJsonUtil.toJsonCmd(MainFrameMgr.SET_TOOLBAR_VISIBLE);
                    jsonObject.set("visible", true);
                    StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, jsonObject);
                }
            });

            contentPanel.add(new HButton("隐藏Menubar") {
                @Override
                protected void onClick() {
                    JsonObject jsonObject = GuiJsonUtil.toJsonCmd(MainFrameMgr.SET_MENUBAR_VISIBLE);
                    jsonObject.set("visible", false);
                    StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, jsonObject);
                }
            });
            contentPanel.add(new HButton("显示Menubar") {
                @Override
                protected void onClick() {
                    JsonObject jsonObject = GuiJsonUtil.toJsonCmd(MainFrameMgr.SET_MENUBAR_VISIBLE);
                    jsonObject.set("visible", true);
                    StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, jsonObject);
                }
            });

            contentPanel.add(new HButton("隐藏status") {
                @Override
                protected void onClick() {
                    JsonObject jsonObject = GuiJsonUtil.toJsonCmd(MainFrameMgr.SET_STATUS_VISIBLE);
                    jsonObject.set("visible", false);
                    StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, jsonObject);
                }
            });
            contentPanel.add(new HButton("显示status") {
                @Override
                protected void onClick() {
                    JsonObject jsonObject = GuiJsonUtil.toJsonCmd(MainFrameMgr.SET_STATUS_VISIBLE);
                    jsonObject.set("visible", true);
                    StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, jsonObject);
                }
            });
            String contentId = StartUtil.eng.push2SharedMap(contentPanel);
            JsonObject reqObj = GuiJsonUtil.toJsonCmd(MainFrameMgr.ADD_TAB_PANE_ITEM);
            reqObj.add("id", contentId);
            reqObj.add("title", "测试页面");
            StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, reqObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
