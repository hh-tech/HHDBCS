package com.hh.hhdb_admin.test.quick_query;


import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class TestQuickQueryMgr {

	public static void main(String[] args) throws Exception {
		MgrTestUtil.doTestMgr(TestQuickQueryMgr.class);
		CsVarUtil.parentFrame.setWindowTitle("快捷窗口测试用例");
	}
}
