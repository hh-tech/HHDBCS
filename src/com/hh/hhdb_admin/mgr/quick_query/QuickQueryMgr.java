package com.hh.hhdb_admin.mgr.quick_query;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangMgr;
import com.hh.frame.lang.LangUtil;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.main_frame.MainFrameMgr;

import javax.swing.*;

public class QuickQueryMgr extends AbsGuiMgr {

	public static final String CMD_SHOW_QUICKQUERY="SHOW_QUICKQUERY";
	public static final String CMD_SHOW_QUICK_QUERY = "CMD_SHOW_QUICK_QUERY";

	@Override
	public void init(JsonObject jObj) {
		LangMgr.merge(QuickQueryMgr.class.getName(), LangUtil.loadLangRes(QuickQueryMgr.class));
	}

	@Override
	public CsMgrEnum getType() {
		return CsMgrEnum.QUICK_CMD;
	}

	@Override
	public String getHelp() {
		return null;
	}

	@Override
	public void doPush(JsonObject msg) throws Exception {
		String id;
		switch (GuiJsonUtil.toStrCmd(msg)) {
			case StartUtil.CMD_CLOSE:
				StartUtil.eng.rmFromSharedMap(GuiJsonUtil.toPropValue(msg,StartUtil.CMD_ID));
				break;
			case CMD_SHOW_QUICKQUERY:
				JsonObject jsonObj= StartUtil.eng.doCall(CsMgrEnum.LOGIN, GuiJsonUtil.genGetShareIdMsg(null));
				LoginBean logBean=(LoginBean)StartUtil.eng.getSharedObj(GuiJsonUtil.toStrSharedId(jsonObj));

				QuickQueryComp queryPanel = new QuickQueryComp(logBean.getJdbc());
				id = StartUtil.eng.push2SharedMap(queryPanel);

				StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainFrameMgr.ADD_TAB_PANE_ITEM)
						.add(StartUtil.CMD_ID,id).add("title", "查询器").add(MainFrameMgr.PARAM_MGR_TYPE, CsMgrEnum.QUICK_CMD.name()));
				break;
			case CMD_SHOW_QUICK_QUERY:
				queryPanel = new QuickQueryComp(getJdbcBean());
				HPanel panel = new HPanel();
				panel.setLastPanel(queryPanel.getLastPanel());
				StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainFrameMgr.ADD_TAB_PANE_ITEM)
						.add("id", StartUtil.eng.push2SharedMap(panel))
						.add("title", queryPanel.getTitle())
						.add(MainFrameMgr.PARAM_MGR_TYPE, CsMgrEnum.QUICK_CMD.name()));
				break;
			default:
				unknowMsg(msg.toPrettyString());
		}
	}

	@Override
	public JsonObject doCall(JsonObject msg) throws Exception {
		JsonObject res = new JsonObject();
		if (CMD_SHOW_QUICKQUERY.equals(GuiJsonUtil.toStrCmd(msg))) {  //测试用例打开查询器
			JsonObject jsonObj = StartUtil.eng.doCall(CsMgrEnum.LOGIN, GuiJsonUtil.genGetShareIdMsg(null));
			LoginBean logBean = (LoginBean) StartUtil.eng.getSharedObj(GuiJsonUtil.toStrSharedId(jsonObj));

			QuickQueryComp queryPanel = new QuickQueryComp(logBean.getJdbc());
			String id = StartUtil.eng.push2SharedMap(queryPanel);

			res.add(StartUtil.CMD_ID, id);
		} else {
			unknowMsg(msg.toPrettyString());
		}
		return res;
	}

	public static String getLang(String key) {
		return LangMgr.getValue(QuickQueryMgr.class.getName(), key);
	}

	public static ImageIcon getIcon(String name) {
		return IconFileUtil.getIcon(new IconBean(CsMgrEnum.QUERY.name(), name, IconSizeEnum.SIZE_16));
	}

	private JdbcBean getJdbcBean() throws Exception {
		JsonObject jsonObj= StartUtil.eng.doCall(CsMgrEnum.LOGIN, GuiJsonUtil.genGetShareIdMsg(null));
		LoginBean logBean=(LoginBean)StartUtil.eng.getSharedObj(GuiJsonUtil.toStrSharedId(jsonObj));
		return logBean.getJdbc();
	}

}
