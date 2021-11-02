package com.hh.hhdb_admin.mgr.cmd;

import com.hh.frame.chardet.ChardetUtil;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.dbcmd.DbCmdTool;
import com.hh.frame.dbcmd.DbCmdType;
import com.hh.frame.dbcmd.SqlRunTool;
import com.hh.frame.parser.ParserUtil;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.util.ClipboardUtil;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;
import com.hh.hhdb_admin.mgr.cmd.ui.CmdText;
import com.hh.hhdb_admin.mgr.cmd.ui.CmdToolBar;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CmdComp extends AbsHComp {
	private static final String topStr = "SQL>";
	
	private JdbcBean jdbc;
	private CmdText cmdtext;
	private SqlRunTool sqlRunTool;
	private DbCmdTool cmdTool;
	private DBTypeEnum dbType;
	private CmdToolBar toolbar;
	private LastPanel lastPanel = new LastPanel(false);
	
	private Connection conn;
	private List<String> historySql = new ArrayList<String>();
	private int hisIndex = 0;
	private boolean isCancel = false;

	public CmdComp(JdbcBean jdbc) {
		this.jdbc = jdbc;
		this.comp = lastPanel.getComp();
		try {
			dbType = DriverUtil.getDbType(jdbc);
			cmdTool = new DbCmdTool(dbType);
			cmdtext = new CmdText(this) {
				@Override
				public void send(String sql) {
					runsql(sql);
				}
				@Override
				public void up() {
					if (hisIndex > 0) replaceSql(historySql.get(--hisIndex));
				}
				@Override
				public void down() {
					if (hisIndex < (historySql.size() - 1) && historySql.size() > 0) {
						replaceSql(historySql.get(++hisIndex));
					} else {
						if (hisIndex < historySql.size()) hisIndex++;
						replaceSql("");
					}
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
					cancel();
				}
			};
			
			toolbar = new CmdToolBar(this);
			
			conn = ConnUtil.getConn(jdbc);
			sqlRunTool = new SqlRunTool(conn);
			setSqlRunTool(toolbar.rowsum,toolbar.nullSign);
			
			lastPanel.set(cmdtext.getScrollPane());
			lastPanel.setHead(toolbar.gethTool().getComp());
		} catch (Exception e) {
			cmdtext.recv(e.toString(), false);
			cmdtext.setEditable(false);
			return;
		}
		cmdtext.recvTop(topStr, false);
	}
	
	public void close() {
		ConnUtil.close(conn);
	}
	
	/**
	 * 撤销
	 */
	public void cancel() {
		try {
			isCancel = true;
			sqlRunTool.cancle();
			toolbar.setCancelBtnEnab(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
		if (ClipboardUtil.isSupport(ClipboardUtil.ContentType.STRING)) {
			try {
				String cli_str = ClipboardUtil.getText();
				
				if (cli_str == null) {
					return;
				}
				new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                                new ByteArrayInputStream(cli_str.getBytes(StandardCharsets.UTF_8)),
                                StandardCharsets.UTF_8))) {
                            String line1 = br.readLine();
                            String line2 = br.readLine();
                            if (line2 != null) {
                                cmdtext.setEditable(false);
                                toolbar.setCancelBtnEnab(true);
                                isCancel = false;
                                cmdtext.recvTop(topStr, true);
            
                                if (!isCancel) {
                                    cmdtext.recv(line1, false);
                                    cmdTool.put(line1);
                                    runOneSql();
                                }
            
                                if (!isCancel) {
                                    cmdtext.recv(line2, false);
                                    cmdTool.put(line2);
                                    runOneSql();
                                }
            
                                String line;
                                while ((line = br.readLine()) != null) {
                                    if (isCancel) {
                                        break;
                                    }
                                    cmdtext.recv(line, false);
                                    cmdTool.put(line);
                                    runOneSql();
                                }
                            } else {
                                cmdtext.recv(cli_str, false);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // 流关闭异常，不做处理
                        cmdtext.setEditable(true);
                        toolbar.setCancelBtnEnab(false);
                    }
                }).start();
			} catch (Exception e1) {
				// 获取不到粘贴板中的内容，不做任何粘贴处理
			}
		}
	}
	/**
	 * 打开文件
	 * @param filePath
	 */
	public void openFile(String filePath) {
		new Thread(new Runnable(){
            @Override
            public void run() {
                File sqlfile = new File(filePath);
                try {
                    cmdtext.setEditable(false);
                    toolbar.setCancelBtnEnab(true);
                    isCancel = false;
                    cmdtext.recvTop(topStr, true);
        
                    if (sqlfile.exists()) {
                        List<String> lines = FileUtils.readLines(sqlfile, ChardetUtil.detectCharset(sqlfile));
                        for (String line : lines) {
                            if (isCancel) {
                                break;
                            }
                            cmdtext.recv(line, false);
                            cmdTool.put(line);
                            runOneSql();
                        }
                    } else {
                        cmdtext.recv(CmdMgr.getLang("fileexist"), true);
                        cmdtext.recvTop(topStr, true);
                    }
                } catch (IOException e) {
                    cmdtext.recv(e.toString(), true);
                    cmdtext.recvTop(topStr, true);
                }
                cmdtext.setEditable(true);
                toolbar.setCancelBtnEnab(false);
            }
        }).start();
	}
	
	/**
	 * 切换模式
	 */
	public void updaSchema(String schema){
		try {
			jdbc.setSchema(schema);
			jdbc.setCurSessionSchema(DbCmdStrUtil.toDbCmdStr(schema,DriverUtil.getDbType(jdbc)));
			ConnUtil.setCurrentSchema(conn,jdbc.getCurSessionSchema() );
            //设置关键词
            cmdtext.setKeyWord();
		} catch (Exception e) {
			cmdtext.recv(e.toString(), false);
			cmdtext.setEditable(false);
		}
	}
	
	/**
	 * 获取编辑器内容
	 * @return
	 */
	public String getText(){
		return cmdtext.getTextArea().getText();
	}
	
	/**
	 * 设置查询参数
	 * @param row
	 * @param nullSigns
	 */
	public void setSqlRunTool(int row,String nullSigns){
		sqlRunTool.setPageNum(row);
		sqlRunTool.setNullV(nullSigns);
	}
	
	private void runsql(String sql) {
		new Thread(new Runnable() {
            @Override
            public void run() {
                cmdtext.setEditable(false);
                toolbar.setCancelBtnEnab(true);
                cmdTool.put(sql);
                if (cmdTool.isDone()) {
                    String tmp = cmdTool.toString();
                    tmp = ParserUtil.trim(dbType, tmp);
                    DbCmdType t = DbCmdTool.toType(tmp);
                    if (t == DbCmdType.FILE_CMD) {
                        String filePath = cmdTool.toString();
                        filePath = filePath.split(";")[0];
                        filePath = filePath.substring(1).trim();
                        System.out.println(filePath);
                        cmdTool.reset();
                        File sqlfile = new File(filePath);
                        if (sqlfile.exists()) {
                            try {
                                List<String> lines = FileUtils.readLines(sqlfile, ChardetUtil.detectCharset(sqlfile));
                                cmdtext.recvTop(topStr, true);
                                for (String line : lines) {
                                    if (isCancel) {
                                        break;
                                    }
                                    cmdtext.recv(line, false);
                                    cmdTool.put(line);
                                    runOneSql();
                                }
                            } catch (IOException e) {
                            	e.printStackTrace();
                                cmdtext.recv(e.toString(), true);
                                cmdtext.recvTop(topStr, true);
                            }
                        } else {
                            cmdtext.recv(CmdMgr.getLang("fileexist"), true);
                            cmdtext.recvTop(topStr, true);
                        }
                    } else {
                        runOneSql();
                    }
                } else {
                    cmdtext.recvTop("   " + (cmdTool.getLines().size() + 1), true);
                }
                cmdtext.setEditable(true);
                toolbar.setCancelBtnEnab(false);
            }
        }).start();
	}

	private void runOneSql() {
		try {
			if (cmdTool.isDone()) {
				try {
					String tmp = cmdTool.toString();
					tmp = ParserUtil.trim(dbType, tmp);
					DbCmdType t = DbCmdTool.toType(tmp);
					if (t == DbCmdType.PROMPT || t == DbCmdType.COMMENT) {
						cmdtext.recv(tmp, true);
					}
					if (t == DbCmdType.SQL) {
//					System.out.println(tmp);
						sqlRunTool.runSql(tmp);
						tmp = sqlRunTool.nextPage();
						while (tmp != null) {
							cmdtext.recv(tmp, true);
							tmp = sqlRunTool.nextPage();
						}
					}
				} catch (Exception e) {
					cmdtext.recv(e.toString(), true);
				}
				if (!cmdTool.toString().trim().isEmpty()) {
					historySql.add(cmdTool.toString());
					hisIndex = historySql.size();
				}
				cmdTool.reset();
				cmdtext.recvTop(topStr, true);
			} else {
				cmdtext.recvTop("   " + (cmdTool.getLines().size() + 1), true);
			}
		}catch (Exception e){
		    e.printStackTrace();
		}
	}
	
	public JdbcBean getJdbc() {
		return jdbc;
	}
}
