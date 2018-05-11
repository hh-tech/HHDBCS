package com.hhdb.csadmin.plugin.table_operate.bean;

public class TableForeignBean implements Cloneable {

	private String oid;
	private String foreignName;
	private TableForeignBean copy;
	private String relname;
	private String relcolumn;
	private String foreign_table;
	private String foreign_column;
	private String comment;
	private String deltype;
	private String updatetype;
	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getForeignName() {
		return foreignName;
	}

	public void setForeignName(String foreignName) {
		this.foreignName = foreignName;
	}

	public String getRelname() {
		return relname;
	}

	public void setRelname(String relname) {
		this.relname = relname;
	}

	public String getRelcolumn() {
		return relcolumn;
	}

	public void setRelcolumn(String relcolumn) {
		this.relcolumn = relcolumn;
	}

	public String getForeign_table() {
		return foreign_table;
	}

	public void setForeign_table(String foreign_table) {
		this.foreign_table = foreign_table;
	}

	public String getForeign_column() {
		return foreign_column;
	}

	public void setForeign_column(String foreign_column) {
		this.foreign_column = foreign_column;
	}

	public String getDeltype() {
		return deltype;
	}

	public void setDeltype(String deltype) {
		this.deltype = deltype;
	}

	public String getUpdatetype() {
		return updatetype;
	}

	public void setUpdatetype(String updatetype) {
		this.updatetype = updatetype;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void copyColumn() {
		copy = (TableForeignBean) clone();
	}

	public TableForeignBean getCopy() {
		return copy;
	}

	public void setCopy(TableForeignBean copy) {
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
