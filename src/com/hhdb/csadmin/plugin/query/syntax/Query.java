package com.hhdb.csadmin.plugin.query.syntax;

public final class Query {
	private String originalQuery;
    private String derivedQuery;
    private int excuteType=ExecuteType.UNKNOW;
    private int objType=ObjType.UNKNOW;
    private int position;
    public Query(String originalQuery) {
        super();
        this.originalQuery = originalQuery;
        this.derivedQuery = originalQuery;
    }
    public String getOriginalQuery() {
        return originalQuery;
    }
    public void setOriginalQuery(String originalQuery) {
        this.originalQuery=originalQuery;
    }
    public String getDerivedQuery() {
        return derivedQuery;
    }
    public void setDerivedQuery(String derivedQuery) {
        String query = derivedQuery.replaceAll("\t", " ");
        if (query.endsWith(";")) {
            query = query.substring(0, query.length() - 1);
        }
        this.derivedQuery = query;
    }
	public int getExcuteType() {
		return excuteType;
	}
	public void setExcuteType(int excuteType) {
		this.excuteType = excuteType;
	}
	public int getObjType() {
		return objType;
	}
	public void setObjType(int objType) {
		this.objType = objType;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
}






