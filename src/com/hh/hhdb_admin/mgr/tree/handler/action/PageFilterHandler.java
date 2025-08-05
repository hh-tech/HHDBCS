package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.base.PageBean;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.page_filter.PageFilterBean;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;

public class PageFilterHandler extends AbsHandler {

	@Override
	public void resolve(CsFolderBean treeNode) throws Exception {
		
		String filter=treeNode.getMrNode().getFilter();
		PageFilterBean pf=new PageFilterBean(filter,treeNode.getId());
		
		PageBean page=treeNode.getMrNode().getPageBean();
		if(page==null) {
			page=new PageBean(0,100);
		}
		pf.setCurrentPage(page.getCurrentPage());
		pf.setNumPerPage(page.getNumPerPage());
		pf.setTotalNum(page.getTotalNum());

//		if(page.getTotalPages()!=null) {
//			pf.setTotalPages(page.getTotalPages());
//		}
		CsVarUtil.eng2.doCall(CsMgrEnum.PAGE_FILTER, pf);
		
	}

}
