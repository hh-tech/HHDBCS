package com.hh.hhdb_admin.test.sequence;

import java.io.File;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.input.WithLabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.sequence.SequenceMgr;
import com.hh.hhdb_admin.mgr.sequence.comp.SeqComp;
import com.hh.hhdb_admin.test.MgrTestUtil;


public class CompTest{
		public  JdbcBean jdbc;
		public  DBTypeEnum type;
		public  HPanel hp;
		public TextAreaInput ment;
	
	public static void main(String[] args) throws Exception {
		
		 try {
				HHSwingUi.init();
			} catch (Exception e) {
				e.printStackTrace();
			}
		  new CompTest();
	}
	
	public CompTest() {
		
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void init() throws Exception {
		IconFileUtil.setIconBaseDir(new File("etc/icon/"));
		jdbc=MgrTestUtil.getJdbcBean();
	 	SequenceMgr.conn=ConnUtil.getConn(jdbc);
	 	type=DriverUtil.getDbType(jdbc);
		HFrame frame=new HFrame();
		HFrame frame1=new HFrame();
		HDialog diaLog=new HDialog(frame1,HDialog.MIDDLE_WIDTH);
	    hp = new HPanel();
	    ment = new TextAreaInput(null,null,5);
		
		TextInput dbType=new TextInput();
		dbType.setValue(type.name());
		dbType.setEnabled(false);
		TextInput schema=new TextInput();
		switch(type) {
		case oracle:
			schema.setValue(jdbc.getUser());
			ment.setValue("当前连接为ORACLE数据库，建议使用大写字母填写模式名和序列名");
			break;
		case mysql:
			ment.setValue("mysql数据库没有序列单独管理功能");
			break;
		case sqlserver:
			schema.setValue(jdbc.getUser());
			ment.setValue("当前连接sqlServer数据库");
			break;
		case hhdb:
			schema.setValue("public");
			ment.setValue("当前连接hhdb数据库");
			break;
		case pgsql:
			schema.setValue("public");
			ment.setValue("当前连接pgsql数据库");
			break;
		case db2:
			schema.setValue(jdbc.getSchema());
			ment.setValue("建议使用大写字母填写模式名和序列名");
			break;
		default:
			ment.setValue("请修改当前类，添加新的数据库型号:"+this.getClass().getName());
		}
		TextInput seq=new TextInput();
		hp.add(getLabelInput("数据库类型:", dbType));
		hp.add(getLabelInput("模式名：", schema));
		hp.add(getLabelInput("序列名：", seq));
		
		HButton create = new HButton("新增") {
		@Override
		protected void onClick() {
			String seqName=seq.getValue();
			if(StringUtils.isBlank(seqName)){
				ment.setValue("序列名不能为空");
				hp.updateUI();
				return;
			}
			SeqComp gcomp = new SeqComp(type, 
					schema.getValue(), seqName, false) {@Override
					protected void refreshSeq() {
						String str="创建序列："+seqName;
						ment.setValue(str);
						hp.updateUI();
					}};
			gcomp.showCreate(diaLog);
			
		}};
		
		HButton updata = new HButton("设计") {@Override
		protected void onClick() {
			String seqName=seq.getValue();
			if(StringUtils.isBlank(seqName)){
				ment.setValue("请输入需要修改的序列名");
				hp.updateUI();
				return;
			}
			SeqComp gcomp = new SeqComp(type, 
					schema.getValue(), seqName, true) {@Override
					protected void refreshSeq() {
						String str="修改序列："+seqName+"序列信息";
						ment.setValue(str);
						hp.updateUI();
					}};
			gcomp.showDesgin(diaLog);
		}};
		
		HButton reName = new HButton("重命名") {@Override
		protected void onClick() {
			String seqName=seq.getValue();
			if(StringUtils.isBlank(seqName)){
				ment.setValue("请输入需要重命名序列名");
				hp.updateUI();
				return;
			}
			String newSeqName= JOptionPane.showInputDialog(null, "请输入新的序列名","重命名", JOptionPane.PLAIN_MESSAGE);
			if(StringUtils.isBlank(newSeqName)) {
				ment.setValue("新的序列名不能为空");
				hp.updateUI();
				return;
			}
			SeqComp gcomp = new SeqComp(type, 
					schema.getValue(), seqName, false) {@Override
					protected void refreshSeq() {
						String str="将序列:"+seqName+" 重命名为："+newSeqName;
						seq.setValue(newSeqName);
						ment.setValue(str);
						hp.updateUI();
						
					}};
			gcomp.reNameSeq(newSeqName);
		}
		
			
		};
		HButton delete = new HButton("删除") {@Override
		protected void onClick() {
			String seqName=seq.getValue();
			if(StringUtils.isBlank(seqName)){
				ment.setValue("请检查是否填入需要删除序列名");
				hp.updateUI();
				return;
			}
			int n = JOptionPane.showConfirmDialog(null,"是否删除？", "删除", JOptionPane.YES_NO_OPTION);
			if(n==0) {
			SeqComp gcomp = new SeqComp(type, 
					schema.getValue(), seqName, false) {@Override
					protected void refreshSeq() {
						String str="删除序列："+seqName;
						ment.setValue(str);
						seq.setValue("");
						hp.updateUI();
					}};
			gcomp.deleteSeq();
			}
		}};
		hp.add(create);
		hp.add(updata);
		hp.add(reName);
		hp.add(delete);
		hp.add(ment);
		frame.setRootPanel(hp);
		frame.show();
		frame.setCloseType(true);
		
	}
	
	public static  WithLabelInput getLabelInput(String label,AbsInput input) {
		HDivLayout hdiv = new HDivLayout(30,0,GridSplitEnum.C3);
		hdiv.setMaxWidth(400);
		HPanel hPane2 = new HPanel(hdiv);
		WithLabelInput winput =new WithLabelInput(hPane2,label,input);
		return winput;
	}
	

}
