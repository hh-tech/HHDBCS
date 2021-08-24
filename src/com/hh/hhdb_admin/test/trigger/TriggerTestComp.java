package com.hh.hhdb_admin.test.trigger;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.input.WithLabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.trigger.TriggerMgr;
import com.hh.hhdb_admin.test.AbsMainTestComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class TriggerTestComp extends AbsMainTestComp{
	@Override
	public void init() {
		HPanel panel = new HPanel(new HDivLayout());
		HPanel panelLinkInfo = new HPanel();
		JdbcBean bean = MgrTestUtil.getJdbcBean();
		panelLinkInfo.add(new LabelInput(bean.toJson().toPrettyString()));
		panelLinkInfo.setTitle("当前连接信息");
		panel.add(panelLinkInfo);

		//参数面版
		TextInput input = new TextInput("");
		TextInput nameInput = new TextInput("");
		HDivLayout hdiv = new HDivLayout(30,0, GridSplitEnum.C3);
		HPanel hPane2 = new HPanel(hdiv);
		hPane2.setTitle("参数设置");
		panel.add(new WithLabelInput(hPane2,"(P1)表名(表下触发器需填写)：",input));
		panel.add(new WithLabelInput(hPane2,"(P2)触发器(修改需填写)：",nameInput));


		//新建触发器
		HButton showBtn=new HButton("新建触发器") {
			@Override
			public void onClick() {
					StartUtil.eng.doPush(CsMgrEnum.TRIGGER, GuiJsonUtil.toJsonCmd(TriggerMgr.CMD_ADD_TRIGGER).add(StartUtil.PARAM_SCHEMA,TriggerTestUtil.getTestSchema() )
							.add(StartUtil.PARAM_TABLE, input.getValue()));
				}

		};
		panel.add(showBtn);

		//新建触发器
		HButton updateBtn=new HButton("修改触发器（P1，P2）") {
			@Override
			public void onClick() {
				StartUtil.eng.doPush(CsMgrEnum.TRIGGER, GuiJsonUtil.toJsonCmd(TriggerMgr.CMD_UPDATE_TRIGGER).add(StartUtil.PARAM_SCHEMA,TriggerTestUtil.getTestSchema() )
						.add(StartUtil.PARAM_TABLE, input.getValue()).add(TriggerMgr.PARAM_TRIGGER_NAME, nameInput.getValue()));
			}

		};
		panel.add(updateBtn);



		tFrame.setRootPanel(panel);
		tFrame.maximize();
	}



}
