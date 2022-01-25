package com.hh.hhdb_admin.mgr.cmd;

import com.hh.frame.chardet.ChardetUtil;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.dbcmd.AbsCmdUIServ;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.util.ClipboardUtil;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.mgr.cmd.ui.CmdText;
import com.hh.hhdb_admin.mgr.cmd.ui.CmdToolBar;
import org.apache.commons.io.FileUtils;
import java.io.*;
import java.util.List;

public class CmdComp extends AbsHComp {
	private JdbcBean jdbc;
	private CmdText cmdtext;
	private CmdToolBar toolbar;
	private LastPanel lastPanel = new LastPanel(false);
	private AbsCmdUIServ cmdServ = null;

	public CmdComp(JdbcBean jdbc) {
		this.jdbc = jdbc;
		this.comp = lastPanel.getComp();
		cmdtext = new CmdText(this) {
			@Override
			public void send(String sql) {
				cmdServ.runsql(sql);
			}

			@Override
			public void up() {
				cmdServ.up();
			}

			@Override
			public void down() {
				cmdServ.down();
			}

			@Override
			public void copy() {
				textCopy();
			}

			@Override
			public void paste() {
				textPaste();
			}

			@Override
			public void keyCancel() {
				cmdServ.cancel();
			}
		};
		toolbar = new CmdToolBar(this);
		lastPanel.set(cmdtext.getScrollPane());
		lastPanel.setHead(toolbar.gethTool().getComp());
		try {
			cmdServ = new AbsCmdUIServ(jdbc) {
				@Override
				public void sendTop2TextPanel(String top) {
					cmdtext.recvTop(top);
				}

				@Override
				public void sendStr2TextPanel(String str) {
					cmdtext.recv(str);
				}

				@Override
				public void run_start() {
					cmdtext.setEditable(false);
					toolbar.setCancelBtnEnab(true);
				}

				@Override
				public void run_end() {
					cmdtext.setEditable(true);
					toolbar.setCancelBtnEnab(false);
				}

				@Override
				public void replaceSql(String newSql) {
					cmdtext.replaceSql(newSql);
				}

				@Override
				public List<String> getFileLines(String filePath) throws Exception {
					File sqlfile = new File(filePath);
					if (sqlfile.exists()) {
						return FileUtils.readLines(sqlfile, ChardetUtil.detectCharset(sqlfile));
					} else {
						throw new Exception(CmdMgr.getLang("fileexist"));
					}
				}

				@Override
				public String getInputValue(String name) {
					return PopPaneUtil.input("请输入："+name);
				}

				@Override
				public void clearUi() {
					// TODO Auto-generated method stub					
				}
			};
			cmdtext.recvTop(cmdServ.getTopStr());
		} catch (Exception e) {
			cmdtext.recv(e.toString());
			cmdtext.setEditable(false);
		}
	}

	public void cancel() {
		cmdServ.cancel();
	}

	public void close() {
		if (null != cmdServ) cmdServ.close();
	}

	/**
	 * 复制
	 */
	public void textCopy() {
		ClipboardUtil.putText(cmdtext.getTextArea().getSelectedText());
	}

	/**
	 * 粘贴
	 */
	public void textPaste() {
		cmdtext.tip.setDisable(true);
		try {
			if (ClipboardUtil.isSupport(ClipboardUtil.ContentType.STRING)) {
				String cli_str = ClipboardUtil.getText();
				if (cli_str == null) {
					return;
				}
				cmdServ.Paste(cli_str);
			}
		} catch (Exception e1) {
			// 获取不到粘贴板中的内容，不做任何粘贴处理
		}finally {
			cmdtext.tip.setDisable(false);
		}
	}

	/**
	 * 打开文件
	 * 
	 * @param filePath
	 */
	public void openFile(String filePath) {
		File sqlfile = new File(filePath);
		if (sqlfile.exists()) {
			try {
				List<String> lines = FileUtils.readLines(sqlfile, ChardetUtil.detectCharset(sqlfile));
				cmdServ.impLines(lines);
			} catch (IOException e) {
				cmdtext.recv("\n" + e.toString());
				cmdtext.recvTop("\n" + cmdServ.getTopStr());
				e.printStackTrace();
			}
		} else {
			cmdtext.recv("\n" + CmdMgr.getLang("fileexist"));
			cmdtext.recvTop("\n" + cmdServ.getTopStr());
		}
	}

	/**
	 * 切换模式
	 */
	public void updaSchema(String schema) {
//		try {
//			jdbc.setSchema(schema);
//			jdbc.setCurSessionSchema(DbCmdStrUtil.toDbCmdStr(schema,DriverUtil.getDbType(jdbc)));
//			ConnUtil.setCurrentSchema(conn,jdbc.getCurSessionSchema() );
//            //设置关键词
//            cmdtext.setKeyWord();
//		} catch (Exception e) {
//			cmdtext.recv(e.toString(), false);
//			cmdtext.setEditable(false);
//		}
	}

	/**
	 * 获取编辑器内容
	 * 
	 * @return
	 */
	public String getText() {
		return cmdtext.getTextArea().getText();
	}

	/**
	 * 设置查询参数
	 * 
	 * @param row
	 * @param nullSigns
	 */
	public void setPars(int row, String nullSigns) {
		cmdServ.setPars(row, nullSigns);
	}

	public JdbcBean getJdbc() {
		return jdbc;
	}
}
