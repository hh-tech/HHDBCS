package com.hhdb.csadmin.plugin.table_operate.bean;

public class TableIndexBean implements Cloneable {

	private String oid;
	private String indexName;
	private String owner;
	private TableIndexBean copy;
	private String amname;
	private boolean unique;
	private boolean exclusion;
	private String definition;
	private int indexrelid;
	private String columns;
	
	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String isAmname() {
		return amname;
	}

	public void setAmname(String amname) {
		this.amname = amname;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public boolean isExclusion() {
		return exclusion;
	}

	public void setExclusion(boolean exclusion) {
		this.exclusion = exclusion;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public int getIndexrelid() {
		return indexrelid;
	}

	public void setIndexrelid(int indexrelid) {
		this.indexrelid = indexrelid;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getAmname() {
		return amname;
	}

	public void copyColumn() {
		copy = (TableIndexBean) clone();
	}

	public TableIndexBean getCopy() {
		return copy;
	}

	public void setCopy(TableIndexBean copy) {
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
