package com.hh.hhdb_admin.mgr.table;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.engine.GuiMsgType;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.login.LoginMgr;
import com.hh.hhdb_admin.mgr.main_frame.MainFrameMgr;
import com.hh.hhdb_admin.mgr.table.comp.RenamePanel;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * @author oyx
 * @date 2020-10-19  0019 10:14:43
 */
public class TableMgr extends AbsGuiMgr {

	public static final String CMD_SHOW_ADD_TABLE = "CMD_SHOW_ADD_TABLE_DIALOG";
	public static final String CMD_SHOW_ADD_TABLE_TAB = "CMD_SHOW_ADD_TABLE_TAB";
	public static final String CMD_RENAME_TABLE_NAME = "CMD_RENAME_TABLE_NAME";
	public static final String PARAM_SCHEMA = "schema";

	private Connection conn;
	private String loginId;

	@Override
	public void init(JsonObject jObj) {

	}

	@Override
	public String getHelp() {
		return GuiJsonUtil.genCmdHelp(CMD_SHOW_ADD_TABLE, "创建表", GuiMsgType.RECE);
	}

	@Override
	public Enum<?> getType() {
		return CsMgrEnum.TABLE;
	}

	@Override
	public void doPush(JsonObject msg) throws Exception {
		loginId = msg.getString("loginId");
		if (StringUtils.isBlank(loginId)) {
			loginId = getLoginBeanId(CsMgrEnum.TABLE);
			if (StringUtils.isBlank(loginId)) {
				PopPaneUtil.info("获取连接失败！");
				return;
			}
		}
		LoginBean loginBean = (LoginBean) StartUtil.eng.getSharedObj(loginId);
		reConn(loginBean);
		conn = loginBean.getConn();

		String cmd = msg.getString("cmd");
		if (StringUtils.isBlank(cmd)) {
			cmd = GuiJsonUtil.toStrCmd(msg);
		}
		DBTypeEnum dbTypeEnum = DriverUtil.getDbType(loginBean.getJdbc());
		if (CMD_SHOW_ADD_TABLE.equals(cmd) || CMD_SHOW_ADD_TABLE_TAB.equals(cmd)) {
			String schemaName = GuiJsonUtil.toPropValue(msg, StartUtil.PARAM_SCHEMA);
			TableComp panelCreate = new TableComp(conn,dbTypeEnum) {
				@Override
				protected void refresh() {
					//刷新树节点
					StartUtil.eng.doPush(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH).add(TreeMgr.PARAM_NODE_TYPE, TreeMrType.TABLE_GROUP.name()).add(StartUtil.PARAM_SCHEMA, schemaName));
				}
			};
			if (StringUtils.isBlank(schemaName)) {
				schemaName = loginBean.getJdbc().getSchema();
			}
			TableComp.schemaName = schemaName;
			TableComp.jdbcBean=loginBean.getJdbc();
			panelCreate.genTableData();
			if (CMD_SHOW_ADD_TABLE_TAB.equals(cmd)) {
				String compId = StartUtil.eng.push2SharedMap(panelCreate.getLastPanel());
				openTab(compId, panelCreate.getTitle());
			} else {
				panelCreate.show();
			}

		} else if (CMD_RENAME_TABLE_NAME.equals(cmd)) {
			//弹窗重命名
			new RenamePanel(GuiJsonUtil.toPropValue(msg, StartUtil.PARAM_SCHEMA), GuiJsonUtil.toPropValue(msg, StartUtil.PARAM_TABLE), conn);
		} else {
			unknowMsg(msg.toPrettyString());
		}
	}

	@Override
	public JsonObject doCall(JsonObject msg) {
		try {
			String loginBeanId = getLoginBeanId(CsMgrEnum.LOGIN);
			if (loginBeanId == null) {
				loginBeanId = loginId;
			}
			if (GuiJsonUtil.isSharedId(msg)) {
				return GuiJsonUtil.toJsonSharedId(loginBeanId);
			}
		} catch (Exception e) {
			return GuiJsonUtil.toError(e);
		}
		return GuiJsonUtil.toError("未知命令:" + msg);
	}

	/**
	 * 获取连接ID
	 */
	public static String getLoginBeanId(Enum<?> mgrType) throws Exception {
		JsonObject sharedIdObj = StartUtil.eng.doCall(mgrType, GuiJsonUtil.genGetShareIdMsg(LoginMgr.ObjType.LOGIN_BEAN));
		return GuiJsonUtil.toStrSharedId(sharedIdObj);
	}

	private void openTab(String id, String title) {
		StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainFrameMgr.ADD_TAB_PANE_ITEM)
				.add("id", id)
				.add("title", title)
				.add(MainFrameMgr.PARAM_MGR_TYPE, CsMgrEnum.TOOLBAR.name()));
	}

	void reConn(LoginBean loginBean) throws ClassNotFoundException, SQLException {
		if (loginBean.getConn() != null && !loginBean.getConn().isClosed()) {
			return;
		}
		Connection conn = ConnUtil.getConn(loginBean.getJdbc());
		loginBean.setConn(conn);
	}
}
