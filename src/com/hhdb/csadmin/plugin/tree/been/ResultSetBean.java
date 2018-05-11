package com.hhdb.csadmin.plugin.tree.been;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResultSetBean {
	private List<Map<String, Object>> valueList = new ArrayList<Map<String, Object>>();
	private List<String[]> columnList = new ArrayList<String[]>();

	public List<String[]> getColumnList() {
		return this.columnList;
	}

	public void setColumnList(List<String[]> columnList) {
		this.columnList = columnList;
	}

	public List<Map<String, Object>> getValueList() {
		return this.valueList;
	}

	public void setValueList(List<Map<String, Object>> valueList) {
		this.valueList = valueList;
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
