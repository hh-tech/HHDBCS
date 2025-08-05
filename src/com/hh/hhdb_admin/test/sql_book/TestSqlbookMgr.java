package com.hh.hhdb_admin.test.sql_book;

import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.test.MgrTestUtil;


public class TestSqlbookMgr {

	public static void main(String[] args) throws Exception {
		MgrTestUtil.doTestMgr(TestSqlbookMgr.class);
		CsVarUtil.parentFrame.setWindowTitle("宝典窗口测试用例");
	}
}
