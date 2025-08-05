package com.hh.hhdb_admin.comp.cmd;

import java.awt.event.ActionEvent;
import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.FileEncodingBean;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.dbcmd.DbCmdTool;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.sqlwin.util.SqlWinUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.ctrl.PopMenuBtn;
import com.hh.frame.swingui.view.data2sql.DataToSqlComp.Type;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.frame.swingui.view.hmenu.HPopMenu;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.SearchSelectBox;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.sql.HCmdSqlArea;
import com.hh.frame.swingui.view.textarea2.sql.SqlAreaConf;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;


class PkgSqlCmdPanel extends LastPanel implements Closeable {
	private JdbcBean jdbc;
	protected HCmdSqlArea harea;
	private HButton cancelBtn;
	private HButton commitBtn;
	private HButton rollbackBtn;
	private CheckBoxInput autoCommit = null;
	private SearchSelectBox schemaBox = null;
	
	private DBTypeEnum dbtype;

	public PkgSqlCmdPanel(SqlAreaConf conf, JdbcBean jdbc) throws Exception {
		super(false);
		this.jdbc = jdbc;
		this.dbtype=DriverUtil.getDbType(jdbc);
		
		harea = new HCmdSqlArea(conf, jdbc) {
			@Override
			protected void beginExec() {
				cancelBtn.setEnabled(true);
			}

			@Override
			protected void endExec() {
				setCommitStatus();
			}
		};
		
		set(harea.getComp());
		setHead(initBar().getComp());
	}

	private HBarPanel initBar()throws Exception {
		HBarLayout layout = new HBarLayout();
		layout.setAlign(AlignEnum.LEFT);
		HBarPanel bar = new HBarPanel(layout);
		//取消按钮
		
		cancelBtn = new HButton(LangMgr.lang(Word2.CANCEL)) {
			@Override
			protected void onClick() {
				harea.cancel();
				if(!harea.isRunning()) {
					cancelBtn.setEnabled(false);
				}
				
			}
		};
		cancelBtn.setIcon(FaSolidEnum.REPLY, 16);
		cancelBtn.setEnabled(false);
		bar.add(cancelBtn);
		//判断数据库是否支持提交功能
		if(SqlWinUtil.hasCommitFunction(dbtype)) {
			
			commitBtn = new HButton(LangMgr.lang(Word2.COMMIT),FaSolidEnum.ARROW_UP_FROM_BRACKET) {
				@Override
				protected void onClick() {
					try {
						harea.commit();
						setCommitStatus();
					} catch (SQLException e) {
						error(e);
					}
				}
			};
			commitBtn.setEnabled(false);
			rollbackBtn = new HButton(LangMgr.lang(Word2.ROLLBACK),FaSolidEnum.CLOCK_ROTATE_LEFT) {
				@Override
				protected void onClick() {
					try {
						harea.rollback();
						setCommitStatus();
					} catch (SQLException e) {
						error(e);
					}
				}
			};
			rollbackBtn.setEnabled(false);
			autoCommit = new CheckBoxInput("auto", LangMgr.lang(Word4.AUTO_COMMIT)) {
				@Override
				protected void onClick(ActionEvent e) {
					try {
						harea.setAutoCommit(autoCommit.isChecked());
					} catch (Exception e1) {
						error(e1);
					}
				}
			};
			autoCommit.setValue(SqlWinUtil.defultAutoCommit((DriverUtil.getDbType(jdbc))));
			harea.setAutoCommit(autoCommit.isChecked());
			bar.add(commitBtn,rollbackBtn,autoCommit);
		}
		//模式选择框
		if (SqlWinUtil.showSchemaBox(dbtype)) {
			schemaBox = new SearchSelectBox() {
				@Override
				protected void onValueChanged(String value) {
					try {
						harea.switchSchema(schemaBox.getValue());
					} catch (Exception e1) {
						error(e1);
					}
				}
			};
			schemaBox.addOption(harea.getCurrentSchema(),harea.getCurrentSchema());
			for (String name : harea.getSchemaNames()) {
				schemaBox.addOption(name, name);
			}
			schemaBox.setValue(harea.getCurrentSchema());
			
			bar.add(schemaBox);
		}
		
		HButton config = new HButton(LangMgr.lang(Word2.SETTINGS),FaSolidEnum.SCREWDRIVER_WRENCH) {
			@Override
			protected void onClick() {
				harea.showCfgDlg();
				
			}
		};
		HButton exp = new HButton(LangMgr.lang(Word2.SAVE),FaSolidEnum.FILE_EXPORT) {
			@Override
			protected void onClick() {
				GlobalWorkUtil.saveText(harea.getText());
			}
		};
		
		HPopMenu menu = new HPopMenu();
		HMenuItem scriptItem = new HMenuItem("SQL"+LangMgr.lang(Word2.SCRIPT)) {
			@Override
			protected void onAction() {
				try {
					FileEncodingBean fb=GlobalWorkUtil.getTextFile();
					if(fb==null) return;
					harea.doCmd(fb.getFile(),fb.getCharset());
				} catch (Exception e) {
					Lg.error(getClass(), e);
					PopDlgUtil.error(e);
				}
			}
		};
		
		
		HMenuItem excelItem = new HMenuItem("EXCEL"+LangMgr.lang(Word2.FILE)) {
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
		PopMenuBtn loadBtn = new PopMenuBtn(LangMgr.lang(Word2.LOAD), menu);
		loadBtn.setIcon(FaSolidEnum.FILE_IMPORT, 16);
		bar.add(config,loadBtn, exp );
		return bar;
	}

	@Override
	public void close() throws IOException {
		harea.close();
	}
	

	
	
	
	private void error(Exception e) {
		PopDlgUtil.error(harea.getComp(),e);
		Lg.error(getClass(), e);
		DbCmdTool dbtool = harea.getDbCmdTool();
		
		try {
			if (dbtool.getConn().isClosed()) {
				if (PopDlgUtil.confirm(harea.getComp(), "尝试重新连接数据库？")) {
					try {
						dbtool.reconnect();
					} catch (Exception ex) {
						PopDlgUtil.error(harea.getComp(), ex);
						Lg.error(getClass(), ex);
					}
				}
			}
		} catch (Exception exception) {
			PopDlgUtil.error(harea.getComp(),exception);
			Lg.error(getClass(), exception);
		}
	}
	
	private void setCommitStatus() {
		try {
			cancelBtn.setEnabled(false);
			if(SqlWinUtil.hasCommitFunction(dbtype)) {
				boolean auto = harea.getAutoCommit();
				if (!auto) {
					boolean hasCommit = harea.hasCommit();
					commitBtn.setEnabled(hasCommit);
					rollbackBtn.setEnabled(hasCommit);
				} else {
					commitBtn.setEnabled(false);
					rollbackBtn.setEnabled(false);
				}
				
				autoCommit.setValue(auto);
			}
		} catch (SQLException e) {
			error(e);
		}
	}
}
