package com.hh.hhdb_admin.mgr.function.ui.run;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.sqlwin.rs.MultiRsBean;
import com.hh.frame.sqlwin.util.WinRsUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 函数运行
 */
public class RunTool {
	Connection conn;
	String sql;
	Statement stmt;
    ResultSet rs;
	CallableStatement call;
	
	public RunTool(Connection conn, String sql) {
		this.conn = conn;
		this.sql = sql;
	}
	
	public void cancel() {
		if(call!=null) {
			try {
				if (!call.isClosed()) call.cancel();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(stmt!=null) {
			try {
				if (!stmt.isClosed()) stmt.cancel();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (rs != null) {
            try {
				if (!rs.isClosed()) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
	}
	
	public List<String> oracleRun() throws SQLException {
		List<String> l = new ArrayList<>();
		String enableSql = "begin dbms_output.enable(); end;";
		StringBuilder sb = new StringBuilder();
		sb.append("declare\n");
		sb.append("num integer := 1000;\n");
		sb.append("begin\n");
		sb.append("dbms_output.get_lines(?, num);\n");
		sb.append("dbms_output.disable();\n");
		sb.append("end;\n");
		try {
			stmt = conn.createStatement();
			stmt.execute(enableSql);
			if((sql.trim().toLowerCase().startsWith("begin") || sql.trim().toLowerCase().startsWith("declare")) && !sql.trim().endsWith(";")) {
				sql = sql.trim()+";";
			}
			stmt.execute(sql);
			call = conn.prepareCall(sb.toString());
			call.registerOutParameter(1, Types.ARRAY, "DBMSOUTPUT_LINESARRAY");
			call.execute();
			Array array = call.getArray(1);
			for (Object o : (Object[]) array.getArray()) {
				if (o != null)
					l.add(o.toString());
			}
		} finally {
			if (call != null)
				call.close();
			if (stmt != null)
				stmt.close();
		}
		return l;
	}
	
	/**
	 * 运行hh/Pg函数
	 * @param outStr	函数中打印的信息
	 * @return
	 * @throws SQLException
	 */
	public Map<String,String> hhPgRun(StringBuffer outStr) throws SQLException {
		Map<String,String> map = new HashMap<>();
		stmt = conn.createStatement();
		boolean hashResult = stmt.execute(sql);
		SQLWarning sqlWarning = stmt.getWarnings();
		
		if (null != sqlWarning) {
			String str = sqlWarning.getMessage();
			if (StringUtils.isNotEmpty(str)) {
				outStr.append(str+"\n");
				while (sqlWarning != null) {
					sqlWarning = sqlWarning.getNextWarning();
					if (null != sqlWarning) {
						outStr.append(sqlWarning.getMessage()+"\n");
					}
				}
			}
		}
		if(hashResult) {
			rs = stmt.getResultSet();
			while (rs.next()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				for (int i = 0; i < rsmd.getColumnCount(); i++) {
					map.put(rsmd.getColumnLabel(i + 1),rs.getString(i + 1));
				}
			}
		}
		return map;
	}
    
    /**
     * 运行mysql函数
     * @param outSql    查询存储过程输出参数值sql
     * @param parList   存储过程参数设置sql
     * @return
     * @throws SQLException
     */
	public Map<String,String> mysqlRun(StringBuffer outSql,List<String> parList) throws SQLException {
        Map<String,String> map = new HashMap<>();
        boolean hashResult = false;
        stmt = conn.createStatement();
        if (null != parList && !parList.isEmpty()) {
            //设置参数
			SqlExeUtil.batchExecute(conn,parList);
            //执行函数
            SqlExeUtil.executeUpdate(conn,sql);
            //获取结果
           	if (StringUtils.isNotBlank(outSql)) hashResult = stmt.execute(outSql.toString());
        } else {
            hashResult = stmt.execute(sql);
        }
        if (hashResult) {
            rs = stmt.getResultSet();
            while (rs.next()) {
                ResultSetMetaData rsmd = rs.getMetaData();
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    map.put(rsmd.getColumnLabel(i + 1),rs.getString(i + 1));
                }
            }
        }
		return map;
	}
	
	/**
	 * 运行sqlsrver函数
	 * @return
	 * @throws SQLException
	 */
	public MultiRsBean sqlsrverRun() throws SQLException {
		stmt = conn.createStatement();
        return WinRsUtil.getMultiRs(stmt, sql);
	}
}
