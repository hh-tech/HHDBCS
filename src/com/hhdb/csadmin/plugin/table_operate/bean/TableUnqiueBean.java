package com.hhdb.csadmin.plugin.table_operate.bean;

public class TableUnqiueBean implements Cloneable {

	private String oid;
	private String unqiueName;
	private TableUnqiueBean copy;
	private int conindid;
	private String columns;
	private String comment;
	private String param;
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

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public void copyColumn() {
		copy = (TableUnqiueBean) clone();
	}

	public TableUnqiueBean getCopy() {
		return copy;
	}

	public void setCopy(TableUnqiueBean copy) {
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
