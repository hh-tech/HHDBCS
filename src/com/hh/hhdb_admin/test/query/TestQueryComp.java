package com.hh.hhdb_admin.test.query;

import com.hh.frame.common.base.JdbcBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.query2.QueryComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class TestQueryComp {

	public static void main(String[] args) throws Exception {
		MgrTestUtil.initConfLang();
		JdbcBean jdbc= MgrTestUtil.getJdbcBean();
		MgrTestUtil.showComp(new QueryComp(jdbc,null, CsVarUtil.parentFrame).getPanel());
	}
}
