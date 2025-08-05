package com.hh.hhdb_admin.mgr.page_filter;

import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;

public class PageFilterMgr extends AbsGuiMgr {
	@Override
	public Enum<?> getType() {
		return CsMgrEnum.PAGE_FILTER;
	}

	@Override
	public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
		PageFilterBean pageFilterBean=(PageFilterBean)bj;
			new PageFilterDlg().show(pageFilterBean);
		return null;
	}

}
