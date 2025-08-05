package com.hh.hhdb_admin.test.funcntion2;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.function2.FunctionBean;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class FunctionMgrTest {
	public static  String SCHEMA = "HU";

	public static void main(String[] args) throws Exception {
		
		
		
		MgrTestUtil.initCsVarJdbc();
		MgrTestUtil.doTestMgr(FunctionMgrTest.class);
	
		
		
		HPanel panel = new HPanel();
		HBarPanel bar = new HBarPanel();
		bar.add(new LabelInput("模式"));
		TextInput  schemaInput = new TextInput();
		schemaInput.setValue(SCHEMA);
		bar.add(schemaInput);
		
		HBarPanel barPanel = new HBarPanel(HBarLayout.newLayout(AlignEnum.LEFT));
		barPanel.add(new HButton("ADD Function") {
			@Override
			protected void onClick() {
				
				FunctionBean csGuiBean = new FunctionBean(FunctionBean.OprType.ADD,null);
				csGuiBean.setNodeType(TreeMrType.FUNCTION);
				csGuiBean.setSchema(schemaInput.getValue());
				
				CsVarUtil.eng2.doCall(CsMgrEnum.FUNCTION, csGuiBean);
			}
		});
		barPanel.add(new HButton("Design Function") {
			@Override
			protected void onClick() {
				String name = PopDlgUtil.nameInput(getComp(), "请输入一个存在的函数名：", "");
				if(!StringUtils.isBlank(name)) {
					FunctionBean csGuiBean = new FunctionBean(FunctionBean.OprType.UPDATE,null);
					csGuiBean.setNodeType(TreeMrType.FUNCTION);
					csGuiBean.setSchema(schemaInput.getValue());
					csGuiBean.setName(name);
					CsVarUtil.eng2.doCall(CsMgrEnum.FUNCTION, csGuiBean);
				}
				
			}
		});
		
		barPanel.add(new HButton("ADD PROCEDURE") {
			@Override
			protected void onClick() {
				
				FunctionBean csGuiBean = new FunctionBean(FunctionBean.OprType.ADD,null);
				csGuiBean.setNodeType(TreeMrType.PROCEDURE);
				csGuiBean.setSchema(schemaInput.getValue());
				
				CsVarUtil.eng2.doCall(CsMgrEnum.FUNCTION, csGuiBean);
			}
		});
		
		barPanel.add(new HButton("Design PROCEDURE") {
			@Override
			protected void onClick() {
				String name = PopDlgUtil.nameInput(getComp(), "请输入一个存在的存储过程名：", "");
				if(!StringUtils.isBlank(name)) {
					FunctionBean csGuiBean = new FunctionBean(FunctionBean.OprType.UPDATE,null);
					csGuiBean.setNodeType(TreeMrType.PROCEDURE);
					csGuiBean.setSchema(schemaInput.getValue());
					csGuiBean.setName(name);
					CsVarUtil.eng2.doCall(CsMgrEnum.FUNCTION, csGuiBean);
				}
				
			}
		});
		
		
		barPanel.add(new HButton("Run function") {
			@Override
			protected void onClick() {
				String name = PopDlgUtil.nameInput(getComp(), "请输入一个存在的函数名：", "");
				if(!StringUtils.isBlank(name)) {
					FunctionBean csGuiBean = new FunctionBean(FunctionBean.OprType.RUN,null);
					csGuiBean.setNodeType(TreeMrType.FUNCTION);
					csGuiBean.setSchema(schemaInput.getValue());
					csGuiBean.setName(name);
					CsVarUtil.eng2.doCall(CsMgrEnum.FUNCTION, csGuiBean);
				}
				
			}
		});
		
		barPanel.add(new HButton("Run Procedure") {
			@Override
			protected void onClick() {
				String name = PopDlgUtil.nameInput(getComp(), "请输入存在的存储过程名：", "");
				if(!StringUtils.isBlank(name)) {
					FunctionBean csGuiBean = new FunctionBean(FunctionBean.OprType.RUN,null);
					csGuiBean.setNodeType(TreeMrType.PROCEDURE);
					csGuiBean.setSchema(schemaInput.getValue());
					csGuiBean.setName(name);
					CsVarUtil.eng2.doCall(CsMgrEnum.FUNCTION, csGuiBean);
				}
				
			}
		});
		
		barPanel.add(new HButton("debug function") {
			@Override
			protected void onClick() {
				String name = PopDlgUtil.nameInput(getComp(), "请输入一个存在的函数名：", "");
				if(!StringUtils.isBlank(name)) {
					FunctionBean csGuiBean = new FunctionBean(FunctionBean.OprType.DEBUG,null);
					csGuiBean.setNodeType(TreeMrType.FUNCTION);
					csGuiBean.setSchema(schemaInput.getValue());
					csGuiBean.setName(name);
					CsVarUtil.eng2.doCall(CsMgrEnum.FUNCTION, csGuiBean);
				}
				
			}
		});
		
		panel.add(barPanel);
		MgrTestUtil.showComp(panel);
		CsVarUtil.parentFrame.setWindowTitle("测试用例");
	}

}
