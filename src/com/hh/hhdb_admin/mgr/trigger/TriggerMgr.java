package com.hh.hhdb_admin.mgr.trigger;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.engine.GuiMsgType;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.login.LoginMgr;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;
import com.hh.hhdb_admin.mgr.trigger.comp.TriggerComp;

import java.sql.Connection;

/**
 * 表触发器
 */
public class TriggerMgr extends AbsGuiMgr {
    public static final String CMD_ADD_TRIGGER = "ADD_TRIGGER";
    public static final String CMD_UPDATE_TRIGGER = "UPDATE_TRIGGER";
    public static final String CMD_DELETE = "DELETE";
    public static final String PARAM_TEST_CONN_ID = "testConnId";
    public static final String PARAM_TRIGGER_NAME = "name";
    public static final String CMD_SET_TEST_CONN = "set_test_conn";
    private static final String SUCCESS = "success";
    private Connection conn;
    private JdbcBean jdbcBean;

    @Override
    public void init(JsonObject jObj) {

    }

    @Override
    public CsMgrEnum getType() {
        return CsMgrEnum.TRIGGER;
    }

    @Override
    public String getHelp() {
        return GuiJsonUtil.genCmdHelp(CMD_ADD_TRIGGER, "显示添加触发器面板", GuiMsgType.RECE) +
                GuiJsonUtil.genCmdHelp(CMD_DELETE, "删除触发器", GuiMsgType.RECE);
    }

    @Override
    public void doPush(JsonObject msg) throws Exception {
        initConn();
        String schemaName = GuiJsonUtil.toPropValue(msg, StartUtil.PARAM_SCHEMA);
        String tableName = GuiJsonUtil.toPropValue(msg, StartUtil.PARAM_TABLE);
        String triggerName = GuiJsonUtil.toPropValue(msg, PARAM_TRIGGER_NAME);
        switch (GuiJsonUtil.toStrCmd(msg)) {
            case CMD_ADD_TRIGGER:
                getTriggerComp(schemaName, tableName).show();
                break;
            case CMD_UPDATE_TRIGGER:
                getTriggerComp(schemaName, tableName).show(triggerName);
                break;
            case CMD_DELETE:
                TriggerUtil.delTrigger(conn, triggerName, tableName, schemaName);
                refreshTree(schemaName, tableName);
                break;
            default:
                unknowMsg(msg.toPrettyString());
        }
    }


    @Override
    public JsonObject doCall(JsonObject msg) throws Exception {
        String cmd = GuiJsonUtil.toStrCmd(msg);
        JsonObject retObj = null;
        if (CMD_SET_TEST_CONN.equals(cmd)) {
            LoginBean bean = (LoginBean) StartUtil.eng.getSharedObj(GuiJsonUtil.toPropValue(msg, PARAM_TEST_CONN_ID));
            conn = bean.getConn();
            retObj = GuiJsonUtil.toJsonProp(SUCCESS, "设置成功");
        }
        return retObj;
    }


    private TriggerComp getTriggerComp(String schemaName, String tableName) {
        return new TriggerComp(conn, jdbcBean, schemaName, tableName) {
            @Override
            protected void refreshTreeData(String schemaName1, String tabName) {
                refreshTree(schemaName1, tabName);
            }
        };
    }

    /**
     * 刷新树节点
     */
    private void refreshTree(String schemaName, String tabName) {
        StartUtil.eng.doPush(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH).add(TreeMgr.PARAM_NODE_TYPE, TreeMrType.TRIGGER_GROUP.name()).add(StartUtil.PARAM_TABLE, tabName).add(StartUtil.PARAM_SCHEMA, schemaName));
    }

    /**
     * 初始化connection
     */
    private void initConn() throws Exception {
        if (conn == null || conn.isClosed()) {
            JsonObject jsonObj = StartUtil.eng.doCall(CsMgrEnum.LOGIN, GuiJsonUtil.genGetShareIdMsg(LoginMgr.ObjType.LOGIN_BEAN));
            LoginBean logBean = (LoginBean) StartUtil.eng.getSharedObj(GuiJsonUtil.toStrSharedId(jsonObj));
            conn = logBean.getConn();
            jdbcBean = logBean.getJdbc();
        }
    }

}
