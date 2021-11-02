package com.hh.hhdb_admin.mgr.query.util;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.dbmg.exp.data.seltype.*;
import com.hh.frame.parser.sql_fmt2.StmtFmtTool;
import com.hh.frame.parser.sql_fmt2.base.AbsSqlCode;
import com.hh.frame.parser.sql_fmt2.gen.SqlFmtParser;
import com.hh.frame.sqlwin.SqlWin;
import com.hh.frame.sqlwin.util.SqlWinUtil;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.common.util.textEditor.QueryEditorTextArea;
import com.hh.hhdb_admin.mgr.query.QueryMgr;
import org.apache.commons.lang3.StringUtils;
import org.hhdbsql.HHConnection;
import org.hhdbsql.copy.CopyManager;
import org.postgresql.PGConnection;

import javax.swing.*;
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

	public static String getVal(Enum<?> type,String val,JdbcBean jdbcBean) throws Exception {
		if (!StringUtils.isNotBlank(val)) return "____null";

		String value = val;
		DBTypeEnum dbTypeEnum = DriverUtil.getDbType(jdbcBean);
		if (dbTypeEnum == DBTypeEnum.oracle) {
			if (type == OraSelType.BLOB || type == OraSelType.RAW || type == OraSelType.CLOB || type == OraSelType.NCLOB) {
				value = "____null";
			}
		} else if (dbTypeEnum == DBTypeEnum.hhdb || dbTypeEnum == DBTypeEnum.pgsql) {
			if (type == PgSelType.BYTEA || type == PgSelType.OID || type == PgSelType.REFCURSOR) {
				value = "____null";
			}
		} else if (dbTypeEnum == DBTypeEnum.mysql) {
			if (type == MysqlSelType.BLOB || type == MysqlSelType.GEOMETRY) {
				value = "____null";
			}
		} else if (dbTypeEnum == DBTypeEnum.db2) {
			if (type == Db2SelType.BLOB || type == Db2SelType.CLOB) {
				value = "____null";
			}
		} else if (dbTypeEnum == DBTypeEnum.hive) {
		} else if (dbTypeEnum == DBTypeEnum.sqlserver) {
			if (type == SqlserverSelType.BLOB || type == SqlserverSelType.CLOB) {
				value = "____null";
			}
		} else if (dbTypeEnum == DBTypeEnum.oscar) {
			if (type == OscarSelType.BLOB || type == OscarSelType.CLOB || type == OscarSelType.BYTEA || type == OscarSelType.REFCURSOR) {
				value = "____null";
			}
		} else if (dbTypeEnum == DBTypeEnum.dm) {
			if (type == DmSelType.BLOB || type == DmSelType.CLOB) {
				value = "____null";
			}
		} else if (dbTypeEnum == DBTypeEnum.kingbase) {
			if (type == KingbaseSelType.BYTEA || type == KingbaseSelType.OID || type == KingbaseSelType.BLOB || type == KingbaseSelType.CLOB) {
				value = "____null";
			}
		} else {
			throw new IOException("未实现数据类型");
		}
		return value;
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

	public static void formatSql(QueryEditorTextArea textArea) {
		try {
			StringBuffer sb = new StringBuffer();
			String sql = StringUtils.isNotEmpty(textArea.getSelectedText()) ? textArea.getSelectedText() : textArea.getText();
			if (!StringUtils.isNotBlank(sql)) return;
			boolean bool = sql.trim().endsWith(";");

			SqlFmtParser parser=new SqlFmtParser(bool ? sql.trim() : sql.trim() + ";");
			List<AbsSqlCode> list=parser.allCodeList();
			StmtFmtTool fmtTool=new StmtFmtTool(list);

			fmtTool.fmt2Lines().forEach(a -> sb.append(a+"\n"));
			String str = sb.toString().trim();
			if (StringUtils.isNotEmpty(textArea.getSelectedText())) {
				textArea.getTextArea().replaceRange(bool ? str : str.substring(0,str.length()-1),
						textArea.getTextArea().getSelectionStart(),textArea.getTextArea().getSelectionEnd());
			}else {
				textArea.getTextArea().replaceRange(bool ? str : str.substring(0,str.length()-1),0,sql.length());
			}
		} catch (Exception e) {
			logUtil.error(logName, e);
			e.printStackTrace();
		}
	}
}
