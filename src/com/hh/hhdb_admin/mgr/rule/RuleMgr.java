package com.hh.hhdb_admin.mgr.rule;

import java.sql.Connection;

import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.login.LoginMgr;

/**
 * @author: Jiang
 * @date: 2020/7/24
 */

public class RuleMgr extends AbsGuiMgr {

    public static final String CMD_ADD = "add";
    public static Connection conn = null;

    @Override
    public void init(JsonObject jObj) {

    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public Enum<?> getType() {
        return null;
    }

    @Override
    public void doPush(JsonObject msg) throws Exception {
        if (conn == null) {
            initConn();
        }
        if (CMD_ADD.equals(GuiJsonUtil.toStrCmd(msg))) {
            RuleComp ruleComp = new RuleComp();
            ruleComp.add(msg);
        }
    }

    @Override
    public JsonObject doCall(JsonObject msg) {
        return null;
    }

    public void initConn() throws Exception {
        JsonObject jsonObj = StartUtil.eng.doCall(CsMgrEnum.LOGIN, GuiJsonUtil.genGetShareIdMsg(LoginMgr.ObjType.LOGIN_BEAN));
        String loginId = GuiJsonUtil.toStrSharedId(jsonObj);
        LoginBean loginBean = (LoginBean) StartUtil.eng.getSharedObj(loginId);
        conn = loginBean.getConn();
    }
}
