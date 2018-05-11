package com.hhdb.csadmin.plugin.cmd.console;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Connection;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.log.LM;
import com.hh.frame.common.util.db.SqlExeUtil;


/**
 * 恢复
 * 
 * @author 钟苇
 * @version 2016年11月4日
 */
public class ConsoleRestore {
	/**
	 * SQL开头, 如果";"后面跟着这些字符串, 则判定";"为一条SQL的结束
	 */
	private static final String[] COMM_SQL_START = { "INSERT ", "COMMENT ", "CREATE ", "SET ", "DROP ", "ALTER ", "GRANT " };
	/**
	 * function SQL 开头
	 */
	private static final String[] FUNC_SQL_START = { "CREATE FUNCTION ", "CREATE OR REPLACE FUNCTION " };
	/**
	 * function SQL 结束
	 */
	private static final String[] FUNC_SQL_END = { "$$;", " VOLATILE;" };

	private String line;
	//private HHStatement statement;
	private Connection connection;

	public ConsoleRestore(String line, Connection connection) {
		this.line = line;
		this.connection = connection;
	}

	public void restore(ConsolePrint printC) {
		// 导入耗时
		long ts = 0, time = 0;
		if(printC.timingFlag){
			ts = System.currentTimeMillis();
		}
		String path = line.split("\\s")[1];
		File sqlFile = new File(path);
		if (!sqlFile.exists())
			printC.printResponse("文件不存在\n");
		if (sqlFile.isDirectory())
			printC.printResponse("路径不能是目录\n");

		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(sqlFile, "r");
			StringBuffer sqlsb = new StringBuffer();
			String line;
			//Result result = null;
			while ((line = readRealLine(raf)) != null) {
				sqlsb.append(line);
				if (line.startsWith(FUNC_SQL_START[0]) || line.startsWith(FUNC_SQL_START[1])) {
					while (line != null && (!line.endsWith(FUNC_SQL_END[0]) && !line.endsWith(FUNC_SQL_END[1]))) {
						line = readRealLine(raf);
						sqlsb.append(line);
					}
				} else {
					while (line != null && (!line.endsWith(";") || !matchIntegritySqlStart(raf))) {
						line = readRealLine(raf);
						sqlsb.append(line);
					}
				}
//
//				statement.execute(sqlsb.toString());
//				result = statement.getResult();
				SqlExeUtil.executeUpdate(connection, sqlsb.toString());
				printC.printResult(null,1, 0, 0);
				sqlsb.setLength(0);
			}
			printC.printResponse("导入SQL脚本文件完成\n");
			if(printC.timingFlag){
				time = System.currentTimeMillis();
			}
			printC.time = time - ts;
			printC.printTiming();
		} catch (Exception e) {
			printC.printResponse(e.getMessage() + "\n");
		} finally {
			try {
				raf.close();
			} catch (IOException e) {
				LM.error(LM.Model.CS.name(), e);
			}
		}
	}

	/**
	 * 按一条完成SQL开头来匹配 true: 匹配到, false: 没匹配到
	 * 
	 * @param raf
	 * @return
	 * @throws IOException
	 */
	private boolean matchIntegritySqlStart(RandomAccessFile raf) throws IOException {
		long mark = raf.getFilePointer();
		String line = readRealLine(raf);
		if (line == null)
			return true;
		raf.seek(mark);
		for (String s : COMM_SQL_START) {
			if (line != null && line.toUpperCase().startsWith(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 读取一行有效sql, 排除空行和--注释行
	 * 
	 * @param raf
	 * @return
	 * @throws IOException
	 */
	private String readRealLine(RandomAccessFile raf) throws IOException {
		String line = raf.readLine();
		while (line != null) {
			if (line.startsWith("--") || StringUtils.isBlank(line)){
				line = raf.readLine();
			}
			else if(line.indexOf("--")>0){
				line = line.substring(0,line.indexOf("--"));
				break;
			}else{
				break;
			}
		}
		if (line == null)
			return line;
		return new String(line.getBytes("ISO-8859-1"), "utf-8");
	}

}
