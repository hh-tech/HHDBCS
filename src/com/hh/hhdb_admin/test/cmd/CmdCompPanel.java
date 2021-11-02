package com.hh.hhdb_admin.test.cmd;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.cmd.CmdComp;
import com.hh.hhdb_admin.mgr.cmd.CmdMgr;
import com.hh.hhdb_admin.test.AbsMainTestComp;

public class CmdCompPanel extends AbsMainTestComp{
	private  String id;

	@Override
	public void init() {
		HBarLayout l = new HBarLayout();
		l.setAlign(AlignEnum.LEFT);
		HBarPanel toolBar = new HBarPanel(l);

		HButton showBtn=new HButton("打开cmd") {
			@Override
			public void onClick() {
				try {
					JsonObject jsonObject = StartUtil.eng.doCall(CsMgrEnum.CMD, GuiJsonUtil.toJsonCmd(CmdMgr.CMD_SHOW_CMD));
					id = GuiJsonUtil.toPropValue(jsonObject,StartUtil.CMD_ID);
					CmdComp cmd = (CmdComp) StartUtil.eng.getSharedObj(id);
					getDialog().setSize(1200, 800);
					HPanel panel = new HPanel();
					LastPanel lp = new LastPanel();
					lp.set(cmd.getComp());
					panel.setLastPanel(lp);
					getDialog().setRootPanel(panel);
					getDialog().show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		toolBar.add(showBtn);

		HButton closeBtn=new HButton("关闭cmd") {
			@Override
			public void onClick() {
				try {
					StartUtil.eng.doPush(CsMgrEnum.CMD, GuiJsonUtil.toJsonCmd(StartUtil.CMD_CLOSE).add(StartUtil.CMD_ID, id));
					getDialog().hide();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		toolBar.add(closeBtn);

		tFrame.setToolBar(toolBar);
	}
}
