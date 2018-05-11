package com.hhdb.csadmin.plugin.table_operate.bean;

public class TableRuleBean implements Cloneable {

	private String oid;
	private String id;
	private String rulename;
	private TableRuleBean copy;
	private String evtype;
	private boolean isinstead;
	private String definition;
	private String comment;
	private String factor;
	
	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRulename() {
		return rulename;
	}

	public void setRulename(String rulename) {
		this.rulename = rulename;
	}

	public String getEvtype() {
		return evtype;
	}

	public void setEvtype(String evtype) {
		this.evtype = evtype;
	}

	public boolean isIsinstead() {
		return isinstead;
	}

	public void setIsinstead(boolean isinstead) {
		this.isinstead = isinstead;
	}
	
	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getFactor() {
		return factor;
	}

	public void setFactor(String factor) {
		this.factor = factor;
	}

	public void copyColumn() {
		copy = (TableRuleBean) clone();
	}

	public TableRuleBean getCopy() {
		return copy;
	}

	public void setCopy(TableRuleBean copy) {
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
