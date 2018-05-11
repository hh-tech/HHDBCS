package com.hhdb.csadmin.plugin.cmd.console;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;

/**
 * 用来执行SQL语句的线程类
 * 
 * @author 张涛
 * @version 2017年4月19日
 */
public class ExecuteSQLWorker extends Thread {
	private KeyHandler keyHandler;
	private String sql;

	ExecuteSQLWorker(String line,KeyHandler keyHandler) {
		this.keyHandler = keyHandler;
		this.sql = line;
	}

	@Override
	public void run() {
		/*
		 * 判断是否已经在事务中 不在事务中, 判断fetchSize>0和select, 开启事务, 完了以后commit
		 */
//		if (keyHandler.statement.getTransactionState())
//			keyHandler.openTransaction = true;
//		boolean transaction = !keyHandler.openTransaction
//				&& keyHandler.statement.getFetchSize() > 0
//				&& (sql.startsWith("select") || sql.startsWith("SELECT"));
		try {
//			if (transaction) // 没开启事务
//				keyHandler.statement.execute("begin");
			long ts = 0, time = 0;
			int count = 0;
			if (keyHandler.printC.timingFlag)
				ts = System.currentTimeMillis();
			//keyHandler.statement.execute(sql);
			//SqlExeUtil.executeUpdate(keyHandler.connection, sql);
			String flagsql = sql.trim().toUpperCase();
			if(flagsql.startsWith("SELECT")&&
					(flagsql.indexOf("INTO")<0||
							(flagsql.indexOf("INTO")>0&&
									flagsql.indexOf("INTO")>
							flagsql.indexOf("FROM")))){
				if(sql.trim().endsWith(";")){
					sql = sql.trim().substring(0,sql.trim().length()-1);
				}
				String csql = "select t1.* from ("+sql+") t1 limit 1000";
				keyHandler.resultlist = SqlQueryUtil.selectList(keyHandler.connection,csql);
			}else{
				count = SqlExeUtil.executeUpdate(keyHandler.connection, sql);
			}
			if (keyHandler.printC.timingFlag)
				time = System.currentTimeMillis() - ts;
			//存储耗时
			keyHandler.printC.time=time;
			//keyHandler.result = keyHandler.statement.getResult();
			keyHandler.printC.printResult(keyHandler.resultlist,count, 0,
					ConsoleConstant.SHOW_ROWS);

//			String warning = keyHandler.result.getWarning();
//			if (warning != null){
//				keyHandler.printC.printResponse(warning + "\n");
//			}

			int size = keyHandler.resultlist!=null?keyHandler.resultlist.size()-1:0;
			if (size > ConsoleConstant.SHOW_ROWS) {
				keyHandler.rowsIndex = ConsoleConstant.SHOW_ROWS;
				keyHandler.inputC.setMoreToText();
			}
//			else if (keyHandler.statement.hasNextBlock()) {
//				keyHandler.result = null;
//				keyHandler.inputC.setMoreToText();
//			} 
			else {
				//if ("SELECT".equals(keyHandler.result.getCommand()))
				if(keyHandler.resultlist!=null){
					keyHandler.printC.printResponse("(" + size + " 行记录)\n");
					keyHandler.rowsIndex = size;
					keyHandler.resultlist = null;
				}
					
//				keyHandler.result = null;
				
				keyHandler.inputC.setText(null);
			}
//			if (!keyHandler.statement.hasNextBlock() && transaction){
//				keyHandler.statement.execute("commit");
//			}
			keyHandler.interrupted = false;
		} catch (Exception e) {
			//e.printStackTrace();
			String errmsg = e.getMessage() == null ? ConsoleConstant.MSG_UNKNOWN_ERROR
					: e.getMessage();
			if ("连接已中断".equals(errmsg)) {
				keyHandler.interrupted = true;
				errmsg += ", 再次输入SQL会自动重新建立连接";
			}
			keyHandler.printC.printResponse(errmsg + "\n");
			keyHandler.inputC.setText(null);
		}
	}

}
