package com.hh.hhdb_admin.mgr.query;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.main_frame.MainFrameMgr;

import javax.swing.*;

public class QueryMgr extends AbsGuiMgr {
	public static int sign = 0; //临时文件夹序号标记
	public static final String CMD_SHOW_QUERY="SHOW_QUERY";		//显示查询器面板

	@Override
	public void init(JsonObject jObj) {
		sign = 0;
		try {
			LangMgr2.loadMerge(QueryMgr.class);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public CsMgrEnum getType() {
		return CsMgrEnum.QUERY;
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
				id = GuiJsonUtil.toPropValue(msg,StartUtil.CMD_ID);
				QueryComp query = (QueryComp)StartUtil.eng.getSharedObj(id);
				query.close();
				StartUtil.eng.rmFromSharedMap(id);
				break;
			case CMD_SHOW_QUERY:
				LoginBean logBean=StartUtil.getLoginBean();
				JdbcBean jdbc = JdbcBean.toJdbc(logBean.getJdbc().toJson());

				QueryComp queryPanel = new QueryComp(jdbc,GuiJsonUtil.toPropValue(msg,"text"));
				id = StartUtil.eng.push2SharedMap(queryPanel);

				StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainFrameMgr.ADD_TAB_PANE_ITEM)
						.add(StartUtil.CMD_ID,id).add("title", getLang("query")).add(MainFrameMgr.PARAM_MGR_TYPE, CsMgrEnum.QUERY.name()));
				break;
			default:
				unknowMsg(msg.toPrettyString());
		}
	}

	@Override
	public JsonObject doCall(JsonObject msg) throws Exception {
		JsonObject res = new JsonObject();
		switch (GuiJsonUtil.toStrCmd(msg)) {
			case CMD_SHOW_QUERY:  //测试用例打开查询器
				JsonObject jsonObj= StartUtil.eng.doCall(CsMgrEnum.LOGIN, GuiJsonUtil.genGetShareIdMsg(null));
				LoginBean logBean=(LoginBean)StartUtil.eng.getSharedObj(GuiJsonUtil.toStrSharedId(jsonObj));

				QueryComp queryPanel = new QueryComp(logBean.getJdbc(),GuiJsonUtil.toPropValue(msg,"text"));
				String id = StartUtil.eng.push2SharedMap(queryPanel);

				res.add(StartUtil.CMD_ID, id);
				break;
			default:
				unknowMsg(msg.toPrettyString());
		}
		return res;
	}

	/**
	 * 中英文
	 * @param key
	 * @return
	 */
	public static String getLang(String key) {
		return LangMgr2.getValue(QueryMgr.class.getName(), key);
	}

	public static ImageIcon getIcon(String name) {
		return IconFileUtil.getIcon(new IconBean(CsMgrEnum.QUERY.name(), name, IconSizeEnum.SIZE_16));
	}
}
