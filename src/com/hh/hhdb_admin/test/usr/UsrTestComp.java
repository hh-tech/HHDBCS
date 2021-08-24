package com.hh.hhdb_admin.test.usr;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
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
import com.hh.hhdb_admin.mgr.usr.UsrMgr;
import com.hh.hhdb_admin.test.AbsMainTestComp;
import com.hh.hhdb_admin.test.MgrTestUtil;
import org.apache.commons.lang3.StringUtils;

public class UsrTestComp extends AbsMainTestComp{
	@Override
	public void init() {
		HPanel panel = new HPanel(new HDivLayout());
		HPanel panelLinkInfo = new HPanel();
		panelLinkInfo.add(new LabelInput(MgrTestUtil.getJdbcBean().toJson().toPrettyString()));
		panelLinkInfo.setTitle("当前连接信息");
		panel.add(panelLinkInfo);

		//新建用户
		HButton showBtn=new HButton("新建用户") {
			@Override
			public void onClick() {
				StartUtil.eng.doPush(CsMgrEnum.USR, GuiJsonUtil.toJsonCmd(UsrMgr.CMD_SHOW_ADD_USER));
			}
		};
		panel.add(showBtn);

		//新建角色
		HButton newBtn=new HButton("新建角色") {
			@Override
			public void onClick() {
				StartUtil.eng.doPush(CsMgrEnum.USR, GuiJsonUtil.toJsonCmd(UsrMgr.CMD_SHOW_ADD_ROLE));
			}
		};
		panel.add(newBtn);

		//参数面版
		TextInput input = new TextInput("userName");
		input.setValue(getDefaultUsr());
		HDivLayout hdiv = new HDivLayout(30,0, GridSplitEnum.C3);
		HPanel hPane2 = new HPanel(hdiv);
		hPane2.setTitle("参数设置");
		panel.add(new WithLabelInput(hPane2,"(P1)用户名/角色名：",input));

		//修改用户
		HButton updateBtn=new HButton("修改用户(需要配置参数P1)") {
			@Override
			public void onClick() {
				if(verifyParam(input.getValue())) {
					JsonObject obj = GuiJsonUtil.toJsonCmd(UsrMgr.CMD_SHOW_UPDATE_USER);
					obj.set(UsrMgr.PARAM_USR_NAME, input.getValue());
					StartUtil.eng.doPush(CsMgrEnum.USR, obj);
				}
			}
		};
		panel.add(updateBtn);
		//修改角色
		HButton roleBtn=new HButton("修改角色(需要配置参数P1)") {
			@Override
			public void onClick() {
				if(verifyParam(input.getValue())) {
					JsonObject obj = GuiJsonUtil.toJsonCmd(UsrMgr.CMD_SHOW_UPDATE_ROLE);
					obj.set(UsrMgr.PARAM_USR_NAME, input.getValue());
					StartUtil.eng.doPush(CsMgrEnum.USR, obj);
				}
			}
		};
		panel.add(roleBtn);

		//修改权限
		HButton permBtn=new HButton("用户权限(需要配置参数P1)") {
			@Override
			public void onClick() {
				if(verifyParam(input.getValue())) {
					JsonObject permission = GuiJsonUtil.toJsonCmd(UsrMgr.CMD_SHOW_PERMISSION);
					permission.set(UsrMgr.PARAM_USR_NAME, input.getValue());
					StartUtil.eng.doPush(CsMgrEnum.USR, permission);
				}

			}
		};
		panel.add(permBtn);

		//重命名用户
		HButton renameBtn=new HButton("重命名用户(需要配置参数P1)") {
			@Override
			public void onClick() {
				if(verifyParam(input.getValue())) {
					JsonObject renameObj = GuiJsonUtil.toJsonCmd(UsrMgr.CMD_SHOW_RENAME);
					renameObj.set(UsrMgr.PARAM_USR_NAME, input.getValue());
					StartUtil.eng.doPush(CsMgrEnum.USR, renameObj);
				}
			}
		};
		panel.add(renameBtn);
		tFrame.setRootPanel(panel);
		tFrame.maximize();
	}

	private  static String getDefaultUsr(){
		String usr = "test";
		DBTypeEnum db_type = DriverUtil.getDbType(MgrTestUtil.getJdbcBean());
		if(db_type == DBTypeEnum.oracle){
			usr = "TEST";
		}else if(db_type == DBTypeEnum.hhdb){
			usr = "test";
		}else if(db_type == DBTypeEnum.pgsql){
			usr = "test";
		}else if(db_type == DBTypeEnum.mysql){
			usr = "test@%";
		}
		return usr;
	}
	private boolean verifyParam(String value){
		if(StringUtils.isBlank(value)){
			PopPaneUtil.error("请先设置参数");
			return false;
		}
		return true;
	}

}
