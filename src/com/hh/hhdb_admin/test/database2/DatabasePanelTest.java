package com.hh.hhdb_admin.test.database2;

import com.hh.frame.lang2.LangMgr;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.database2.DatabasePanel;
import com.hh.hhdb_admin.test.MgrTestUtil;

import java.io.IOException;

public class DatabasePanelTest {
	public static void main(String[] args) throws Exception {
		try {
			LangMgr.addLang(CsL.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		MgrTestUtil.initCsVarJdbc();
		new DatabasePanel(CsVarUtil.jdbc, CsVarUtil.conn);
	}

}
