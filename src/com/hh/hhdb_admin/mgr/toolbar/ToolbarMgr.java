package com.hh.hhdb_admin.mgr.toolbar;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author: Jiang
 * @date: 2020/10/12
 */

public class ToolbarMgr extends AbsGuiMgr {

	public static final String CMD_INIT = "INIT";

	private String objId = null;

	@Override
	public void init(JsonObject jObj) {

	}

	@Override
	public String getHelp() {
		return null;
	}

	@Override
	public Enum<?> getType() {
		return CsMgrEnum.TOOLBAR;
	}

	@Override
	public void doPush(JsonObject msg) throws Exception {
	}

	@Override
	public JsonObject doCall(JsonObject msg) throws Exception {
		JsonObject res = new JsonObject();
		if (CMD_INIT.equals(GuiJsonUtil.toStrCmd(msg))) {
			if (objId != null) {
				StartUtil.eng.rmFromSharedMap(objId);
			}
			HBarLayout layout = new HBarLayout();
			layout.setAlign(AlignEnum.LEFT);
			layout.setBottomHeight(10);
			ToolbarComp toolbarComp = new ToolbarComp(layout);
			toolbarComp.getComp().setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.lightGray));
			objId = StartUtil.eng.push2SharedMap(toolbarComp);
			res.set("id", objId);
		} else {
			unknowMsg(msg.toPrettyString());
		}
		return res;
	}
}
