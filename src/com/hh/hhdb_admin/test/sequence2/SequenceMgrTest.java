package com.hh.hhdb_admin.test.sequence2;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.sequence2.SeqBean;
import com.hh.hhdb_admin.test.MgrTestUtil;
import org.apache.commons.lang3.StringUtils;

public class SequenceMgrTest {
	public static String SCHEMA = "HU";
	
	public static String SEQNAME= "SEQ_HU";
	
	public static void main(String[] args) throws Exception {
		
		
		MgrTestUtil.initCsVarJdbc();
		MgrTestUtil.doTestMgr(SequenceMgrTest.class);
		HBarPanel barPanel = new HBarPanel(HBarLayout.newLayout(AlignEnum.LEFT));
		
		TextInput schema = new TextInput();
		schema.getComp().setColumns(20);
		
		TextInput table = new TextInput();
		table.getComp().setColumns(20);
		
		schema.setValue(SCHEMA);
		
		barPanel.add(new LabelInput("Schema:"),schema);
		
		
		HBarPanel barPanel2 = new HBarPanel(HBarLayout.newLayout(AlignEnum.LEFT));
		
		barPanel2.add(new HButton("ADD") {
			@Override
			protected void onClick() {
				if(StringUtils.isBlank(schema.getValue())) {
					PopDlgUtil.info(getComp(), "请输入Schema");
				}
				SeqBean csGuiBean = new SeqBean(SeqBean.OprType.ADD,null);
				csGuiBean.setSchema(schema.getValue());
				CsVarUtil.eng2.doCall(CsMgrEnum.SEQUENCE, csGuiBean);
			}
		});
		
		barPanel2.add(new HButton("UPDATE") {
			@Override
			protected void onClick() {
				
				if(StringUtils.isBlank(schema.getValue())) {
					PopDlgUtil.info(getComp(), "请输入Schema");
				}
				
				String name = PopDlgUtil.nameInput("修改的序列名", SEQNAME);
				if(StringUtils.isBlank(name)) {
					return;
				}
				SeqBean csGuiBean = new SeqBean(SeqBean.OprType.UPDATE,null);
				csGuiBean.setSchema(schema.getValue());
				csGuiBean.setName(name);
				CsVarUtil.eng2.doCall(CsMgrEnum.SEQUENCE, csGuiBean);
			}
		});
		barPanel2.add(new HButton("RENAME") {
			@Override
			protected void onClick() {
				
				
				if(StringUtils.isBlank(schema.getValue())) {
					PopDlgUtil.info(getComp(), "请输入Schema");
				}
				
				String name = PopDlgUtil.nameInput("修改的序列原名", SEQNAME);
				if(StringUtils.isBlank(name)) {
					return;
				}
				SeqBean csGuiBean = new SeqBean(SeqBean.OprType.RENAME,null);
				csGuiBean.setSchema(schema.getValue());
				csGuiBean.setName(name);
				CsVarUtil.eng2.doCall(CsMgrEnum.SEQUENCE, csGuiBean);
			}
		});
		
		
		HPanel p = new HPanel();
		p.add(barPanel);
		p.add(barPanel2);
		MgrTestUtil.showComp(p);
		CsVarUtil.parentFrame.setWindowTitle("添加测试用例");
	}

}
