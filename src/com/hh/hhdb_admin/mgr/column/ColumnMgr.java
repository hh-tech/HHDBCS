package com.hh.hhdb_admin.mgr.column;

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
 * @author yangxianhui
 */
public class ColumnMgr extends AbsGuiMgr {

    public static final String CMD_SHOW_ADD_TABLE_COLUMN = "CMD_SHOW_ADD_TABLE_COLUMN";//添加列
    public static final String CMD_SHOW_UPDATE_TABLE_COLUMN = "CMD_SHOW_UPDATE_TABLE_COLUMN";//修改列
    public static final String CMD_SHOW_RENAME_TABLE_COLUMN = "CMD_SHOW_RENAME_TABLE_COLUMN";//重命名列
    public static String PARAM_COLUMN_NAME = "COLUMN_NAME";

    @Override
    public void init(JsonObject jObj) {

    }

    @Override
    public String getHelp() {
        return GuiJsonUtil.genCmdHelp(CMD_SHOW_ADD_TABLE_COLUMN, "创建列", GuiMsgType.RECE) +
                GuiJsonUtil.genCmdHelp(CMD_SHOW_UPDATE_TABLE_COLUMN, "设计列", GuiMsgType.RECE) +
                GuiJsonUtil.genCmdHelp(CMD_SHOW_RENAME_TABLE_COLUMN, "重命名列", GuiMsgType.RECE);
    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.TABLE;
    }

    @Override
    public void doPush(JsonObject msg) throws Exception {
        String schemaName = GuiJsonUtil.toPropValue(msg, StartUtil.PARAM_SCHEMA);
        String tableName = GuiJsonUtil.toPropValue(msg, StartUtil.PARAM_TABLE);
        Connection conn = getConn();
        ColumnComp columnComp = new ColumnComp(conn, DriverUtil.getDbType(conn), schemaName, tableName) {
            @Override
            public void refreshTree() {
                StartUtil.eng.doPush(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH)
                        .add(TreeMgr.PARAM_NODE_TYPE, TreeMrType.COLUMN_GROUP.name())
                        .add(StartUtil.PARAM_TABLE, tableName)
                        .add(StartUtil.PARAM_SCHEMA, schemaName));
            }
        };
        switch (GuiJsonUtil.toStrCmd(msg)) {
            case CMD_SHOW_ADD_TABLE_COLUMN:
                columnComp.show(false, "");
                break;
            case CMD_SHOW_UPDATE_TABLE_COLUMN: {
                String columnName = GuiJsonUtil.toPropValue(msg, PARAM_COLUMN_NAME);
                columnComp.show(true, columnName);
                break;
            }
            case CMD_SHOW_RENAME_TABLE_COLUMN: {
                String columnName = GuiJsonUtil.toPropValue(msg, PARAM_COLUMN_NAME);
                columnComp.renameColumn(tableName, columnName);
                break;
            }
            default:
                unknowMsg(msg.toPrettyString());
                break;
        }
    }

    @Override
    public JsonObject doCall(JsonObject msg) {
        return null;
    }

    /**
     * 获取连接ID
     */
    private Connection getConn() throws Exception {
        JsonObject sharedIdObj = StartUtil.eng.doCall(CsMgrEnum.LOGIN, GuiJsonUtil.genGetShareIdMsg(LoginMgr.ObjType.LOGIN_BEAN));
        LoginBean loginBean = (LoginBean) StartUtil.eng.getSharedObj(GuiJsonUtil.toStrSharedId(sharedIdObj));
        return loginBean.getConn();
    }

}
