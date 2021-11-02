package com.hh.hhdb_admin.mgr.cmd;

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

public class CmdMgr extends AbsGuiMgr {
	public static final String CMD_SHOW_CMD="CMD_SHOW_CMD";

	@Override
	public void init(JsonObject jObj) {
		try {
			LangMgr2.loadMerge(CmdMgr.class);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public CsMgrEnum getType() {
		return CsMgrEnum.CMD;
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
                CmdComp cmd = (CmdComp)StartUtil.eng.getSharedObj(id);
				cmd.close();
				StartUtil.eng.rmFromSharedMap(id);
				break;
			case CMD_SHOW_CMD:
				JsonObject jsonObj= StartUtil.eng.doCall(CsMgrEnum.LOGIN, GuiJsonUtil.genGetShareIdMsg(null));
				LoginBean logBean=(LoginBean)StartUtil.eng.getSharedObj(GuiJsonUtil.toStrSharedId(jsonObj));
				JdbcBean jdbc = JdbcBean.toJdbc(logBean.getJdbc().toJson());
				
				id = StartUtil.eng.push2SharedMap(new CmdComp(jdbc));
				StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainFrameMgr.ADD_TAB_PANE_ITEM)
						.add(StartUtil.CMD_ID,id).add("title", getLang("cmd")).add(MainFrameMgr.PARAM_MGR_TYPE, CsMgrEnum.CMD.name()));
				break;
			default:
				unknowMsg(msg.toPrettyString());
		}
	}

	@Override
	public JsonObject doCall(JsonObject msg) throws Exception {
		JsonObject res = new JsonObject();
		switch (GuiJsonUtil.toStrCmd(msg)) {
			case CMD_SHOW_CMD:  //测试用例打开
				JsonObject jsonObj= StartUtil.eng.doCall(CsMgrEnum.LOGIN, GuiJsonUtil.genGetShareIdMsg(null));
				LoginBean logBean=(LoginBean)StartUtil.eng.getSharedObj(GuiJsonUtil.toStrSharedId(jsonObj));
//
				String id = StartUtil.eng.push2SharedMap(new CmdComp(logBean.getJdbc()));
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
		return LangMgr2.getValue(CmdMgr.class.getName(), key);
	}

	public static ImageIcon getIcon(String name) {
		return IconFileUtil.getIcon(new IconBean(CsMgrEnum.CMD.name(), name, IconSizeEnum.SIZE_16));
	}
}
