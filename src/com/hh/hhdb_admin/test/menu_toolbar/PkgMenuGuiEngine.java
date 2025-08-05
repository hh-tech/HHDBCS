package com.hh.hhdb_admin.test.menu_toolbar;

import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine2.GuiEngine;
import com.hh.frame.swingui.engine2.GuiMgrArrBean;

class PkgMenuGuiEngine extends GuiEngine {

	public <T extends Enum<T>> PkgMenuGuiEngine(Class<T> mgrType, GuiMgrArrBean arr) throws Exception {
		super(mgrType, arr);
	}
	
	
	public JsonObject doCall(Enum<?> mgrType, JsonObject msg) throws Exception {
		print(mgrType,msg);
		return null;
	}
	
	private void print(Enum<?> mgrType, JsonObject msg) {
		System.out.println(mgrType.name());
		System.out.println(msg);
	}

}
