package com.hh.hhdb_admin.test.sequence;

import java.io.File;

import com.hh.frame.common.util.ClassLoadUtil;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.GuiEngine;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.test.MainTestMgr;

public class SeqMgrTest {
	
	public static GuiEngine eng=null;
	
	public static void main(String[] args) throws Exception {
			
			try {
	            //初始化自定义UI
	            HHSwingUi.init();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
			IconFileUtil.setIconBaseDir(new File("etc/icon/"));
			String jStr=ClassLoadUtil.loadTextRes(SeqMgrTest.class, "conf.json");
			JsonObject jObj=Json.parse(jStr).asObject();
			StartUtil.eng=eng=new GuiEngine(CsMgrEnum.class,jObj);
			eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainTestMgr.CMD_SHOW));
		
	}

}
