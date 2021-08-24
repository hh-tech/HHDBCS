package com.hh.hhdb_admin.mgr.about;

import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangEnum;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.engine.GuiMsgType;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;

public class AboutMgr extends AbsGuiMgr {
    public static final String CMD_SHOW_ABOUT = "SHOW_ABOUT";
    private AboutComp aboutComp;
    private LangEnum language;

    @Override
    public void init(JsonObject jObj) {
        aboutComp = new AboutComp();
        language = StartUtil.default_language;
    }


    @Override
    public CsMgrEnum getType() {
        return CsMgrEnum.ABOUT;
    }

    @Override
    public String getHelp() {
        return GuiJsonUtil.genCmdHelp(CMD_SHOW_ABOUT, "显示关于页面", GuiMsgType.RECE);
    }


    @Override
    public void doPush(JsonObject msg) throws Exception {
        changeLanguage();
        String cmd = GuiJsonUtil.toStrCmd(msg);
        if (cmd.equals(CMD_SHOW_ABOUT)) {
            aboutComp.show();
        } else {
            unknowMsg(msg.toPrettyString());
        }
    }

    @Override
    public JsonObject doCall(JsonObject msg) throws Exception {
        return null;
    }

    private void changeLanguage() {
        if (language != StartUtil.default_language) {
            aboutComp = new AboutComp();
            language = StartUtil.default_language;
        }
    }


}
