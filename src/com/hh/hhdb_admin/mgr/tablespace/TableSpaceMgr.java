package com.hh.hhdb_admin.mgr.tablespace;

import com.hh.frame.common.base.DBTypeEnum;
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
public class TableSpaceMgr extends AbsGuiMgr {

    public static final String CMD_SHOW_ADD_TABLE_SPACE = "CMD_SHOW_ADD_TABLE_SPACE";
    public static final String CMD_DELETE_TABLE_SPACE = "CMD_DELETE_TABLE_SPACE";

    @Override
    public void init(JsonObject jObj) {}

    @Override
    public String getHelp() {
        return GuiJsonUtil.genCmdHelp(CMD_SHOW_ADD_TABLE_SPACE, "显示新增表空间", GuiMsgType.RECE)
                +  GuiJsonUtil.genCmdHelp(CMD_DELETE_TABLE_SPACE, "删除表空间", GuiMsgType.RECE);
    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.TABLE_SPACE;
    }

    @Override
    public void doPush(JsonObject msg) throws Exception {
        Connection conn = getConn();
        DBTypeEnum dbTypeEnum = DriverUtil.getDbType(conn);
        TableSpaceComp spaceComp = new TableSpaceComp(conn, dbTypeEnum) {
            @Override
            public void refreshTree() {
                // 刷新树节点
                StartUtil.eng.doPush(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH).add(TreeMgr.PARAM_NODE_TYPE, TreeMrType.ROOT_TABLE_SPACE_GROUP.name()));
            }
        };
        String cmd = GuiJsonUtil.toStrCmd(msg);
        if (TableSpaceMgr.CMD_SHOW_ADD_TABLE_SPACE.equals(cmd)) {
            spaceComp.show();
        } else if (TableSpaceMgr.CMD_DELETE_TABLE_SPACE.equals(cmd)) {
            spaceComp.delTableSpace(msg.getString("name"));
        } else {
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
