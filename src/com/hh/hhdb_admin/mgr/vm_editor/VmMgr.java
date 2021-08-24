package com.hh.hhdb_admin.mgr.vm_editor;

import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangMgr;
import com.hh.frame.lang.LangUtil;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.main_frame.MainFrameMgr;

import javax.swing.*;

/**
 * 模板编辑器
 */
public class VmMgr extends AbsGuiMgr {
	public static final String CMD_SHOW_VM="SHOW_VM";


	@Override
	public void init(JsonObject jObj) {
		LangMgr.merge(VmMgr.class.getName(), LangUtil.loadLangRes(VmMgr.class));
	}

	@Override
	public CsMgrEnum getType() {
		return CsMgrEnum.VM;
	}

	@Override
	public String getHelp() {
		return null;
	}

	@Override
	public void doPush(JsonObject msg) throws Exception {
		switch (GuiJsonUtil.toStrCmd(msg)) {
			case StartUtil.CMD_CLOSE:
				StartUtil.eng.rmFromSharedMap(GuiJsonUtil.toPropValue(msg,StartUtil.CMD_ID));
				break;
			case CMD_SHOW_VM:
                String id = StartUtil.eng.push2SharedMap(new VmComp(GuiJsonUtil.toPropValue(msg,"text")));
				StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainFrameMgr.ADD_TAB_PANE_ITEM)
						.add(StartUtil.CMD_ID,id).add("title", getLang("vm_editor")).add(MainFrameMgr.PARAM_MGR_TYPE, CsMgrEnum.VM.name()));
				break;
			default:
				unknowMsg(msg.toPrettyString());
		}
	}

	@Override
	public JsonObject doCall(JsonObject msg) throws Exception {
		JsonObject res = new JsonObject();
		switch (GuiJsonUtil.toStrCmd(msg)) {
			case CMD_SHOW_VM:  //测试用例打开查询器
				String id = StartUtil.eng.push2SharedMap(new VmComp(GuiJsonUtil.toPropValue(msg,"text")));
				res.add(StartUtil.CMD_ID, id);
				break;
			default:
				unknowMsg(msg.toPrettyString());
		}
		return res;
	}

	public static String getLang(String key) {
		return LangMgr.getValue(VmMgr.class.getName(), key);
	}

	public static ImageIcon getIcon(String name) {
		return IconFileUtil.getIcon(new IconBean(CsMgrEnum.VM.name(), name, IconSizeEnum.SIZE_16));
	}
}
