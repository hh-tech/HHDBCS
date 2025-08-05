package com.hh.hhdb_admin.comp.sql;

import com.hh.frame.common.base.*;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.dbobj3.explain.ExplainBean;
import com.hh.frame.dbobj3.explain.ExplainUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.parser.AbsStmt;
import com.hh.frame.parser.BaseSplitStmt;
import com.hh.frame.parser.ParserUtil;
import com.hh.frame.parser.PosBean;
import com.hh.frame.sqlwin.PreferSqlWinBean;
import com.hh.frame.sqlwin.SqlWin;
import com.hh.frame.sqlwin.WinMgr;
import com.hh.frame.sqlwin.util.SqlWinUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.ctrl.PopMenuBtn;
import com.hh.frame.swingui.view.data2sql.DataToSqlComp.Type;
import com.hh.frame.swingui.view.fa.FaRegularEnum;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.frame.swingui.view.hmenu.HPopMenu;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SearchSelectBox;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.script.ScriptLoadBar;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.HTAreaUtil;
import com.hh.frame.swingui.view.textarea2.dlg.HTAreaFRDlg;
import com.hh.frame.swingui.view.textarea2.dlg.HTAreaGotoDlg;
import com.hh.frame.swingui.view.textarea2.sql.HSqlArea;
import com.hh.frame.swingui.view.textarea2.sql.SqlAreaConf;
import com.hh.frame.swingui.view.textarea2.sql.SqlConfDlg;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.event.CaretEvent;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Closeable;
import java.io.File;
import java.util.*;

class PkgSqlTopPanel extends LastPanel implements Closeable{
	enum ExeType{
		auto_split, exec_update, query
	}
	
	HSqlArea harea;
	SqlWin sqlWin;
	CheckBoxInput autoCommit;
	SelectBox execType;
	SearchSelectBox schemaBox;
	DBTypeEnum dbtype;
	DBTypeEnum.Version ver;
	HButton runBtn;
	
	private PkgSqlRunJob runJob;
	private HButton commitBtn,rollbackBtn;
	private LabelInput status=new LabelInput();
	private JdbcBean jdbc;
	
