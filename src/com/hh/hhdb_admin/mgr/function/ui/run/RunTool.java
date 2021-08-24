package com.hh.hhdb_admin.mgr.function.ui.run;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RunTool {
	Connection conn;
	String sql;
	Statement stmt;
	CallableStatement call;
	
	public RunTool(Connection conn, String sql) {
		this.conn = conn;
		this.sql = sql;
	}
	public List<String> procRun() throws SQLException{
		List<String> l = new ArrayList<>();
		DBTypeEnum dbtype = DriverUtil.getDbType(conn);
		switch (dbtype) {
		case hhdb:
		case pgsql:
			hhPgProcOutput(conn, sql, l);
			break;
		case oracle:
			oracleProcOutput(conn, sql, l);
			break;
		// TODO 其他数据库类型
		default:
		}
		return l;
	}
	public void cancel() {
		if(call!=null) {
			try {
				call.cancel();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
		}
		if(stmt!=null) {
			try {
				stmt.cancel();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
		}
	}
	private void oracleProcOutput(Connection conn, String sql, List<String> l) throws SQLException {
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
	}

	private void hhPgProcOutput(Connection conn, String sql, List<String> l) throws SQLException {
		stmt = conn.createStatement();
		boolean hashResult = stmt.execute(sql);
		SQLWarning sqlWarning = stmt.getWarnings();
		
		if (null != sqlWarning) {
			String str = sqlWarning.getMessage();
			if (StringUtils.isNotEmpty(str)) {
				l.add(str);
				while (sqlWarning != null) {
					sqlWarning = sqlWarning.getNextWarning();
					if (null != sqlWarning) {
						l.add(sqlWarning.getMessage());
					}
				}
			}
		}
		
		if(hashResult) {
			ResultSet rs = stmt.getResultSet();
			while (rs.next()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				for (int i = 0; i < rsmd.getColumnCount(); i++) {
					l.add(rsmd.getColumnLabel(i + 1) + "=" + rs.getString(i + 1));
				}
			}
		}
	}
}
