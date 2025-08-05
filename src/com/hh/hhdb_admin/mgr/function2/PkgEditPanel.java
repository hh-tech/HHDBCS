package com.hh.hhdb_admin.mgr.function2;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.db_sql.ProcFunSqlUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.hhdb_admin.common.CompileTool;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import org.apache.commons.lang3.StringUtils;

class PkgEditPanel extends LastPanel{
	private HTextArea area;
	private HBarPanel barPanel;
	private HButton saveButton;
	private HButton formatButton;
	private HButton previousButton;
	private HButton compileButton;
	private String oldText;
	private CompileTool tool;
	
	private String name;	//修改函数的名称
	
	public PkgEditPanel(String name){
		this.name = name;
		HBarLayout layout = new HBarLayout();
		layout.setTopHeight(10);
		layout.setBottomHeight(10);
		layout.setAlign(AlignEnum.LEFT);
		barPanel = new HBarPanel(layout);
		
		HTAreaConf config = new HTAreaConf();
		config.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
		
		initButton();
		area = new HTextArea(config);
		area.getRSyntaxTextArea().addCaretListener(e -> {
			saveButton.setEnabled(!oldText.equals(area.getText()));
            if (null != compileButton) compileButton.setEnabled(oldText.equals(area.getText()));
        });
		setHead(barPanel.getComp());
		set(area.getComp());
	}
	
	
	public void init(String Text,boolean isAdd) {
		barPanel.removeAll();
		
		if(isAdd) {
			oldText="";
			barPanel.add(previousButton);
			barPanel.add(saveButton);
			barPanel.add(formatButton);
			saveButton.setEnabled(true);
		}else {
			oldText = Text;
			if(ProcFunSqlUtil.isSupportCompile(CsVarUtil.getDbtype(), CsVarUtil.jdbc.getCurrentVersion())){
				barPanel.add(compileButton);
			}
			
			barPanel.add(saveButton);
			barPanel.add(formatButton);
			saveButton.setEnabled(false);
		}
		
		area.setText(Text);
	}
	
	
	
	public void setTool(CompileTool tool) {
		this.tool = tool;
	}


	private void initButton() {
		saveButton = new HButton(LangMgr.lang(Word2.SAVE),FaSolidEnum.FLOPPY_DISK) {
			@Override
			protected void onClick() {
				if(StringUtils.isBlank(area.getText())) {
					PopDlgUtil.error(getComp(), "语句为空");
					return;
				}
				
				//mysql修改函数需要先删除再保存，由于不能通过事物控制，所以先试着创建一个临时函数判断能否创建成功，然后再执行原本的修改
				if (StringUtils.isNotBlank(name) && (CsVarUtil.getDbtype() == DBTypeEnum.mysql || CsVarUtil.getDbtype() == DBTypeEnum.gbase8a)) {
					String s = CsVarUtil.getDbtype() == DBTypeEnum.mysql ? "`":"\"";
					String str = area.getText().replace(s + ""+name+"" + s,s + "_hhdbcs_Temp_Stored_Proc_" + name + "" + s);
					StringBuffer sb = new StringBuffer();
					sb.append(str.substring(str.indexOf(";")+1).trim()).append(";\n");
					sb.append(str.substring(0,str.indexOf(";")).trim());
					
					if(!GlobalWorkUtil.exeSql(CsVarUtil.jdbc, CsVarUtil.conn, sb.toString())) {
						return;
					}
				}
				
				if(GlobalWorkUtil.exeSql(CsVarUtil.jdbc, CsVarUtil.conn, area.getText())) {
					saveButton.setEnabled(false);
					compileButton.setEnabled(true);
					inform();
				}
			}
		};
		
		
		formatButton = new HButton(LangMgr.lang(Word3.FORMAT),FaSolidEnum.WAND_MAGIC_SPARKLES) {
			@Override
			protected void onClick() {
				GlobalWorkUtil.formatSql(area);
			}
		};
		
		
		previousButton = new HButton(LangMgr.lang(Word3.LAST_STEP),FaSolidEnum.LEFT_LONG) {
			@Override
			protected void onClick() {
				doPrev();
			}
		};
		
		compileButton = new HButton(LangMgr.lang(Word2.COMPILE),FaSolidEnum.RECEIPT) {
			protected void onClick() {
				doCompile();
			};
		};
	}
	
	
    
    private void doCompile() {
    	try {
			if(tool.compile()||StringUtils.isBlank(tool.getErrorMsg())) {
				PopDlgUtil.info(getComp(), LangMgr.lang(Word4.COMPILE_SUCCESS));
				return;
			};
			
			PopDlgUtil.error(getComp(),tool.getErrorMsg());
		
		} catch (Exception e) {
			Lg.error(getClass(), e);
			PopDlgUtil.error(getComp(), e);
		}
    }
    
	protected void doPrev(){
		
	}
	
	protected void inform() {
		
	}
	
	@Override
	public int getHeight() {
		return barPanel.getHeight() + 600;
	}
	
}
