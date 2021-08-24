package com.hh.hhdb_admin.test.view;

import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.input.WithLabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.view.ViewMgr;
import com.hh.hhdb_admin.test.AbsMainTestComp;
import com.hh.hhdb_admin.test.MgrTestUtil;
import org.apache.commons.lang3.StringUtils;

public class ViewTestComp extends AbsMainTestComp{

	@Override
	public void init() {
//		JdbcBean bean = MgrTestUtil.getJdbcBean();
		HPanel panel = new HPanel(new HDivLayout());
		HPanel panelLinkInfo = new HPanel();
		panelLinkInfo.add(new LabelInput(MgrTestUtil.getJdbcBean().toJson().toPrettyString()));
		panelLinkInfo.setTitle("当前连接信息");
		panel.add(panelLinkInfo);


		panel.add( getCreateViewBtn(true));
		panel.add( getCreateViewBtn(false));

		//参数面版
		TextInput input = new TextInput("viewName");
		HDivLayout hdiv = new HDivLayout(30,0, GridSplitEnum.C3);
		HPanel hPane2 = new HPanel(hdiv);
		hPane2.setTitle("参数设置");
		panel.add(new WithLabelInput(hPane2,"(P1)视图/物化视图 名称：",input));


		panel.add( getUpdateViewBtn(input,true));
		panel.add( getUpdateViewBtn(input,false));

		panel.add(getOpenViewBtn(input,true));
		panel.add(getOpenViewBtn(input,false));
		tFrame.setRootPanel(panel);
	}

	/**
	 * 修改视图
	 * @return
	 */
	private HButton getCreateViewBtn ( boolean isMView){
		return new HButton(isMView?"添加物化视图":"添加视图") {
			@Override
			public void onClick() {
				JsonObject object = GuiJsonUtil.toJsonCmd(isMView?ViewMgr.CMD_SHOW_CREATE_MVIEW:ViewMgr.CMD_SHOW_CREATE_VIEW);
				object.add(StartUtil.PARAM_SCHEMA,MgrTestUtil.getJdbcBean().getSchema());
				StartUtil.eng.doPush(CsMgrEnum.VIEW, object);
			}
		};

	}

	/**
	 * 修改视图
	 * @return
	 */
	private HButton getUpdateViewBtn ( TextInput input,boolean isMView){
		return new HButton(isMView?"修改物化视图(需要设置参数P1)":"修改视图(需要设置参数P1)") {
			@Override
			public void onClick() {
				if(!verifyParam(input.getValue())) {
					return;
				}
				JsonObject object = GuiJsonUtil.toJsonCmd(isMView?ViewMgr.CMD_SHOW_UPDATE_MVIEW:ViewMgr.CMD_SHOW_UPDATE_VIEW);
				object.add(ViewMgr.PARAM_VIEW_NAME,input.getValue());
				object.add(StartUtil.PARAM_SCHEMA,MgrTestUtil.getJdbcBean().getSchema());
				StartUtil.eng.doPush(CsMgrEnum.VIEW, object);
			}
		};

	}
	/**
	 * 打开视图
	 * @return
	 */
	private HButton getOpenViewBtn ( TextInput input,boolean isMView){
		return new HButton(isMView?"打开物化视图(需要设置参数P1)":"打开视图(需要设置参数P1)") {
			@Override
			public void onClick() {
				if(!verifyParam(input.getValue())) {
					return;
				}
				JsonObject object = GuiJsonUtil.toJsonCmd(isMView?ViewMgr.CMD_SHOW_OPEN_MVIEW:ViewMgr.CMD_SHOW_OPEN_VIEW);
				object.add(ViewMgr.PARAM_VIEW_NAME,input.getValue());
				object.add(StartUtil.PARAM_SCHEMA,MgrTestUtil.getJdbcBean().getSchema());
				StartUtil.eng.doPush(CsMgrEnum.VIEW, object);
			}
		};

	}

	private boolean verifyParam(String value){
		if(StringUtils.isBlank(value)){
			PopPaneUtil.error("请先设置参数");
			return false;
		}
		return true;
	}

}
