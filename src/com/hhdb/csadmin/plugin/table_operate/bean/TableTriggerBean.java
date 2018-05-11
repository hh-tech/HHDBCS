package com.hhdb.csadmin.plugin.table_operate.bean;

public class TableTriggerBean implements Cloneable {

	private String oid;
	private String treggerName;
	private TableTriggerBean copy;
	private boolean isrow;
	private String condtion;
	private boolean tginsert;
	private boolean tgupdate;
	private boolean tgdelete;
	private String columns;
	private String 	tgfunc;
	private String funparm;
	private String comment;
	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getTreggerName() {
		return treggerName;
	}

	public void setTreggerName(String treggerName) {
		this.treggerName = treggerName;
	}

	public boolean isIsrow() {
		return isrow;
	}

	public void setIsrow(boolean isrow) {
		this.isrow = isrow;
	}

	public String getCondtion() {
		return condtion;
	}

	public void setCondtion(String condtion) {
		this.condtion = condtion;
	}

	public boolean isTginsert() {
		return tginsert;
	}

	public void setTginsert(boolean tginsert) {
		this.tginsert = tginsert;
	}

	public boolean isTgupdate() {
		return tgupdate;
	}

	public void setTgupdate(boolean tgupdate) {
		this.tgupdate = tgupdate;
	}

	public boolean isTgdelete() {
		return tgdelete;
	}

	public void setTgdelete(boolean tgdelete) {
		this.tgdelete = tgdelete;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getTgfunc() {
		return tgfunc;
	}

	public void setTgfunc(String tgfunc) {
		this.tgfunc = tgfunc;
	}

	public String getFunparm() {
		return funparm;
	}

	public void setFunparm(String funparm) {
		this.funparm = funparm;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void copyColumn() {
		copy = (TableTriggerBean) clone();
	}

	public TableTriggerBean getCopy() {
		return copy;
	}

	public void setCopy(TableTriggerBean copy) {
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
