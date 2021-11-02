package com.hh.hhdb_admin.mgr.index;

import com.hh.frame.common.util.DriverUtil;
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

import java.sql.Connection;

/**
 * 索引的组件MGR
 *
 * @author yangxianhui
 */
public class IndexMgr extends AbsGuiMgr {

    public static final String CMD_SHOW_ADD_TABLE_INDEX = "CMD_SHOW_ADD_TABLE_INDEX"; //添加索引

    @Override
    public void init(JsonObject jObj) {
    }

    @Override
    public String getHelp() {
        return GuiJsonUtil.genCmdHelp(CMD_SHOW_ADD_TABLE_INDEX, "创建索引", GuiMsgType.RECE);
    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.TABLE;
    }

    @Override
    public void doPush(JsonObject msg) throws Exception {
        String cmd = GuiJsonUtil.toStrCmd(msg);
        String schemaName = GuiJsonUtil.toPropValue(msg, StartUtil.PARAM_SCHEMA);
        String tableName = GuiJsonUtil.toPropValue(msg, StartUtil.PARAM_TABLE);
        Connection conn = getConn();
        IndexComp common = new IndexComp(conn, DriverUtil.getDbType(conn), schemaName, tableName) {
            @Override
            public void refreshTree() {
                StartUtil.eng.doPush(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH)
                        .add(TreeMgr.PARAM_NODE_TYPE, TreeMrType.INDEX_GROUP.name())
                        .add(StartUtil.PARAM_TABLE, tableName)
                        .add(StartUtil.PARAM_SCHEMA, schemaName));
            }
        };
        if (CMD_SHOW_ADD_TABLE_INDEX.equals(cmd)) {
            common.show();
        } else {
            unknowMsg(msg.toPrettyString());
        }
    }

    @Override
    public JsonObject doCall(JsonObject msg) {
        return null;
    }

    /**
     * 获取连接
     */
    public Connection getConn() throws Exception {
        JsonObject sharedIdObj = StartUtil.eng.doCall(CsMgrEnum.LOGIN, GuiJsonUtil.genGetShareIdMsg(LoginMgr.ObjType.LOGIN_BEAN));
        LoginBean loginBean = (LoginBean) StartUtil.eng.getSharedObj(GuiJsonUtil.toStrSharedId(sharedIdObj));
        return loginBean.getConn();
    }
}
