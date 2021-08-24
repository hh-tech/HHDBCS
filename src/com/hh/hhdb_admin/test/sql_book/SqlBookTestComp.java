package com.hh.hhdb_admin.test.sql_book;

import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.sql_book.SqlBookMgr;
import com.hh.hhdb_admin.test.AbsMainTestComp;

public class SqlBookTestComp extends AbsMainTestComp{
	@Override
	public void init() {
		HPanel panel = new HPanel(new HDivLayout());

		

		//新建用户
		HButton showBtn=new HButton("打开sql宝典") {
			@Override
			public void onClick() {
				StartUtil.eng.doPush(CsMgrEnum.SQL_BOOK, GuiJsonUtil.toJsonCmd(SqlBookMgr.CMD_SHOW_SQL_BOOK));
			}
		};
		panel.add(showBtn);
		
		
		//获取sqlbook路径
		HButton pathBtn=new HButton("获取当前sqlbook路径") {
			@Override
			public void onClick() {
				try {
					JsonObject o = StartUtil.eng.doCall(CsMgrEnum.SQL_BOOK, GuiJsonUtil.genGetShareIdMsg(SqlBookMgr.ObjType.SHARE_PATH));
					PopPaneUtil.error(GuiJsonUtil.toStrSharedId(o));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		panel.add(pathBtn);

		
		tFrame.setRootPanel(panel);
		tFrame.maximize();
	}


}
