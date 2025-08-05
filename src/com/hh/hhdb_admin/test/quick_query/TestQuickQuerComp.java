package com.hh.hhdb_admin.test.quick_query;

import com.hh.frame.common.base.JdbcBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.quick_query.QuickQueryComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class TestQuickQuerComp {

	public static void main(String[] args) throws Exception {
		MgrTestUtil.initConfLang();
		JdbcBean jdbc= MgrTestUtil.getJdbcBean();
		QuickQueryComp que = new QuickQueryComp(jdbc, CsVarUtil.parentFrame);
		MgrTestUtil.showComp(que);
	}
}
