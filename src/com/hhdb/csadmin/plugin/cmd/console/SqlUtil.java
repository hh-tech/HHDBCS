package com.hhdb.csadmin.plugin.cmd.console;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SqlUtil {
	public static Map<String, Object> getMap(ResultSet rs) throws SQLException{
		Map<String, Object>  map=new HashMap<String, Object>();		
		ResultSetMetaData rsmd = rs.getMetaData();
		for (int i = 0; i < rsmd.getColumnCount(); i++) {
			map.put(lookupColumnName(rsmd, i+1), rs.getObject(i + 1));
		}
		return map;
	}
	
	public static Map<String, String> getStrMap(ResultSet rs) throws SQLException{
		Map<String, String>  map=new HashMap<String, String>();		
		ResultSetMetaData rsmd = rs.getMetaData();
		for (int i = 0; i < rsmd.getColumnCount(); i++) {
			map.put(lookupColumnName(rsmd, i+1), rs.getString(i + 1));
		}
		return map;
	}
	
	/**
	 * 获取结果的列名
	 * @param resultSetMetaData
	 * @param columnIndex
	 * @return
	 * @throws SQLException
	 */
	public static String lookupColumnName(final ResultSetMetaData resultSetMetaData, final int columnIndex) throws SQLException {
		String name = resultSetMetaData.getColumnLabel(columnIndex);
		if (name == null || name.length() < 1) {
			name = resultSetMetaData.getColumnName(columnIndex);
		}
		return name;
	}
}