	public PkgSqlTopPanel(SqlAreaConf conf, JdbcBean jdbc,PkgSqlRsPanel rsPanel) throws Exception {
		this.jdbc = jdbc;
		this.ver=jdbc.getCurrentVersion();
//		sqlWin = WinMgr.newWin(jdbc, "tmp_query_"+System.currentTimeMillis());
		sqlWin = new SqlWin(jdbc,"tmp_query_"+System.currentTimeMillis(),WinMgr.workDir,false);
		setSqlWinPrefer(conf);
		dbtype=DriverUtil.getDbType(jdbc);
		
		this.harea=new HSqlArea(conf, jdbc) {
			@Override
			protected void caretUpdate(CaretEvent e) {
				int dot=e.getDot();
				String fmt="[%d,%d]";
				try {
					int line=HTAreaUtil.getLineNum(harea.getRSyntaxTextArea(), dot);
					int col=HTAreaUtil.getLineCol(area, dot);
					status.setValue(String.format(fmt, line,col));
				} catch (BadLocationException e1) {
					status.setValue(e1.getMessage());
					Lg.error(getClass(), e1);
				}
			}
			
			@Override
			protected void doConf(Set<Enum<?>> set,HTAreaConf conf) {
				super.doConf(set,conf);
				if(harea!=null) {
					setSqlWinPrefer((SqlAreaConf)conf);
				}
				
			}
		};
		harea.getRSyntaxTextArea().setHighlightCurrentLine(false);
		//添加快捷键
		harea.getRSyntaxTextArea().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == (KeyEvent.VK_F8)) {
					runBtnClick();
				}
			}
		});
		
		//初始化工具栏
		HBarPanel toolBar=new HBarPanel( HBarLayout.newLayout(AlignEnum.LEFT));
		//运行停止按钮
		runJob=new PkgSqlRunJob(jdbc,this,rsPanel);
		runBtn=new HButton(LangMgr.lang(Word2.EXECUTE),FaSolidEnum.PLAY) {
			@Override
			protected void onClick() {
				runBtnClick();
			}
		};
		runBtn.setToolTipText("F8");
		Map<String,String> optionMap=new HashMap<>();
		optionMap.put(ExeType.auto_split.name(), LangMgr.lang(Word4.AUTOMATIC_SPLIT));
		optionMap.put(ExeType.query.name(), LangMgr.lang(Word4.SINGLE_QUERY));
		optionMap.put(ExeType.exec_update.name(), LangMgr.lang(Word4.SINGLE_EXECUTION));
		execType=new SelectBox(optionMap);
		
		toolBar.add(runBtn,execType);
		//判断数据库是否支持提交功能
		if(SqlWinUtil.hasCommitFunction(dbtype)) {
			boolean autoCommitValue=SqlWinUtil.defultAutoCommit(dbtype);
			autoCommit = new CheckBoxInput("auto",LangMgr.lang(Word4.AUTO_COMMIT)) {
				@Override
				protected void onClick(ActionEvent e) {
					try {
						sqlWin.setAutoCommit(autoCommit.isChecked());
					} catch (Exception e1) {
						error(e1);
					}
				}
			};
			autoCommit.setValue(autoCommitValue);
			sqlWin.setAutoCommit(autoCommitValue);
			//提交按钮
			commitBtn = new HButton(LangMgr.lang(Word2.COMMIT),FaSolidEnum.ARROW_UP_FROM_BRACKET) {
				@Override
				protected void onClick() {
					try {
						sqlWin.commit();
						setBtn4Commit();
					} catch (Throwable e) {
						error(e);
					} 
				}
			};
			commitBtn.setEnabled(false);
			//回滚按钮
			rollbackBtn = new HButton(LangMgr.lang(Word2.ROLLBACK),FaSolidEnum.CLOCK_ROTATE_LEFT) {
				@Override
				protected void onClick() {
					try {
						sqlWin.rollback();
						setBtn4Commit();
					} catch (Throwable e) {
						error(e);
					}
				}
			};
			rollbackBtn.setEnabled(false);
			toolBar.add(autoCommit,commitBtn,rollbackBtn);
		}
			
		//模式选择框
		
		String currSchema;
		if(StringUtils.isNoneBlank(jdbc.getSchema())) {
			currSchema=SqlStrUtil.rmQuote4Sel(jdbc.getSchema(), dbtype, ver);
		}else {
			currSchema=SqlWinUtil.getCurrSchema(dbtype, ver, sqlWin.getConn());
			harea.switchSchema(currSchema);
		}
		if (SqlWinUtil.showSchemaBox(dbtype)) {
			schemaBox = new SearchSelectBox() {
				@Override
				protected void onValueChanged(String value) {
					try {
						String schema=SqlStrUtil.addQuote4DDL(getValue(), dbtype, ver);
						ConnUtil.setCurrentSchema(dbtype,ver,sqlWin.getConn(), schema);
						harea.switchSchema(getValue());
					} catch (Throwable e1) {
						error(e1);
					}
				}
			};
			schemaBox.addOption(currSchema,currSchema);
			for (String name : SqlWinUtil.getSchemaNameList(dbtype,jdbc.getCurrentVersion(),sqlWin.getConn())) {
				schemaBox.addOption(name,name);
			}
			schemaBox.setValue(currSchema);
			
			toolBar.add(schemaBox);
		} 
		//查询分析
		HMenuItem quAnalyse=new HMenuItem(LangMgr.lang(Word4.QUERY_ANALYSIS)) {
			@Override
			public void onAction() {
				String selectedSql = getNoneBlankSelectedText();
				if (selectedSql==null) return;
				try {
					ExplainBean bean = ExplainUtil.getExplain(dbtype,ver,sqlWin.getConn(), selectedSql);
					rsPanel.setMessage(selectedSql + "\n" + bean.getText());
				} catch (Throwable e) {
					rsPanel.setMessage(selectedSql + "\n" + e);
				} finally {
					rsPanel.selectPanel(PkgSqlRsPanel.ERRORPANE);
				}
			}
		};
		//语法分析
		HMenuItem sqlAnalyse=new HMenuItem(LangMgr.lang(Word4.SYNTAX_ANALYSIS)) {
			@Override
			public void onAction() {
				String str=getNoneBlankText();
				if(str==null) return;
				List<AbsStmt> stmts = null;
				Map<Enum<?>,Integer> stmtMap = new LinkedHashMap<>();
				if (!StringUtils.endsWith(str.trim(), ";")) str += ";";
				try {
					stmts = ParserUtil.getSplitStmts(dbtype, str);
				} catch (Throwable e1) {
					PopDlgUtil.error(comp, LangMgr.sLang(CsL.SQL_ANALYSIS_FAILURE)+"：\n"+ e1.getMessage());
					return;
				}
				for (AbsStmt stmt : stmts) {
					String err=ParserUtil.checkSql((BaseSplitStmt)stmt);
					if(err!=null) {
						PopDlgUtil.error(comp, LangMgr.sLang(CsL.SQL_ANALYSIS_FAILURE)+"：\n"+ err);
						return;
					}
					if (stmtMap.containsKey(stmt.getType())) {
						stmtMap.put(stmt.getType(),stmtMap.get(stmt.getType())+1);
					} else {
						stmtMap.put(stmt.getType(),1);
					}
				}
				StringBuffer sb = new StringBuffer();
				for (Map.Entry<Enum<?>, Integer> entry : stmtMap.entrySet()) {
					sb.append(entry.getKey()).append(" "+entry.getValue()+" ").append(LangMgr.lang(Word1.PARAGRAPH)+"SQL\n");
				}
				PopDlgUtil.info(comp, LangMgr.sLang(CsL.SQL_ANALYSIS_SUCCESS)+":\t"+ sb);
			}
		};
		HPopMenu toolMenu =new HPopMenu();
		toolMenu.addItem(quAnalyse,sqlAnalyse);
		PopMenuBtn analyseBtn = new PopMenuBtn(LangMgr.lang(Word2.ANALYSE), toolMenu);
		analyseBtn.setIcon(FaSolidEnum.CALENDAR_CHECK,16);
		// 格式化SQL
		HButton formatBut = new HButton(LangMgr.lang(Word2.BEAUTIFY),FaSolidEnum.WAND_MAGIC_SPARKLES) {
			@Override
			public void onClick() {
				if(getNoneBlankText()!=null) GlobalWorkUtil.formatSql(harea);
			}
		};
		//保存
		HButton saveBut = new HButton(LangMgr.lang(Word2.SAVE),FaSolidEnum.FLOPPY_DISK) {
			@Override
			public void onClick() {
				if(getNoneBlankText() != null) GlobalWorkUtil.saveText(harea.getText());
			}
		};
		//加载
		HPopMenu menu = new HPopMenu();
		HMenuItem scriptItem = new HMenuItem( "SQL" + LangMgr.lang(Word2.SCRIPT)) {
			@Override
			protected void onAction() {
				try {
					FileEncodingBean fb=GlobalWorkUtil.getTextFile();
					if(fb==null) return;
					try {
						harea.loadFile(fb.getFile(), fb.getCharset(),true);
					} catch (Throwable e) {
						PopDlgUtil.logErr(getClass(), harea.getComp(), e);
					}
				} catch (Exception e) {
					Lg.error(getClass(), e);
					PopDlgUtil.error(e);
				}
			}
		};
		
		HMenuItem excelItem = new HMenuItem("Excel"+LangMgr.lang(Word2.FILE)) {
			@Override
			protected void onAction() {
				GlobalWorkUtil.showDataToSqlDialog(harea,Type.excel_file);
			}
		};
		
		HMenuItem csvItem = new HMenuItem("CSV"+LangMgr.lang(Word2.FILE)) {
			@Override
			protected void onAction() {
				GlobalWorkUtil.showDataToSqlDialog(harea,Type.csv_file);
			}
		};
		HMenuItem textItem = new HMenuItem("分隔符文本") {
			@Override
			protected void onAction() {
				GlobalWorkUtil.showDataToSqlDialog(harea,Type.text);
			}
		};
		menu.addItem(scriptItem,excelItem,csvItem,textItem);
		PopMenuBtn loadBut = new PopMenuBtn(LangMgr.lang(Word2.LOAD), menu);
		loadBut.setIcon(FaSolidEnum.FILE_IMPORT, 16);

		HButton config = new HButton(LangMgr.lang(Word2.SETTINGS),FaSolidEnum.SCREWDRIVER_WRENCH) {
			@Override
			protected void onClick() {
				harea.showCfgDlg();
			}
		};
		HButton helpBtn = new HButton(LangMgr.lang(Word2.HELP), FaRegularEnum.CIRCLE_QUESTION) {
			@Override
			protected void onClick() {
				HDialog helpDialog = new HDialog(700, 400);
				HPanel panel = new HPanel();
				HTable tab = new HTable();
				tab.hideSeqCol();
				tab.setShowVerLine(false);
				tab.setShowHorLine(false);
				DataCol keyCol1 = new DataCol("key", LangMgr.lang(Word3.SHORTCUT_KEYS));
				keyCol1.setWidth(150);
				keyCol1.setMaxWidth(150);
				DataCol describeCol1 = new DataCol("describe", LangMgr.lang(Word2.DESCRIPTION));
				tab.addCols(keyCol1,describeCol1);
				LastPanel lastPanel = new LastPanel(false);
				lastPanel.setWithScroll(tab.getComp());
				List<Map<String, String>> data = new ArrayList<>();
				addHelp(data,"F8",LangMgr.sLang(CsL.RUN_SQL));
				addHelp(data,"TAB",LangMgr.sLang(CsL.SELECTED_TEXT_FORWARD));
				addHelp(data,"SHIFT+Tab",LangMgr.sLang(CsL.SELECT_TEXT_TO_MOVE_BACKWARDS));
				addHelp(data,"CTRL+f",LangMgr.sLang(CsL.FIND_REPLACE_PANEL));
				addHelp(data,"CTRL+g",LangMgr.sLang(CsL.POSITIONING_PANEL));
				addHelp(data,LangMgr.sLang(CsL.MOUSE_WHEEL),LangMgr.sLang(CsL.BODY_SIZE_ADJUSTMENT));
				addHelp(data,"CTRL+/",LangMgr.sLang(CsL.COMMENT_CODE));
				addHelp(data,"CTRL+z",LangMgr.lang(Word2.REVOKE));
				addHelp(data,"CTRL+y",LangMgr.sLang(CsL.RESCIND));
				addHelp(data,"CTRL+c",LangMgr.lang(Word2.COPY));
				addHelp(data,"CTRL+v",LangMgr.lang(Word2.PASTE));
				addHelp(data,"CTRL+x",LangMgr.lang(Word2.CUT));
				addHelp(data,"CTRL+a",LangMgr.lang(Word2.SELECT_ALL));
				addHelp(data,"DELETE",LangMgr.lang(Word2.DELETE));
				tab.load(data, 1);
				panel.setLastPanel(lastPanel);
				helpDialog.setRootPanel(panel);
				helpDialog.setIconImage(IconFileUtil.getLogo());
				helpDialog.setWindowTitle(LangMgr.lang(Word2.HELP));
				helpDialog.removeCancleBtn();
				helpDialog.show();
			}
			private  void addHelp(List<Map<String, String>> list,String key,String name){
				Map<String, String> map = new HashMap<>();
				map.put("key",key);
				map.put("describe",name);
				list.add(map);
			}
		};
		toolBar.add(analyseBtn,formatBut,saveBut,loadBut,config,helpBtn);
		set(harea.getComp());
		setHead(toolBar.getComp());
		HBarPanel footBar=new HBarPanel();
		footBar.add(status);
		
		
		ScriptLoadBar loadBar = new ScriptLoadBar() {
			@Override
			public void sendMessage(String string) {
				harea.insert2Caret(string);
			}
		};
		
		String path = jdbc.getJobj().getString(JdbcInfo.SCRIPT_PATH, "");
		if(!StringUtils.isBlank(path)) {
			DBTypeEnum type = DriverUtil.getDbType(jdbc);
			File dir = new File(path);
			if(dir.exists()&&dir.isDirectory()) {
				loadBar.loadDir(dir);
				File f = new File(path,type.name().toLowerCase());
				if(f.exists()&&f.isDirectory()) {
					loadBar.loadDir(f);
				}
			}
			
			
			
		}
		footBar.setLastHComp(loadBar);
		setFoot(footBar.getComp());
	}
	
	public void setCfgDlg(SqlConfDlg dlg) {
		harea.setCfgDlg(dlg);
	}
	
	public void setFRDlg(HTAreaFRDlg dlg) {
		harea.setFRDlg(dlg);
	}
	
	public void setGotoDlg(HTAreaGotoDlg dlg) {
		harea.setGotoDlg(dlg);
	}
	
	public void setBtn4runing(boolean isRunning) {
		execType.setEnabled(!isRunning);
		if (null != autoCommit) autoCommit.setEnabled(!isRunning);
		if (null != schemaBox) schemaBox.setEnabled(!isRunning);
		if(isRunning) {
			runBtn.setText(LangMgr.lang(Word2.STOP));
			runBtn.setIcon(FaSolidEnum.STOP,16);
		}else {
			runBtn.setText(LangMgr.lang(Word2.EXECUTE));
			runBtn.setIcon(FaSolidEnum.PLAY,16);
		}
	}
	
	public void setBtn4Commit() {
		try {
			if(!SqlWinUtil.hasCommitFunction(dbtype)) return;
			if(autoCommit.isChecked()) {
				commitBtn.setEnabled(false);
				rollbackBtn.setEnabled(false);
			}else {
				boolean hasCommit=sqlWin.hasCommit();
				commitBtn.setEnabled(hasCommit);
				rollbackBtn.setEnabled(hasCommit);
			}
		} catch (Throwable e) {
			error(e);
		}
	}
	
	public void runBtnClick(){
		if(runJob.getStatus()!=JobStatus.RUNNING) {
			if (!StringUtils.isNotBlank(getNoneBlankText())) return;
			if(schemaBox!=null) jdbc.setSchema(schemaBox.getValue());
			runJob.setJdbc(jdbc);
			new Thread(runJob).start();
		}else {
			try {
				sqlWin.cancel();
			} catch (Throwable e) {
				error(e);
			}
		}
	}
	
	public PosBean getSelectPos() throws BadLocationException {
		RSyntaxTextArea ra=harea.getRSyntaxTextArea();
		int end=ra.getSelectionEnd();
		//文档为空
		if(end==0) {
			return null;
		}
		int start=ra.getSelectionStart();
		String selectText= harea.getSelectedText();

		PosBean pos=new PosBean();
		if(StringUtils.isBlank(selectText)) {
			String fullText=harea.getText();
			if(StringUtils.isBlank(fullText))
				return null;
			else {
				pos.setBeginLine(1);
				pos.setBeginColumn(1);
				pos.setEndLine(ra.getLineCount());
				pos.setEndColumn(ra.getLineEndOffset(pos.getEndLine()-1));
			}
		}else {
			pos.setBeginLine(HTAreaUtil.getLineNum(ra, start));
			pos.setBeginColumn(HTAreaUtil.getLineCol(ra, start));
			pos.setEndColumn(HTAreaUtil.getLineCol(ra, end));
			pos.setEndLine(HTAreaUtil.getLineNum(ra, end));
		}
		return pos;
	}
	
	@Override
	public void close() {
		try {
			sqlWin.close();
		} catch (Throwable e) {
			error(e);
		}
		IOUtils.closeQuietly(harea);
	}
	
	public HSqlArea getHarea() {
		return harea;
	}
	
	
	private void error(Throwable e) {
		PopDlgUtil.logErr(getClass(), harea.getComp(), e);
	}
	
	private String getNoneBlankSelectedText() {
		String txt = harea.getSelectedText();
		if(StringUtils.isBlank(txt)) {
			PopDlgUtil.error(LangMgr.sLang(CsL.TEXT_FIELD_SELECT_TEXT_NULL));
			return null;
		}
		return txt;
	}
	private String getNoneBlankText() {
		String txt=harea.getSelectedText();
		if(txt!=null) 
			return txt;
		txt = harea.getText();
		if(StringUtils.isBlank(txt)) {
			PopDlgUtil.error(LangMgr.sLang(CsL.THE_TEXT_FIELD_EMPTY));
			return null;
		}
		return txt;
	}
	
	private void setSqlWinPrefer(SqlAreaConf sqlConf) {
		PreferSqlWinBean pwb = new PreferSqlWinBean();
		pwb.setCmdExceptionExit(sqlConf.isErrBreak());
		pwb.setNullFalg(sqlConf.getNullValue());
		pwb.setNumPerPage(sqlConf.getPageSize());
		Lg.debug(PkgSqlTopPanel.class, pwb.toString());
		sqlWin.setPrefer(pwb);
	}
	
	public void reconnect() throws Exception {
		sqlWin = WinMgr.newWin(jdbc, "tmp_query_"+System.currentTimeMillis());
	}
}
