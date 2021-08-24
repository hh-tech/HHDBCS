package com.hh.hhdb_admin.mgr.pack;

import com.hh.frame.dbobj2.ora.OraSessionEnum;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.engine.GuiMsgType;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.login.LoginMgr;
import com.hh.hhdb_admin.mgr.main_frame.MainFrameMgr;

import java.sql.Connection;

/**
 * @author YuSai
 */
public class PackageMgr extends AbsGuiMgr {

    public static final String CMD_ADD = "CMD_ADD";
    public static final String CMD_DESIGN = "CMD_DESIGN";
    public static final String PACKAGE_NAME = "PACKAGE_NAME";
    public static final String HEAD_OR_BODY = "HEAD_OR_BODY";
    public static final String TYPE = "TYPE";

    @Override
    public void init(JsonObject jObj) {
    }

    @Override
    public String getHelp() {
        return GuiJsonUtil.genCmdHelp(CMD_ADD, "新增", GuiMsgType.RECE) +
                GuiJsonUtil.genCmdHelp(CMD_DESIGN, "设计", GuiMsgType.RECE);
    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.PACKAGE;
    }

    @Override
    public void doPush(JsonObject msg) throws Exception {
        String schema = GuiJsonUtil.toPropValue(msg, StartUtil.PARAM_SCHEMA);
        String name = GuiJsonUtil.toPropValue(msg, StartUtil.PARAM_TABLE);
        PackageComp packageComp = new PackageComp(getConn(), schema);
        String cmd = GuiJsonUtil.toStrCmd(msg);
        if (StartUtil.CMD_CLOSE.equals(cmd)) {
            StartUtil.eng.rmFromSharedMap(GuiJsonUtil.toPropValue(msg,StartUtil.CMD_ID));
        } else if (CMD_ADD.equals(cmd)) {
            packageComp.add(name);
        }  else if (CMD_DESIGN.equals(cmd)) {
            packageComp.design(name, OraSessionEnum.valueOf(GuiJsonUtil.toPropValue(msg, HEAD_OR_BODY)));
            StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainFrameMgr.ADD_TAB_PANE_ITEM)
                    .add(StartUtil.CMD_ID, StartUtil.eng.push2SharedMap(packageComp.getPanel()))
                    .add("title", name)
                    .add(MainFrameMgr.PARAM_MGR_TYPE, CsMgrEnum.PACKAGE.name()));
        } else{
            unknowMsg(msg.toPrettyString());
        }
    }

    @Override
    public JsonObject doCall(JsonObject msg) {
        return null;
    }

    public Connection getConn() throws Exception {
        JsonObject sharedIdObj = StartUtil.eng.doCall(CsMgrEnum.LOGIN, GuiJsonUtil.genGetShareIdMsg(LoginMgr.ObjType.LOGIN_BEAN));
        LoginBean loginBean = (LoginBean) StartUtil.eng.getSharedObj(GuiJsonUtil.toStrSharedId(sharedIdObj));
        return loginBean.getConn();
    }
}
