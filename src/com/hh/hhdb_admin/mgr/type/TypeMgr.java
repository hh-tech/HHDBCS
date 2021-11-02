package com.hh.hhdb_admin.mgr.type;

import com.hh.frame.dbobj2.ora.OraSessionEnum;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.engine.GuiMsgType;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.login.LoginMgr;
import com.hh.hhdb_admin.mgr.main_frame.MainFrameMgr;

import java.sql.Connection;

/**
 * @author YuSai
 */
public class TypeMgr extends AbsGuiMgr {

    public static final String CMD_ADD = "CMD_ADD";
    public static final String CMD_DESIGN = "CMD_DESIGN";
    public static final String PARAM_TYPE = "PARAM_TYPE";
    public static final String PARAM_NAME = "PARAM_NAME";
    public static final String PARAM_TEST = "PARAM_TEST";
    public static final String CMD_OPEN_AS_DESIGN = "CMD_OPEN_AS_DESIGN";
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
        return CsMgrEnum.TYPE;
    }

    @Override
    public void doPush(JsonObject msg) throws Exception {
        String cmd = GuiJsonUtil.toStrCmd(msg);
        if (StartUtil.CMD_CLOSE.equals(cmd)) {
            StartUtil.eng.rmFromSharedMap(GuiJsonUtil.toPropValue(msg, StartUtil.CMD_ID));
        } else {
            String schema = GuiJsonUtil.toPropValue(msg, StartUtil.PARAM_SCHEMA);
            String nodeType = GuiJsonUtil.toPropValue(msg, PARAM_TYPE);
            OraSessionEnum sessionEnum = OraSessionEnum.valueOf(nodeType);
            TypeComp typeComp = new TypeComp(getConn(), schema, sessionEnum);
            if (CMD_ADD.equals(cmd)) {
                String test = GuiJsonUtil.toPropValue(msg, PARAM_TEST);
                if ("test".equals(test)) {
                    HFrame frame = new HFrame();
                    frame.setRootPanel(typeComp.getPanel(GuiJsonUtil.toPropValue(msg, PARAM_NAME)));
                    frame.show();
                    frame.maximize();
                } else {
                    typeComp.add();
                }
            } else if (CMD_DESIGN.equals(cmd)) {
                String name = GuiJsonUtil.toPropValue(msg, PARAM_NAME);
                StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainFrameMgr.ADD_TAB_PANE_ITEM)
                        .add(StartUtil.CMD_ID, StartUtil.eng.push2SharedMap(typeComp.getPanel(name)))
                        .add("title", name)
                        .add(MainFrameMgr.PARAM_MGR_TYPE, CsMgrEnum.TYPE.name()));
            } else if (CMD_OPEN_AS_DESIGN.equals(cmd)) {
                typeComp.openFile(GuiJsonUtil.toPropValue(msg, "text"), GuiJsonUtil.toPropValue(msg, "fileName"), sessionEnum);
            } else {
                unknowMsg(msg.toPrettyString());
            }
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
