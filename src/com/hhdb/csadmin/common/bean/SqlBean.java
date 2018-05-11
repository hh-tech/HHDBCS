package com.hhdb.csadmin.common.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SqlBean {
	private List<String[]> columnList = new ArrayList<String[]>();
	private String sql;

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getSql() {
		return this.sql;
	}

	public String replaceParams(Map<String, Object> params) {
		List<String> list = getColumnKeys();
		String sqlstr = new String(sql);
		for (int j = 0; j < list.size(); j++) {
			int i = sqlstr.indexOf('?');
			if (i != -1) {
				sqlstr = sqlstr.replaceFirst("\\?", params.get(list.get(j)) + "");
			}
		}
		return sqlstr;
	}

	public String replaceParams(Object[] params) {
		String sqlstr = new String(sql);
		for (int j = 0; j < params.length; j++) {
			int i = sqlstr.indexOf('?');
			if (i != -1) {
				sqlstr = sqlstr.replaceFirst("\\?", params[j] + "");
			}
		}
		return sqlstr;
	}

	public void setColumnList(List<String[]> columnList) {
		this.columnList = columnList;
	}

	public List<String[]> getColumnList() {
		return this.columnList;
	}

	// 获取字段中文/表头
	public List<String> getColumnNames() {
		List<String> list = new ArrayList<String>();
		for (String[] ss : columnList) {
			list.add(ss[1]);
		}
		return list;
	}

	// 获取字段
	public List<String> getColumnKeys() {
		List<String> list = new ArrayList<String>();
		for (String[] ss : columnList) {
			list.add(ss[0]);
		}
		return list;
	}
}
