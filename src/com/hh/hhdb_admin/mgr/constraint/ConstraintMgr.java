package com.hh.hhdb_admin.mgr.constraint;

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
import com.hh.hhdb_admin.mgr.table.TableComp;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;

import java.sql.Connection;

/**
 * @author YuSai
 */
public class ConstraintMgr extends AbsGuiMgr {

    public static final String CMD_SHOW_CONSTRAINT_FK_DIALOG = "CMD_SHOW_CONSTRAINT_FK_DIALOG";
    public static final String CMD_SHOW_CONSTRAINT_UK_DIALOG = "CMD_SHOW_CONSTRAINT_UK_DIALOG";
    public static final String CMD_SHOW_CONSTRAINT_PK_DIALOG = "CMD_SHOW_CONSTRAINT_PK_DIALOG";
    public static final String CMD_SHOW_CONSTRAINT_CK_DIALOG = "CMD_SHOW_CONSTRAINT_CK_DIALOG";
    public static final String CMD_DELETE_CONSTRAINT = "CMD_DELETE_CONSTRAINT";

    @Override
    public void init(JsonObject jObj) {

    }

    @Override
    public String getHelp() {
        return GuiJsonUtil.genCmdHelp(CMD_SHOW_CONSTRAINT_FK_DIALOG, "显示新增外键约束", GuiMsgType.RECE) +
                GuiJsonUtil.genCmdHelp(CMD_SHOW_CONSTRAINT_UK_DIALOG, "显示新增唯一键约束", GuiMsgType.RECE) +
                GuiJsonUtil.genCmdHelp(CMD_SHOW_CONSTRAINT_PK_DIALOG, "显示新增主键约束", GuiMsgType.RECE) +
                GuiJsonUtil.genCmdHelp(CMD_SHOW_CONSTRAINT_CK_DIALOG, "显示新增检查约束", GuiMsgType.RECE) +
                GuiJsonUtil.genCmdHelp(CMD_DELETE_CONSTRAINT, "删除约束", GuiMsgType.RECE);
    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.CONSTRAINT;
    }

    @Override
    public void doPush(JsonObject msg) throws Exception {
        String cmd = GuiJsonUtil.toStrCmd(msg);
        String constType = msg.getString("constType");
        String tableName = msg.getString(StartUtil.PARAM_TABLE);
        String schema = msg.getString(StartUtil.PARAM_SCHEMA);
        Connection conn = getConn(schema);
        ConstraintComp common = new ConstraintComp(TreeMrType.valueOf(constType), conn, DriverUtil.getDbType(conn), schema, tableName) {
            @Override
            public void refreshTree() {
                StartUtil.eng.doPush(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH)
                        .add(TreeMgr.PARAM_NODE_TYPE, constType)
                        .add(StartUtil.PARAM_SCHEMA, schema)
                        .add(StartUtil.PARAM_TABLE, tableName));
            }
        };
        switch (cmd) {
            case CMD_SHOW_CONSTRAINT_FK_DIALOG:
                common.showFore();
                break;
            case CMD_SHOW_CONSTRAINT_UK_DIALOG:
            case CMD_SHOW_CONSTRAINT_PK_DIALOG:
            case CMD_SHOW_CONSTRAINT_CK_DIALOG:
                common.showOtherConst();
                break;
            case CMD_DELETE_CONSTRAINT:
                common.delConst(constType, schema, tableName, msg.getString("constName"));
                break;
            default:
                unknowMsg(msg.toPrettyString());
        }
    }

    @Override
    public JsonObject doCall(JsonObject msg) {
        return null;
    }
    public Connection getConn(String schema) throws Exception {
        JsonObject sharedIdObj = StartUtil.eng.doCall(CsMgrEnum.LOGIN, GuiJsonUtil.genGetShareIdMsg(LoginMgr.ObjType.LOGIN_BEAN));
        LoginBean loginBean = (LoginBean) StartUtil.eng.getSharedObj(GuiJsonUtil.toStrSharedId(sharedIdObj));
        TableComp.jdbcBean = loginBean.getJdbc();
        TableComp.schemaName = schema;
        return loginBean.getConn();
    }

}
