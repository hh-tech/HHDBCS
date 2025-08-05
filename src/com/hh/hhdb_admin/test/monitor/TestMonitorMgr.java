package com.hh.hhdb_admin.test.monitor;

import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.test.MgrTestUtil;


public class TestMonitorMgr {

	public static void main(String[] args) throws Exception {
		MgrTestUtil.doTestMgr(TestMonitorMgr.class);
		CsVarUtil.parentFrame.setWindowTitle("监控窗口测试用例");
	}
}
