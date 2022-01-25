package com.hh.hhdb_admin.mgr.query.util;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.json.JsonObject;
import com.hh.frame.parser.AbsStmt;
import com.hh.frame.parser.ParserUtil;
import com.hh.frame.parser.sql_fmt2.SqlFmtUtil;
import com.hh.frame.sqlwin.SqlWin;
import com.hh.frame.sqlwin.util.SqlWinUtil;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.common.util.textEditor.QueryEditorTextArea;
import com.hh.hhdb_admin.mgr.query.QueryMgr;
import com.hh.hhdb_admin.mgr.sql_book.SqlBookMgr;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.hhdbsql.HHConnection;
import org.hhdbsql.copy.CopyManager;
import org.postgresql.PGConnection;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class QuerUtil {
	private static String logName = QuerUtil.class.getSimpleName();

	/**
	 * 获取总记录条数
	 */
	public static String getCount(JdbcBean jdbc,String sql){
		Connection conn = null;
		try {
			conn = ConnUtil.getConn(jdbc);
			String countsql = "select count(1) from (" + (sql.endsWith(";") ? sql.substring(0, sql.length()-1):sql) + " )a";
			long xtime = System.currentTimeMillis();
			List<List<Object>> countlist = SqlQueryUtil.selectList(conn, countsql);
			long xxtime = System.currentTimeMillis();
			return "总记录数查询结果："+QueryMgr.getLang("timeSpent") + "=" + (xxtime - xtime) + "ms," + QueryMgr.getLang("total")+"="+ countlist.get(1).get(0);
		} catch (Exception e) {
			e.printStackTrace();
			logUtil.error(logName, e);
			PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e.getMessage());
			return "";
		} finally {
			ConnUtil.close(conn); // 关闭连接
		}
	}
	/**
	 * \copy元子命令导入导出
	 * 仅支持hhdbsql
	 * 例子：\copy a to 'C:\Users\hhxd\Desktop\新建文件夹/bbs.csv' csv header ;
	 * 			\copy a from 'C:\Users\hhxd\Desktop\新建文件夹/bbs.csv';
	 * @param substring
	 * @param conn
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void copyStream(JdbcBean jdbc,String substring) throws Exception {
		Connection conn = ConnUtil.getConn(jdbc);
		StringBuilder sb = new StringBuilder();
		sb.append(substring);
		if(DriverUtil.getDbType(jdbc)==DBTypeEnum.hhdb){
			CopyManager cpManager = ((HHConnection) conn).getCopyAPI();
			//如果是导入命令
			//因为用户可能地址或者表名上加from ，所以判断字段前后加上空格
			if(substring.indexOf(" from ")!=-1){
				//路径是由单引号包起来的
				int i = substring.indexOf(" '");
				int o  = substring.indexOf("' ");
				if(o==-1){
					o=substring.lastIndexOf("'");
				}
				String str = sb.substring(i+2,o).replaceAll(" ", "");
				sb.replace(i, o+2, " stdin ");
				InputStream ip = new FileInputStream(str);
				cpManager.copyIn(sb.toString().substring(1),ip);
				ip.close();
				//经测试，导入的时候如果不加这个，数据没有导进来，且显示操作已执行
				//需要提交
				//conn.commit();
			}
			//导出命令
			else if(substring.indexOf(" to ")!=-1){
				int i = substring.indexOf(" '");
				//截取路径四种情况
				//1.路径后面加了空格
				//2.路径后面没加空格且以分号结尾
				//3.路径后面没东西且回车换行了（换行默认添加分号）
				//4.路径后面没东西了
				int o  = substring.indexOf("' ");
				//如果没有截取到说明后面没有加东西，直接截取最后一个'
				if(o==-1){
					o=substring.lastIndexOf("'");
				}
				String str = sb.substring(i+2,o).replaceAll(" ", "");
				sb.replace(i, o+2, " stdout ");
				OutputStream op = new FileOutputStream(str);
				cpManager.copyOut(sb.toString().substring(1),op);
				op.close();
			}
		}else{
			org.postgresql.copy.CopyManager cpManager;
			cpManager = ((PGConnection) conn).getCopyAPI();
			if(substring.indexOf(" from ")!=-1){
				int i = substring.indexOf(" '");
				int o  = substring.indexOf("' ");
				if(o==-1){
					o=substring.lastIndexOf("'");
				}
				String str = sb.substring(i+2,o).replaceAll(" ", "");
				sb.replace(i, o+2, " stdin ");
				InputStream ip = new FileInputStream(str);
				cpManager.copyIn(sb.toString().substring(1),ip);
				ip.close();
				conn.commit();
			}
			else if(substring.indexOf(" to ")!=-1){
				int i = substring.indexOf(" '");
				int o  = substring.indexOf("' ");
				if(o==-1){
					o=substring.lastIndexOf("'");
				}
				String str = sb.substring(i+2,o).replaceAll(" ", "");
				sb.replace(i, o+2, " stdout ");
				OutputStream op = new FileOutputStream(str);
				cpManager.copyOut(sb.toString().substring(1),op);
				op.close();
			}
		}
	}

	/**
	 * 执行时报错,回滚
	 */
	public static void rollbackErr(Connection conn)throws Exception{
		DBTypeEnum dbtype = DriverUtil.getDbType(conn);

		if (dbtype == DBTypeEnum.hhdb || dbtype == DBTypeEnum.pgsql) {
			conn.rollback();
		}
	}

	/**
	 * 下一页报错时回滚
	 * @param sqlWin
	 * @throws Exception
	 */
	public static void nextPageRollback(Connection conn,Exception ex)throws Exception{
		DBTypeEnum dbtype = DriverUtil.getDbType(conn);

		if (dbtype == DBTypeEnum.hhdb || dbtype == DBTypeEnum.pgsql) {
			if (ex.getMessage().contains("current transaction is aborted, commands ignored until end of transaction block")
				|| ex.getMessage().contains("当前事务被终止, 事务块结束之前的查询被忽略")) {
				JOptionPane.showMessageDialog(null, QueryMgr.getLang("nextNo"),QueryMgr.getLang("error"), JOptionPane.ERROR_MESSAGE);
				if (!conn.getAutoCommit()) conn.rollback();
			}
		}
	}

	public static String initCommitType(DBTypeEnum dbtype) {
		if(SqlWinUtil.defultAutoCommit(dbtype)) {
			return QueryMgr.getLang("auto-commit");
		}else {
			return QueryMgr.getLang("auto-guestPosting");
		}
	}

	/**
	 * 判断是否有提交
	 */
	public static Boolean hasCommit(SqlWin sqlwin)throws Exception{
		DBTypeEnum dbTypeEnum = DriverUtil.getDbType(sqlwin.getConn());
		if (dbTypeEnum == DBTypeEnum.hhdb || dbTypeEnum == DBTypeEnum.pgsql) {
			if (sqlwin.hasCommit()) {
				return true;
			}else {
				//解决hh pg手动提交时创建函数判断不了是否有提交问题
				String sql = "select backend_xid from "+ (dbTypeEnum == DBTypeEnum.hhdb ? "hh" : "pg") +"_stat_activity where pid='%s'";
				String backend_xid = SqlQueryUtil.selectOneColumn(sqlwin.getConn(), String.format(sql, SqlWinUtil.getSid(sqlwin.getConn()))).get(0);
        		return StringUtils.isNotBlank(backend_xid);
			}
		}else {
			return	sqlwin.hasCommit();
		}
	}
	
	/**
	 * 格式化sql
	 * @param dbType
	 * @param textArea
	 * @throws Exception
	 */
	public static void formatSql(DBTypeEnum dbType,QueryEditorTextArea textArea) {
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotEmpty(textArea.getSelectedText())) {
			try {
				if (!StringUtils.isNotBlank(textArea.getSelectedText())) return;
				sb.append(SqlFmtUtil.fmt2Str(textArea.getSelectedText()).trim());
				textArea.getTextArea().replaceRange(sb.toString(),textArea.getTextArea().getSelectionStart(),textArea.getTextArea().getSelectionEnd());
			} catch (Exception e) {
				PopPaneUtil.error(StartUtil.parentFrame.getWindow(), QueryMgr.getLang("fmt_failed"));
			}
		}else {
			String str = textArea.getText();
			if (!StringUtils.isNotBlank(str)) return;
			boolean bool = true;
			List<AbsStmt> stmts = null;
			try {
				if (!StringUtils.endsWith(str.trim(), ";")) {
					str += ";";
					bool = false;
				}
				stmts = ParserUtil.getStmts(dbType, str);
			} catch (Exception ignored) {}
			
			try {
				if (null == stmts) {
					sb.append(SqlFmtUtil.fmt2Str(str).trim());
				} else {
					for (AbsStmt stmt : stmts) {
						sb.append(SqlFmtUtil.fmt2Str(ParserUtil.getSql(ParserUtil.toLines(str), stmt.getPos()).trim()));
					}
				}
				String sql = sb.toString().trim();
				textArea.getTextArea().replaceRange(bool ? sql : sql.substring(0,sql.length()-1),0,textArea.getText().length());
			} catch (Exception e) {
				PopPaneUtil.error(StartUtil.parentFrame.getWindow(), QueryMgr.getLang("fmt_failed"));
			}
		}
	}
	
	/**
	 * 保存sql到宝典
	 * @param txt	保存内容
	 * @param bool	是否提示
	 */
	public static void saveSqlBook(String txt,boolean bool){
		try {
			if (!StringUtils.isNotBlank(txt)) return;
			
			JsonObject o = StartUtil.eng.doCall(CsMgrEnum.SQL_BOOK, GuiJsonUtil.genGetShareIdMsg(SqlBookMgr.ObjType.SHARE_PATH));
			JFileChooser chooser = new JFileChooser();
			if (null != o) chooser.setCurrentDirectory(new File(GuiJsonUtil.toStrSharedId(o)));
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			FileFilter fileFilter = new FileNameExtensionFilter("SQL文件(*.sql)","sql");
			chooser.setFileFilter(fileFilter);
			if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
				String url = chooser.getSelectedFile().getCanonicalPath();
				url = url.endsWith(".sql") ? url : url+".sql";
				File file = new File(url);
				FileUtils.writeStringToFile(file, txt, "utf-8");
				if (bool) PopPaneUtil.info(StartUtil.parentFrame.getWindow(), QueryMgr.getLang("success"));
			}
		}catch (Exception e){
			e.printStackTrace();
			PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
		}
	}
}
