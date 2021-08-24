package com.hh.hhdb_admin.mgr.usr;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.create_dbobj.userMr.base.UsrFormType;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.engine.GuiMsgType;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.login.LoginMgr;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;
import com.hh.hhdb_admin.mgr.usr.comp.AddUpdUsrComp;
import com.hh.hhdb_admin.mgr.usr.comp.PermComp;
import com.hh.hhdb_admin.mgr.usr.comp.ReNameUsrComp;
import com.hh.hhdb_admin.mgr.usr.util.UsrUtil;

import java.sql.Connection;

public class UsrMgr extends AbsGuiMgr {
    public static final String CMD_SHOW_ADD_USER = "show_add_usr";
    public static final String CMD_SHOW_UPDATE_USER = "show_update_usr";
    public static final String CMD_SHOW_ADD_ROLE = "show_add_role";
    public static final String CMD_SHOW_UPDATE_ROLE = "show_update_role";
    public static final String CMD_SHOW_PERMISSION = "show_usr_permission";
    public static final String CMD_SHOW_RENAME = "show_usr_rename";
    public static final String CMD_SET_TEST_CONN = "set_test_conn";
    public static final String CMD_DELETE = "del_usr";
    public static final String CMD_DELETE_ROLE = "del_role";
    public static final String PARAM_USR_NAME = "usrName";
    public static final String PARAM_TEST_CONN_ID = "testConnId";
    private static final String SUCCESS = "success";
    private Connection conn;

    @Override
    public void init(JsonObject jObj) {

    }


    @Override
    public CsMgrEnum getType() {
        return CsMgrEnum.USR;
    }

    @Override
    public String getHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append(GuiJsonUtil.genCmdHelp(CMD_SHOW_ADD_USER, "显示添加用户面板", GuiMsgType.RECE));
        sb.append(GuiJsonUtil.genCmdHelp(CMD_SHOW_UPDATE_USER, "显示设计用户面板", GuiMsgType.RECE));
        sb.append(GuiJsonUtil.genCmdHelp(CMD_SHOW_ADD_ROLE, "显示添加角色面板", GuiMsgType.RECE));
        sb.append(GuiJsonUtil.genCmdHelp(CMD_SHOW_UPDATE_ROLE, "显示设计角色面板", GuiMsgType.RECE));
        sb.append(GuiJsonUtil.genCmdHelp(CMD_SHOW_PERMISSION, "显示用户权限面板", GuiMsgType.RECE));
        sb.append(GuiJsonUtil.genCmdHelp(CMD_DELETE, "删除用户", GuiMsgType.RECE));
        sb.append(GuiJsonUtil.genCmdHelp(CMD_SHOW_RENAME, "显示重命名用户弹框", GuiMsgType.RECE));
        sb.append(GuiJsonUtil.genCmdHelp(CMD_DELETE_ROLE, "删除角色", GuiMsgType.RECE));
        return sb.toString();
    }


    @Override
    public void doPush(JsonObject msg) throws Exception {
        String cmd = GuiJsonUtil.toStrCmd(msg);
        initConn();
        switch (cmd) {
            case CMD_SHOW_ADD_USER:
                getAddUsrComp().show(StartUtil.getMainDialog());
                break;
            case CMD_SHOW_UPDATE_USER:
                getAddUsrComp().show(StartUtil.getMainDialog(), GuiJsonUtil.toPropValue(msg, PARAM_USR_NAME));
                break;
            case CMD_SHOW_ADD_ROLE:
                getAddRoleComp().show(StartUtil.getMainDialog());
                break;
            case CMD_SHOW_UPDATE_ROLE:
                getAddRoleComp().show(StartUtil.getMainDialog(), GuiJsonUtil.toPropValue(msg, PARAM_USR_NAME));
                break;
            case CMD_SHOW_PERMISSION:
                new PermComp(conn).show(StartUtil.getMainDialog(), GuiJsonUtil.toPropValue(msg, PARAM_USR_NAME));
                break;
            case CMD_SHOW_RENAME:
                getReNameUsrComp().show(StartUtil.getMainDialog(), GuiJsonUtil.toPropValue(msg, PARAM_USR_NAME));
                break;
            case CMD_DELETE:
                UsrUtil.delUser(conn, GuiJsonUtil.toPropValue(msg, PARAM_USR_NAME));
                infoRefreashUsrSet();
                break;
            case CMD_DELETE_ROLE:
                UsrUtil.delRole(conn, GuiJsonUtil.toPropValue(msg, PARAM_USR_NAME));
                infoRefreshRoleSet();
                break;
            default:
                unknowMsg(msg.toPrettyString());
                break;
        }
    }

    @Override
    public JsonObject doCall(JsonObject msg) {
        String cmd = GuiJsonUtil.toStrCmd(msg);
        JsonObject retObj = null;
        if (CMD_SET_TEST_CONN.equals(cmd)) {
            LoginBean bean = (LoginBean) StartUtil.eng.getSharedObj(GuiJsonUtil.toPropValue(msg, PARAM_TEST_CONN_ID));
            conn = bean.getConn();
            retObj = GuiJsonUtil.toJsonProp(SUCCESS, "设置成功");
        }
        return retObj;
    }

    /**
     * 添加用户组件
     */
    private AddUpdUsrComp getAddUsrComp() {
        return new AddUpdUsrComp(conn, UsrFormType.usr) {
            @Override
            protected void informRefreshUsr() {
                infoRefreashUsrSet();
            }
        };
    }

    /**
     * 添加角色组件
     */
    private AddUpdUsrComp getAddRoleComp() {
        return new AddUpdUsrComp(conn, UsrFormType.role) {
            @Override
            protected void informRefreshUsr() {
                UsrMgr.this.infoRefreshRoleSet();
            }
        };
    }


    /**
     * 获取重命名组件
     */
    private ReNameUsrComp getReNameUsrComp() {
        return new ReNameUsrComp(conn) {
            @Override
            protected void infoRefreshUsr() {
                infoRefreashUsrSet();
            }
        };
    }

    /**
     * 获取连接
     */
    private LoginBean getLoginBean() throws Exception {
        JsonObject sharedIdObj = StartUtil.eng.doCall(CsMgrEnum.LOGIN, GuiJsonUtil.genGetShareIdMsg(LoginMgr.ObjType.LOGIN_BEAN));
        return (LoginBean) StartUtil.eng.getSharedObj(GuiJsonUtil.toStrSharedId(sharedIdObj));
    }

    /**
     * 通知主面板左侧的树主键刷新用户集合
     */
    private void infoRefreashUsrSet() {
        StartUtil.eng.doPush(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH).add(TreeMgr.PARAM_NODE_TYPE, TreeMrType.ROOT_USER_GROUP.name()));
    }

    /**
     * 通知主面板左侧的树主键刷新角色集合
     */
    private void infoRefreshRoleSet() {
        StartUtil.eng.doPush(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH).add(TreeMgr.PARAM_NODE_TYPE, TreeMrType.ROOT_ROLE_GROUP.name()));
    }

    /**
     * 初始化connection
     */
    private void initConn() throws Exception {
        if (conn == null || conn.isClosed()) {
            conn = getLoginBean().getConn();
        }

    }


}
