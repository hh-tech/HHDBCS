package com.hh.hhdb_admin.mgr.database;

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
 * @author YuSai
 */
public class DatabaseMgr extends AbsGuiMgr {

    public static final String CMD_SHOW_ADD_DATABASE = "CMD_SHOW_ADD_DATABASE";
    public static final String CMD_DELETE_DATABASE = "CMD_DELETE_DATABASE";

    @Override
    public void init(JsonObject jObj) {
    }

    @Override
    public String getHelp() {
        return GuiJsonUtil.genCmdHelp(CMD_SHOW_ADD_DATABASE, "显示新增数据库", GuiMsgType.RECE)
                + GuiJsonUtil.genCmdHelp(CMD_DELETE_DATABASE, "删除数据库", GuiMsgType.RECE);
    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.DATABASE;
    }

    @Override
    public void doPush(JsonObject msg) throws Exception {
        LoginBean loginBean = getLoginBean();
        Connection conn = loginBean.getConn();
        DatabaseComp databaseComp = new DatabaseComp(conn, DriverUtil.getDbType(conn), loginBean.getJdbc().getUser()) {
            @Override
            public void refreshTree() {
                // 刷新树节点
                StartUtil.eng.doPush(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH).
                        add(TreeMgr.PARAM_NODE_TYPE, TreeMrType.ROOT_DATABASE_GROUP.name()));
            }
        };
        String cmd = GuiJsonUtil.toStrCmd(msg);
        switch (cmd) {
            case CMD_SHOW_ADD_DATABASE:
                databaseComp.show();
                break;
            case CMD_DELETE_DATABASE:
                databaseComp.delDatabase(msg.getString("name"));
                break;
            default:
                unknowMsg(msg.toPrettyString());
        }
    }

    @Override
    public JsonObject doCall(JsonObject msg) {
        return null;
    }

    public LoginBean getLoginBean() throws Exception {
        JsonObject sharedIdObj = StartUtil.eng.doCall(CsMgrEnum.LOGIN, GuiJsonUtil.genGetShareIdMsg(LoginMgr.ObjType.LOGIN_BEAN));
        return (LoginBean) StartUtil.eng.getSharedObj(GuiJsonUtil.toStrSharedId(sharedIdObj));
    }

}
