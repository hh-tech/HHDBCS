package com.hh.hhdb_admin.mgr.page_filter;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.json.JsonObject;
import com.hh.hhdb_admin.common.bean.BaseRefreshBean;
public class PageFilterBean extends BaseRefreshBean{

	public PageFilterBean(String filter,String refreshNodeId) {
		super(refreshNodeId);
		setFilter(filter);
	}
	public PageFilterBean(JsonObject jobj) {
		super(jobj);
	}
	private final static String FILTER="filter";
	private final static String NUMPERPAGE="numPerPage";
//	private final static String TOTALPAGES="totalPages";
	private final static String CURRENTPAGE="currentPage";
	private final static String TOTAL_NUM="total_num";
	public String getFilter() {
		return jobj.getString(FILTER);
	}
	public void setFilter(String filter) {
		if(StringUtils.isBlank(filter))filter="";
		jobj.set(FILTER, filter);
	}
	public int getNumPerPage() {
		return jobj.getInt(NUMPERPAGE);
	}
	public void setNumPerPage(int numPerPage) {
		jobj.set(NUMPERPAGE, numPerPage);
	}
	public Integer getTotalNum() {
		return jobj.getInt(TOTAL_NUM);
	}
	public void setTotalNum(int totalNum) {
		jobj.set(TOTAL_NUM, totalNum);
	}
	public Integer getTotalPages() {
		return getTotalNum()/getNumPerPage()+1;
	}
	
	public int getCurrentPage() {
		return jobj.getInt(CURRENTPAGE);
	}
	public void setCurrentPage(int currentPage) {
		jobj.set(CURRENTPAGE, currentPage);
	}
}
