package com.hh.hhdb_admin.test.cmd;
import java.io.IOException;

import com.hh.frame.common.base.JdbcBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.cmd2.CmdComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class TestCmdComp {
	public static void main(String[] args) throws IOException {
		MgrTestUtil.initConfLang();
		JdbcBean jdbc= MgrTestUtil.getJdbcBean();
		MgrTestUtil.showComp(new CmdComp(jdbc, CsVarUtil.parentFrame).getPanel());
	}
	
}
