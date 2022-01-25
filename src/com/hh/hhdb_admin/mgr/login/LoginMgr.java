package com.hh.hhdb_admin.mgr.login;

import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.engine.GuiMsgType;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.main_frame.MainFrameMgr;

public class LoginMgr extends AbsGuiMgr {
    public static final String CMD_SHOW_LOGIN = "SHOW_LOGIN";
    public static final String CMD_SHOW_SWITCH = "SHOW_SWITCH";
    public static final String CMD_SWITCH_SCHEMA = "SWITCH_SCHEMA";

    public static String loginBeanId = "";

    public enum ObjType {
        LOGIN_BEAN
    }

    @Override
    public void init(JsonObject jObj) {

    }

    @Override
    public CsMgrEnum getType() {
        return CsMgrEnum.LOGIN;
    }

    @Override
    public String getHelp() {
        return GuiJsonUtil.genCmdHelp(CMD_SHOW_LOGIN, "显示登录界面", GuiMsgType.RECE) +
                GuiJsonUtil.genCmdHelp(CMD_SHOW_SWITCH, "显示切换界面", GuiMsgType.RECE);
    }

    @Override
    public void doPush(JsonObject msg) {
        try {
            LoginComp loginComp = new LoginComp() {
                @Override
                public void inform(LoginBean loginBean) {
                    loginBeanId = StartUtil.eng.push2SharedMap(loginBean);
                    StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainFrameMgr.CMD_SHOW));
                }
            };

            String cmd = GuiJsonUtil.toStrCmd(msg);
            switch (cmd) {
                case CMD_SHOW_LOGIN:
                    loginComp.showLogin();
                    break;
                case CMD_SHOW_SWITCH:
                    loginComp.switchLogin();
                    break;
                case CMD_SWITCH_SCHEMA:
                    String schemaName = msg.getString("schemaName");
                    loginComp.switchSchema(schemaName);
                    break;
                default:
                    unknowMsg(msg.toPrettyString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            //启动失败时强制关闭工作空间占用线程，不写null会导致错误弹出框不出现
            PopPaneUtil.error(null, e);
            System.exit(0);
        }

    }

    @Override
    public JsonObject doCall(JsonObject msg) {
        try {
            if (GuiJsonUtil.isSharedId(msg)) {
                return GuiJsonUtil.toJsonSharedId(loginBeanId);
            }
        } catch (Exception e) {
            return GuiJsonUtil.toError(e);
        }
        return GuiJsonUtil.toError("未知命令:" + msg);
    }

}
