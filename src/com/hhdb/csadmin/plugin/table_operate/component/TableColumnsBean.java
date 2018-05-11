package com.hhdb.csadmin.plugin.table_operate.component;

public class TableColumnsBean implements Cloneable {

	private String position;
	private String table;
	private TableColumnsBean copy;
	private String columnName;
	private String typeName;
	private String columnSize;
	private String decimals;
	private boolean isNull;
	private boolean primaryKey;
	private boolean newColumn;
	private boolean markedDeleted;
	private boolean markeUpdate;
	private String coldefault;
	private String comment;
	private String collname;

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getColumnSize() {
		return columnSize;
	}

	public void setColumnSize(String columnSize) {
		this.columnSize = columnSize;
	}

	public String getDecimals() {
		return decimals;
	}

	public void setDecimals(String decimals) {
		this.decimals = decimals;
	}

	public boolean isNull() {
		return isNull;
	}

	public void setNull(boolean isNull) {
		this.isNull = isNull;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isNewColumn() {
		return newColumn;
	}

	public void setNewColumn(boolean newColumn) {
		this.newColumn = newColumn;
	}

	public boolean isMarkedDeleted() {
		return markedDeleted;
	}

	public void setMarkedDeleted(boolean markedDeleted) {
		this.markedDeleted = markedDeleted;
	}

	public boolean isMarkeUpdate() {
		return markeUpdate;
	}

	public void setMarkeUpdate(boolean markeUpdate) {
		this.markeUpdate = markeUpdate;
	}

	public String getColdefault() {
		return coldefault;
	}

	public void setColdefault(String coldefault) {
		this.coldefault = coldefault;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCollname() {
		return collname;
	}

	public void setCollname(String collname) {
		this.collname = collname;
	}

	public void copyColumn() {
		copy = (TableColumnsBean) clone();
	}

	public TableColumnsBean getCopy() {
		return copy;
	}

	public void setCopy(TableColumnsBean copy) {
		this.copy = copy;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
