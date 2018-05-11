package com.hhdb.csadmin.plugin.table_operate.bean;

public class TableCheckBean implements Cloneable {

	private String oid;
	private String unqiueName;
	private TableCheckBean copy;
	private int conindid;
	private String columns;
	private String comment;
	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getUnqiueName() {
		return unqiueName;
	}

	public void setUnqiueName(String unqiueName) {
		this.unqiueName = unqiueName;
	}

	public int getConindid() {
		return conindid;
	}

	public void setConindid(int conindid) {
		this.conindid = conindid;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void copyColumn() {
		copy = (TableCheckBean) clone();
	}

	public TableCheckBean getCopy() {
		return copy;
	}

	public void setCopy(TableCheckBean copy) {
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
