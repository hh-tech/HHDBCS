package com.hh.hhdb_admin.mgr.view;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;

import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.engine.GuiMsgType;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.login.LoginMgr;
import com.hh.hhdb_admin.mgr.main_frame.MainFrameComp;
import com.hh.hhdb_admin.mgr.main_frame.MainFrameMgr;
import com.hh.hhdb_admin.mgr.table_open.ModifyTabDataComp;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;
import com.hh.hhdb_admin.mgr.view.comp.AddUpdViewComp;

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
    private String openId = null;
    private static final String OPEN_TMP = "open_tmp";
    private Map<String, ModifyTabDataComp> map = new HashMap<>();


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
        	case StartUtil.CMD_CLOSE:
				openId = GuiJsonUtil.toPropValue(msg, StartUtil.CMD_ID);
				ModifyTabDataComp tabDataComp = map.get(openId);
				tabDataComp.close();
				map.remove(openId);
				StartUtil.eng.rmFromSharedMap(openId);
				break;
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
            	openView(viewName,schemaName);
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
                openView(viewName,schemaName);
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
    
    
    private void openView(String viewName,String schemaName) {

		
		ModifyTabDataComp tablePanel = null;
		
		//判断是否已打
		boolean bool = true;
		for (String string : map.keySet()) {
			tablePanel = map.get(string);
			if (viewName.equals(tablePanel.getTabName()) && schemaName.equals(tablePanel.getSchemaName())) {
				openId = string;
				bool = false;
				break;
			}
		}
		if (bool) {
			File tmpFile = new File(StartUtil.workspace + File.separator + OPEN_TMP);
			
			
			tablePanel = new ModifyTabDataComp(loginBean.getJdbc(),schemaName, viewName, tmpFile);
	        String sql = String.format("select * from %s.%s",
	            		schemaName,
	                    DbCmdStrUtil.toDbCmdStr(viewName, DriverUtil.getDbType(loginBean.getJdbc())));
	        tablePanel.loadReadOnlyTable(sql);
	        //打开新的
			openId = StartUtil.eng.push2SharedMap(tablePanel);
			map.put(openId + "", tablePanel);
		}
		if (((MainFrameComp) StartUtil.parentFrame).getTabPane() == null) {
			HDialog dialog = new HDialog(StartUtil.parentFrame, HDialog.LARGE_WIDTH);
			HPanel panel = new HPanel();
			panel.setLastPanel(tablePanel);
			((JDialog) dialog.getWindow()).setTitle(tablePanel.getSchemaName() + "." + tablePanel.getTabName());
			((JDialog) dialog.getWindow()).setResizable(false);
			dialog.setRootPanel(panel);
			dialog.show();
		} else {
			StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainFrameMgr.ADD_TAB_PANE_ITEM)
					.add(StartUtil.CMD_ID, openId).add("title", schemaName + "." + viewName).add(MainFrameMgr.PARAM_MGR_TYPE, CsMgrEnum.VIEW.name()));
		}
		
    
    }
}
