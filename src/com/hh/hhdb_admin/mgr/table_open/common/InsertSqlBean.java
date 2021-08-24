package com.hh.hhdb_admin.mgr.table_open.common;

import com.hh.frame.common.base.DBTypeEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ouyangxu
 * @date 2021-01-29 0029 17:26:27
 */
public class InsertSqlBean implements Serializable {
	private static final long serialVersionUID = -573122708500148812L;
	private DBTypeEnum dbTypeEnum;
	private String schemaName;
	private String tableName;
	private List<String> colNames;
	private List<String> values;
	private List<AddLobBean> addLobs;

	public InsertSqlBean(DBTypeEnum dbTypeEnum, String schemaName, String tableName) {
		this.dbTypeEnum = dbTypeEnum;
		this.schemaName = schemaName;
		this.tableName = tableName;
	}


	public DBTypeEnum getDbTypeEnum() {
		return dbTypeEnum;
	}

	public void setDbTypeEnum(DBTypeEnum dbTypeEnum) {
		this.dbTypeEnum = dbTypeEnum;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<String> getColNames() {
		return colNames;
	}

	public void setColNames(List<String> colNames) {
		this.colNames = colNames;
	}

	public void addColNames(String... colNamesArray) {
		if (colNames == null) {
			colNames = new ArrayList<>();
		}
		colNames.addAll(Arrays.asList(colNamesArray));
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public void addValues(String... valuesArray) {
		if (values == null) {
			values = new ArrayList<>();
		}
		values.addAll(Arrays.asList(valuesArray));
	}

	public List<AddLobBean> getAddLobs() {
		return addLobs;
	}

	public void addAddLobs(AddLobBean... addLobsArray) {
		if (addLobs == null) {
			addLobs = new ArrayList<>();
		}
		addLobs.addAll(Arrays.asList(addLobsArray));
	}

	public void setAddLobs(List<AddLobBean> addLobs) {
		this.addLobs = addLobs;
	}

	@Override
	public String toString() {
		StringBuilder sql = new StringBuilder();
		StringBuilder colNamesStr = new StringBuilder();
		StringBuilder valuesStr = new StringBuilder();
		if (colNames != null && colNames.size() > 0) {
			for (String colName : colNames) {
				colNamesStr.append(colName).append(",");
			}
		}
		if (values != null && values.size() > 0) {
			for (String value : values) {
				valuesStr.append(value).append(",");
			}
		}
		sql.append("INSERT INTO ").append(ModifyTabDataUtil.getTabFullName(dbTypeEnum, schemaName, tableName));
		sql.append(String.format("(%s) VALUES (%s) ", delSb(colNamesStr).toString(), delSb(valuesStr).toString()));

		return sql.toString();
	}

	private StringBuilder delSb(StringBuilder sb) {
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb;
	}
}
