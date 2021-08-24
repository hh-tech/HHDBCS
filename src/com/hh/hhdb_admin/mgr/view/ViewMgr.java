package com.hh.hhdb_admin.mgr.view;

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
import com.hh.hhdb_admin.mgr.view.comp.AddUpdViewComp;
import com.hh.hhdb_admin.mgr.view.comp.OpenViewComp;

import java.sql.Connection;

/**
 * author:yangxianhui
 * date:2020/10/15
 */
public class ViewMgr extends AbsGuiMgr {
    public static final String CMD_SHOW_CREATE_VIEW = "show_create_view";
    public static final String CMD_SHOW_UPDATE_VIEW = "show_update_view";
    public static final String CMD_SHOW_OPEN_VIEW = "show_open_view";
    public static final String CMD_DELETE_VIEW = "delete_view";
    public static final String CMD_SHOW_OPEN_MVIEW = "show_open_mview";
    public static final String CMD_SHOW_CREATE_MVIEW = "show_create_mview";
    public static final String CMD_SHOW_UPDATE_MVIEW = "show_update_mview";
    public static final String CMD_DELETE_MVIEW = "delete_mview";
    public static final String CMD_SET_TEST_CONN = "set_test_conn";
    public static final String PARAM_TEST_CONN_ID = "testConnId";
    public static final String PARAM_VIEW_NAME = "view_name";
    private static final String SUCCESS = "success";
    private LoginBean loginBean;
    private Connection conn;


    @Override
    public void init(JsonObject jObj) {
    }


    @Override
    public CsMgrEnum getType() {
        return CsMgrEnum.VIEW;
    }

    @Override
    public String getHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append(GuiJsonUtil.genCmdHelp(CMD_SHOW_CREATE_VIEW, "创建视图", GuiMsgType.RECE));
        sb.append(GuiJsonUtil.genCmdHelp(CMD_SHOW_UPDATE_VIEW, "设计视图", GuiMsgType.RECE));
        sb.append(GuiJsonUtil.genCmdHelp(CMD_SHOW_OPEN_VIEW, "打开视图", GuiMsgType.RECE));
        sb.append(GuiJsonUtil.genCmdHelp(CMD_SHOW_OPEN_MVIEW, "打开物化视图", GuiMsgType.RECE));
        sb.append(GuiJsonUtil.genCmdHelp(CMD_SHOW_CREATE_MVIEW, "创建物化视图", GuiMsgType.RECE));
        sb.append(GuiJsonUtil.genCmdHelp(CMD_SHOW_UPDATE_MVIEW, "设计物化视图", GuiMsgType.RECE));
        sb.append(GuiJsonUtil.genCmdHelp(CMD_DELETE_VIEW, "删除视图", GuiMsgType.RECE));
        sb.append(GuiJsonUtil.genCmdHelp(CMD_DELETE_MVIEW, "删除物化视图", GuiMsgType.RECE));
        return sb.toString();

    }


    @Override
    public void doPush(JsonObject msg) throws Exception {
        initConn();
        String cmd = GuiJsonUtil.toStrCmd(msg);
        String schemaName = GuiJsonUtil.toPropValue(msg, StartUtil.PARAM_SCHEMA);
        switch (cmd) {
            case CMD_SHOW_CREATE_VIEW:
                getAddUpdateComp().show(schemaName, false);
                break;
            case CMD_SHOW_UPDATE_VIEW: {
                String viewName = GuiJsonUtil.toPropValue(msg, PARAM_VIEW_NAME);
                getAddUpdateComp().show(schemaName, viewName, false);
                break;
            }
            case CMD_SHOW_OPEN_VIEW: {
                String viewName = GuiJsonUtil.toPropValue(msg, PARAM_VIEW_NAME);
                OpenViewComp openViewComp = new OpenViewComp(loginBean.getJdbc());
                openViewComp.show(schemaName, viewName, false);
                break;
            }
            case CMD_SHOW_CREATE_MVIEW:
                getAddUpdateComp().show(schemaName, true);
                break;
            case CMD_SHOW_UPDATE_MVIEW: {
                String viewName = GuiJsonUtil.toPropValue(msg, PARAM_VIEW_NAME);
                getAddUpdateComp().show(schemaName, viewName, true);
                break;
            }
            case CMD_SHOW_OPEN_MVIEW: {
                String viewName = GuiJsonUtil.toPropValue(msg, PARAM_VIEW_NAME);
                OpenViewComp openViewComp = new OpenViewComp(loginBean.getJdbc());
                openViewComp.show(schemaName, viewName, true);
                break;
            }
            case CMD_DELETE_VIEW: {
                String viewName = GuiJsonUtil.toPropValue(msg, PARAM_VIEW_NAME);
                ViewUtil.delView(conn, schemaName, viewName);
                break;
            }
            case CMD_DELETE_MVIEW: {
                String viewName = GuiJsonUtil.toPropValue(msg, PARAM_VIEW_NAME);
                ViewUtil.delView(conn, schemaName, viewName);
                break;
            }
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
            loginBean = (LoginBean) StartUtil.eng.getSharedObj(GuiJsonUtil.toPropValue(msg, PARAM_TEST_CONN_ID));
            conn = loginBean.getConn();
            retObj = GuiJsonUtil.toJsonProp(SUCCESS, "设置成功");
        }
        return retObj;
    }

    /**
     * 获取创建和更新视图组件
     *
     * @return AddUpdViewComp
     */
    private AddUpdViewComp getAddUpdateComp() {
        return new AddUpdViewComp(loginBean) {
            @Override
            protected void informRefreshView(String schemaName, boolean isMaterialized) {
                infoTreeRefresh(schemaName, isMaterialized);
            }
        };
    }

    /**
     * 通知tree刷新
     *
     * @param schemaName     schema名称
     * @param isMaterialized 是否物化视图
     */
    private void infoTreeRefresh(String schemaName, boolean isMaterialized) {
        StartUtil.eng.doPush(CsMgrEnum.TREE,
                GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH).add(TreeMgr.PARAM_NODE_TYPE, (isMaterialized ? TreeMrType.M_VIEW_GROUP : TreeMrType.VIEW_GROUP).name())
                        .add(StartUtil.PARAM_SCHEMA, schemaName)
        );
    }

    /**
     * 初始化连接conn
     *
     * @throws Exception
     */
    private void initConn() throws Exception {
        if (conn == null || conn.isClosed()) {
            JsonObject sharedIdObj = StartUtil.eng.doCall(CsMgrEnum.LOGIN, GuiJsonUtil.genGetShareIdMsg(LoginMgr.ObjType.LOGIN_BEAN));
            loginBean = (LoginBean) StartUtil.eng.getSharedObj(GuiJsonUtil.toStrSharedId(sharedIdObj));
            conn = loginBean.getConn();
        }

    }
}
