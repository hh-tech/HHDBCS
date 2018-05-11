package com.hhdb.csadmin.plugin.cmd.console;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.util.List;

import javax.swing.JScrollBar;

import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.common.dao.ConnService;
import com.hhdb.csadmin.plugin.cmd.SqlCmdPlugin;

/**
 * 按键处理
 * 
 * @author 钟苇
 * @version 2017年10月23日 modify by 张涛
 */
public class KeyHandler implements KeyListener{
	private StringBuffer sqlBuffer;
	
	ConsolePrint printC;
	ConsolePrefix prefixC;
	ConsoleInput inputC;
//	private JScrollBar scrollBar;
	private LineHandler lineHandler;
	Connection connection;
	//Statement statement;
//	private int fetchTotalRows;
	//ResultSet result;
	List<List<Object>> resultlist;
	int rowsIndex;
	private SqlCmdPlugin sqlcmdplugin;
	/**
	 * true:中断, false:没中断
	 */
	boolean interrupted;
	/**
	 * true = 自动开启事务之前已经开启事务
	 */
	boolean openTransaction = true;
	
	public KeyHandler(ConsolePrint printC, ConsolePrefix prefixC, ConsoleInput inputC, JScrollBar scrollBar, Connection connection,SqlCmdPlugin sqlcmdplugin) {
		this.sqlcmdplugin = sqlcmdplugin;
		sqlBuffer = new StringBuffer();
		lineHandler = new LineHandler(printC,sqlcmdplugin);
		this.inputC = inputC;
		this.prefixC = prefixC;
		this.printC = printC;
		this.printC.scrollBar = scrollBar;
		this.connection = connection;
//		try {
//			statement = connection.createStatement();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		switch(code){
		case KeyEvent.VK_ENTER:
			enterPressed();
			break;
		case KeyEvent.VK_UP:
			inputC.upPressed();
			break;
		case KeyEvent.VK_DOWN:
			inputC.downPressed();
			break;
		case KeyEvent.VK_C:
			if(e.getModifiers() == 2){
				ctrlAndCPressed();
			}
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		switch(code){
		case KeyEvent.VK_ENTER:
			enterReleased();
			break;
		case KeyEvent.VK_Q:
			qReleased();
			break;
		}
	}
	
	
	public void enterPressed() {
		// show more
		if (resultlist != null&&resultlist.size()-1>rowsIndex) {
			// 没打印完
			printC.printMoreRow(resultlist, rowsIndex);
			rowsIndex++;
			// 打印完
			if (resultlist.size()-1 == rowsIndex) {
				resultlist = null;
				//if (!statement.hasNextBlock()) {
//					if(fetchTotalRows > 0)
//						printC.printResponse("(" + (fetchTotalRows * statement.getFetchSize() + rowsIndex) + " 行记录)\n");
//					else{
						printC.printResponse("(" + rowsIndex + " 行记录)\n");
//					}
					//为了后面的enterReleased事件
					inputC.setText(null);
				//}
					rowsIndex = 0;
			}
			return;
		} else {
			//指针未结束
//			if (statement.hasNextBlock()) {
//				// 没查询完
//				try {
//					result = statement.nextBlock();
//					if (!openTransaction && !statement.hasNextBlock()) {
//						statement.execute("commit");
//					}
//				} catch (JDBCException | IOException e) {
//					e.printStackTrace();
//					printC.printResponse(e.getMessage() + "\n");
//				}
//				
//				fetchTotalRows++;
//				if(result.getRows().size() == 0) {
//					rowsIndex = 0;
//					result = null;
//					printC.printResponse("(" + (fetchTotalRows * statement.getFetchSize() + rowsIndex) + " 行记录)\n");
//					fetchTotalRows = 0;
//					return;
//				}
//
//				rowsIndex = 0;
//				printC.setMaxLength(result);
//				printC.printMoreRow(result, rowsIndex);
//				rowsIndex++;
//				if (result.getDbTable().getRows().size() == rowsIndex) {
//					result = null;
//					printC.printResponse("(" + (fetchTotalRows * statement.getFetchSize() + rowsIndex) + " 行记录)\n");
//					fetchTotalRows = 0;
//				}
//				return;
//			}
		}

		// 将输入内容显示到printC面板
		String line = inputC.getText().trim();
		printC.printResponse(prefixC.getText()+line+"\n");

		// 保存非空输入到历史记录, 可以上下键选择
		if (line.length() > 0) {
			inputC.addHLine(line);
		} else{
			return;
		}
			
		/*   输入行处理        */
		if (line.equals(ConsoleConstant.REGEX_CLEAR)) {
			// 清屏
			printC.clear();
		} else if (line.equals(ConsoleConstant.REGEX_TIMING)) {
			// 计时
			printC.timing();
//		} else if (line.matches(ConsoleConstant.REGEX_SWITCH_DB)) {
//			// 切换
//			connection = lineHandler.switchDB(line, prefixC, connection);
//			//statement = connection.createStatement();
		} else if (line.matches(ConsoleConstant.REGEX_FETCH_COUNT)) {
			// fetch_count
			lineHandler.fetchCount(line, connection);
		} else if (line.matches(ConsoleConstant.REGEX_RESTORE)) {
			// 还原
			lineHandler.restore(line, connection);
		} else if (line.startsWith("\\") && (sqlBuffer.length() == 0)) {
			if(interrupted)
				try {
					CmdEvent getsbEvent = new CmdEvent(SqlCmdPlugin.class.getPackage().getName(), "com.hhdb.csadmin.plugin.conn", "GetServerBean");
					HHEvent revent = sqlcmdplugin.sendEvent(getsbEvent);
					ServerBean sb = (ServerBean)revent.getObj();
					
					connection = ConnService.createConnection(sb);
					//connection = ConnectionHelper.getConnection(connection.getHost(), connection.getPort(), connection.getDatabase(), connection.getUser(), connection.getPassword());
				} catch (Exception e) {
					printC.printResponse("重新建立数据库连接异常\n");
				}
			try {
				// PG命令
				executePg(line);
			} catch (Exception e) {
				printC.printResponse(e.getMessage() + "\n");
			}
		} else {
			if(interrupted)
				try {
					CmdEvent getsbEvent = new CmdEvent(SqlCmdPlugin.class.getPackage().getName(), "com.hhdb.csadmin.plugin.conn", "GetServerBean");
					HHEvent revent = sqlcmdplugin.sendEvent(getsbEvent);
					ServerBean sb = (ServerBean)revent.getObj();
					
					connection = ConnService.createConnection(sb);
					
					//connection = ConnectionHelper.getConnection(connection.getHost(), connection.getPort(), connection.getDatabase(), connection.getUser(), connection.getPassword());
				} catch (Exception e) {
					printC.printResponse("重新建立数据库连接异常\n");
				}
			// 执行SQL
			executeSql(line);
		}
	}
	
