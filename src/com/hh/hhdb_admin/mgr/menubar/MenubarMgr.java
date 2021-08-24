package com.hh.hhdb_admin.mgr.menubar;

import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.main_frame.MainFrameMgr;

/**
 * @author: Jiang
 * @date: 2020/10/12
 */
public class MenubarMgr extends AbsGuiMgr {

    public static final String CMD_INIT = "INIT";
    public static final String CMD_SHOW_LICENSE = "CMD_SHOW_LICENSE";
    public static final String CMD_SHOW_VERSION = "CMD_SHOW_VERSION";
    public static final String CMD_SHOW_CONVERSION = "CMD_SHOW_CONVERSION";
    public static final String CMD_SHOW_SETTING = "CMD_SHOW_SETTING";

    @Override
    public void init(JsonObject jObj) {
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.MENUBAR;
    }

    @Override
    public void doPush(JsonObject msg) throws Exception {
        String cmd = GuiJsonUtil.toStrCmd(msg);
        switch (cmd) {
            case CMD_SHOW_LICENSE:
                new LicenseComp(false);
                break;
            case CMD_SHOW_VERSION:
                new VersionInfoComp();
                break;
            case CMD_SHOW_CONVERSION:
                SqlConversionComp sql = new SqlConversionComp();
                String id = StartUtil.eng.push2SharedMap(sql.getPanel());
                StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainFrameMgr.ADD_TAB_PANE_ITEM)
                        .add(StartUtil.CMD_ID,id).add("title", MenubarComp.getLang("sql_Conversion")).add(MainFrameMgr.PARAM_MGR_TYPE, CsMgrEnum.MENUBAR.name()));
                break;
            case CMD_SHOW_SETTING:
                new SettingsComp();
                break;
            default:
        }
    }

    @Override
    public JsonObject doCall(JsonObject msg) throws Exception {
        JsonObject res = new JsonObject();
        if (CMD_INIT.equals(GuiJsonUtil.toStrCmd(msg))) {
            MenubarComp menubarComp = new MenubarComp();
            String id = StartUtil.eng.push2SharedMap(menubarComp);
            res.add("id", id);
        } else {
            unknowMsg(msg.toPrettyString());
        }
        return res;
    }
}
