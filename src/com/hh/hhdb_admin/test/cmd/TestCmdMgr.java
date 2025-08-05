package com.hh.hhdb_admin.test.cmd;

import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.test.MgrTestUtil;


public class TestCmdMgr {

	public static void main(String[] args) throws Exception {
		MgrTestUtil.doTestMgr(TestCmdMgr.class);
		CsVarUtil.parentFrame.setWindowTitle("命令窗口测试用例");
	}
}