	void executePg(String line){
		String sql = LineHandler.pgCommandHandler(line);
		if(sql==null){
			printC.printResponse(ConsoleConstant.MSG_INIT_PGCMD_FAILED);
			return;
		}
		// 特殊处理, 多个select
		if (sql.matches(ConsoleConstant.REGEX_D_PATTERN)) {
			try {
				lineHandler.doDPattern(sql, connection);
			} catch (Exception e) {
				String error = e.getMessage() == null ? ConsoleConstant.MSG_UNKNOWN_ERROR : e.getMessage();
				printC.printResponse(error + "\n");
			}
		} else {
			new ExecuteSQLWorker(sql,this).start();
		}
	}
	
	void executeSql(String line) {
		sqlBuffer.append(line);
		if (line.lastIndexOf(';') > -1) {
			new ExecuteSQLWorker(sqlBuffer.toString(), this).run();
			sqlBuffer.setLength(0);
			prefixC.setStart();
		} else {
			if (line.length() > 0) {
				sqlBuffer.append(" ");
				prefixC.setContinue();
			}
		}
	}
	
	
	
	public void qReleased() {
		if (inputC.beMore()) {
			//不展示后面的记录
			prefixC.setVisible(true);
			inputC.setText(null);
			inputC.setEditable(true);
			printC.printTiming();
			//result = null;
			rowsIndex = 0;
//			fetchTotalRows = 0;
			resultlist=null;
//			if (!openTransaction && statement.getTransactionState() && statement.getFetchSize() > 0) {
//				try {
//					statement.execute("commit");
//				} catch (IOException | JDBCException e) {
//					printC.printResponse(e.getMessage() + "\n");
//				}
//			}
		}
	}

	public void ctrlAndCPressed() {
//		try {
//			ConnectionHelper.cancelRequest(connection.getHost(), connection.getPort(), connection.getCancelPid(), connection.getCancelKey());
//		} catch (JDBCException e) {
//			printC.printResponse(ConsoleConstant.MSG_CANCEL_REQUEST_FAILED);
//		}
	}

	public void enterReleased() {
		if (!inputC.beMore()) {
			//result已全部打印
			prefixC.setVisible(true);
			inputC.setText(null);
			inputC.setEditable(true);
			printC.printTiming();
		}else{
			prefixC.setVisible(false);
			inputC.setEditable(false);
		}
		inputC.selectAll();
	}
}
