package com.hh.hhdb_admin.test.query;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.test.MgrTestUtil;
public class TestQueryMgr {

	public static void main(String[] args) throws Exception {
		MgrTestUtil.doTestMgr(TestQueryMgr.class);
		CsVarUtil.parentFrame.setWindowTitle("语句窗口测试用例");

	}
}
