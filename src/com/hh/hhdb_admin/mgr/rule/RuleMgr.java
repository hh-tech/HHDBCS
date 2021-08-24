package com.hh.hhdb_admin.mgr.rule;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.login.LoginMgr;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;

import java.sql.Connection;

/**
 * @author: Jiang
 * @date: 2020/7/24
 */

public class RuleMgr extends AbsGuiMgr {

    public static final String CMD_ADD = "add";
    public static final String CMD_DELETE = "delete";
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
        switch (GuiJsonUtil.toStrCmd(msg)) {
            case CMD_ADD:
                RuleComp ruleComp = new RuleComp();
                ruleComp.add(msg);
                break;
            case CMD_DELETE:
                String sql = "DROP RULE \"%s\" ON \"%s\".\"%s\"";
                String schemaName = msg.getString("schemaName");
                String tableName = msg.getString("tableName");
                String name = msg.getString("name");
                SqlExeUtil.executeUpdate(conn, String.format(sql, name, schemaName, tableName));
                StartUtil.eng.doPush(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH)
                        .add(TreeMgr.PARAM_NODE_TYPE, TreeMrType.RULE_GROUP.name()));
                break;
            default:
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
