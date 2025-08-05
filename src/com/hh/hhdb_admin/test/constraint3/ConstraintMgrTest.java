package com.hh.hhdb_admin.test.constraint3;

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
import com.hh.hhdb_admin.mgr.constraint3.ConstraintBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class ConstraintMgrTest {
	public static String TABLE = "MYTABLE";
	public static String SCHEMA = "HU";
	
	public static void main(String[] args) throws Exception {
		
		
		MgrTestUtil.initCsVarJdbc();
		MgrTestUtil.doTestMgr(ConstraintMgrTest.class);
		HBarPanel barPanel = new HBarPanel(HBarLayout.newLayout(AlignEnum.LEFT));
		
		TextInput schema = new TextInput();
		schema.getComp().setColumns(20);
		
		TextInput table = new TextInput();
		table.getComp().setColumns(20);
		
		schema.setValue(SCHEMA);
		table.setValue(TABLE);
		
		barPanel.add(new LabelInput("Schema:"),schema);
		barPanel.add(new LabelInput("Table:"),table);
		
		
		HBarPanel barPanel2 = new HBarPanel(HBarLayout.newLayout(AlignEnum.LEFT));
		
		barPanel2.add(new HButton("创建primaryKey") {
			@Override
			protected void onClick() {
				
				if(StringUtils.isBlank(schema.getValue())) {
					PopDlgUtil.info(getComp(), "请输入Schema");
				}
				
				if(StringUtils.isBlank(table.getValue())) {
					PopDlgUtil.info(getComp(), "请输入table");
				}
				
				ConstraintBean bean = new ConstraintBean(ConstraintBean.OprType.PK,"");
				bean.setSchema(schema.getValue());
				bean.setTableName(table.getValue());
				bean.setConstType(TreeMrType.PRIMARY_KEY_GROUP);
				CsVarUtil.eng2.doCall(CsMgrEnum.CONSTRAINT, bean);
			}
		});
		
		barPanel2.add(new HButton("创建uniqueKey") {
			@Override
			protected void onClick() {
				
				if(StringUtils.isBlank(schema.getValue())) {
					PopDlgUtil.info(getComp(), "请输入Schema");
				}
				
				if(StringUtils.isBlank(table.getValue())) {
					PopDlgUtil.info(getComp(), "请输入table");
				}
				
				ConstraintBean bean = new ConstraintBean(ConstraintBean.OprType.UK,"");
				bean.setSchema(schema.getValue());
				bean.setTableName(table.getValue());
				bean.setConstType(TreeMrType.UNIQUE_KEY_GROUP);
				CsVarUtil.eng2.doCall(CsMgrEnum.CONSTRAINT, bean);
			}
		});
		
		
		HPanel p = new HPanel();
		p.add(barPanel);
		p.add(barPanel2);
		MgrTestUtil.showComp(p);
		CsVarUtil.parentFrame.setWindowTitle("添加测试用例");
	}

}
