package com.hh.hhdb_admin.comp.cmd;

import java.io.Closeable;
import java.io.IOException;

import javax.swing.text.BadLocationException;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.FileEncodingBean;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.ctrl.PopMenuBtn;
import com.hh.frame.swingui.view.data2sql.DataToSqlComp.Type;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.frame.swingui.view.hmenu.HPopMenu;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.sql.HSqlArea;
import com.hh.frame.swingui.view.textarea2.sql.SqlAreaConf;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;

class PkgCmdScriptPanel extends LastPanel implements Closeable{
	private HBarPanel bottomBar;
	private HSqlArea codeArea;

	public PkgCmdScriptPanel(PkgSqlCmdPanel scp,JdbcBean jdbc)throws Exception {
		SqlAreaConf conf = new SqlAreaConf();
		conf.setAutoPop(true);
		this.codeArea = new HSqlArea(conf,jdbc);
		this.codeArea.getRSyntaxTextArea().setRows(10);
		codeArea.setFRDlg(GlobalWorkUtil.getFrDlg());
		codeArea.setGotoDlg(GlobalWorkUtil.getAreaGotoDlg());

		HBarLayout layout = new HBarLayout();
		layout.setxGap(15);
		layout.setTopHeight(10);
		layout.setBottomHeight(10);
		bottomBar = new HBarPanel(layout);
		
		HButton sendBtn = new HButton(LangMgr.lang(Word2.SEND)) {
			@Override
			protected void onClick() {
				
				String text = codeArea.getSelectedText();
				if(text==null) {
					text=codeArea.getText();
				}
				if(StringUtils.isBlank(text))return;
				try {
					scp.harea.doCmd(text);
				} catch (BadLocationException e) {
					Lg.error(PkgCmdScriptPanel.class, e);
					PopDlgUtil.error(e);
				}
			}
		};
		
		
		HPopMenu menu = new HPopMenu();
		HMenuItem scriptItem = new HMenuItem("SQL" +LangMgr.lang(Word2.SCRIPT)) {
			@Override
			protected void onAction() {
				try {
					FileEncodingBean fb=GlobalWorkUtil.getTextFile();
					if(fb==null)return;
					codeArea.loadFile(fb.getFile(), fb.getCharset() , true);
				} catch (Exception e) {
					Lg.error(PkgCmdScriptPanel.class, e);
					PopDlgUtil.error(e);
				}
			}
		};
		
		HMenuItem excelItem = new HMenuItem("EXCEL"+LangMgr.lang(Word2.FILE)) {
			@Override
			protected void onAction() {
				GlobalWorkUtil.showDataToSqlDialog(codeArea,Type.excel_file);
			}
		};
		
		HMenuItem csvItem = new HMenuItem("CSV"+LangMgr.lang(Word2.FILE)) {
			@Override
			protected void onAction() {
				GlobalWorkUtil.showDataToSqlDialog(codeArea,Type.csv_file);
			}
		};
		HMenuItem textItem = new HMenuItem("分隔符文本") {
			@Override
			protected void onAction() {
				GlobalWorkUtil.showDataToSqlDialog(codeArea,Type.text);
			}
		};
		menu.addItem(scriptItem,excelItem,csvItem,textItem);
		PopMenuBtn loadBtn = new PopMenuBtn(LangMgr.lang(Word2.LOAD), menu);
		loadBtn.setIcon(FaSolidEnum.FILE_IMPORT, 16);
		
//		HButton loadBtn = new HButton(LangMgr.lang(Word2.LOAD)) {
//			@Override
//			protected void onClick() {
//				try {
//					FileEncodingBean fb=GlobalWorkUtil.getTextFile();
//					if(fb==null)return;
//					String text= FileUtils.readFileToString(fb.getFile(), fb.getCharset());
//					codeArea.setText(text);
//				} catch (Exception e) {
//					Lg.error(PkgCmdScriptPanel.class, e);
//					PopDlgUtil.error(e);
//				}
//			}
//		};

		
		HButton saveBtn = new HButton(LangMgr.lang(Word2.SAVE)) {
			@Override
			protected void onClick() {
				String text = codeArea.getText();
				GlobalWorkUtil.saveText(text);
			}
		};
		sendBtn.setIcon(FaSolidEnum.ARROW_UP,16);
		saveBtn.setIcon(FaSolidEnum.FILE_EXPORT,16);
		loadBtn.setIcon(FaSolidEnum.FILE_IMPORT,16);
		bottomBar.add(sendBtn,loadBtn, saveBtn);
		set(codeArea.getComp());
		setFoot(bottomBar.getComp());
	}
	
	public void setScriptBar(HBarPanel bar) {
		setHead(bar.getComp());
	}

	public String getCmdString() {
		String text = codeArea.getSelectedText();
		if (text == null || text.length() == 0) {
			text = codeArea.getText();
		}

		return text;
	}
	public HTextArea getCodeArea() {
		return codeArea;
	}

	@Override
	public void close() throws IOException {
		codeArea.close();
	}
	
}
