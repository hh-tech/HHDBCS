package com.hh.hhdb_admin.mgr.attribute;

import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;

/**
 * @author: Jiang
 * @date: 2020/10/14
 */

public class AttributeMgr extends AbsGuiMgr {

	public static final String SHOW_ATTR = "showAttr";
	public static LoginBean loginBean;

	@Override
	public void init(JsonObject jObj) {
	}

	@Override
	public String getHelp() {
		return null;
	}

	@Override
	public Enum<?> getType() {
		return CsMgrEnum.ATTRIBUTE;
	}

	@Override
	public void doPush(JsonObject msg) throws Exception {
		if (SHOW_ATTR.equals(GuiJsonUtil.toStrCmd(msg))) {
			LoginBean loginBean = StartUtil.getLoginBean();
			if (loginBean == null) {
				return;
			}
			AttributeComp comp = new AttributeComp();
			comp.showAttr(msg, loginBean.isSshAuth() ? loginBean.getOriginalJdbc() : loginBean.getJdbc(), loginBean.getConn());
		} else {
			unknowMsg(msg.toPrettyString());
		}
	}

	@Override
	public JsonObject doCall(JsonObject msg) {
		return null;
	}

}
