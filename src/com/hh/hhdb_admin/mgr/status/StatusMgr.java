package com.hh.hhdb_admin.mgr.status;

import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;

/**
 * @author: Jiang
 * @date: 2020/10/12
 */

public class StatusMgr extends AbsGuiMgr {
    public static final String CMD_INIT = "INIT";

    @Override
    public void init(JsonObject jObj) {

    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.STATUS;
    }

    @Override
    public void doPush(JsonObject msg) throws Exception {

    }

    @Override
    public JsonObject doCall(JsonObject msg) throws Exception {
        JsonObject res = new JsonObject();
        if (CMD_INIT.equals(GuiJsonUtil.toStrCmd(msg))) {
            StatusComp statusComp = new StatusComp();
            String id = StartUtil.eng.push2SharedMap(statusComp);
            res.add("id", id);
        } else {
            unknowMsg(msg.toPrettyString());
        }
        return res;
    }
}
