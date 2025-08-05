package com.hh.hhdb_admin.test.obj_query;

import java.io.IOException;

import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class TestObjQueryMgr {

	public static void main(String[] args) throws IOException {
		MgrTestUtil.doTestMgr(TestObjQueryMgr.class);
		CsVarUtil.parentFrame.setWindowTitle("对象查找测试用例");
	}

}
