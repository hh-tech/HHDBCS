package com.hh.hhdb_admin.test.gis;

import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.gis.GisComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class GisCompTest {

	public static void main(String[] args) throws Exception {
		MgrTestUtil.initConfLang();
		MgrTestUtil.initCsVarJdbc();
		GisComp gis = new GisComp(CsVarUtil.jdbc);
		MgrTestUtil.showComp(gis.getLastPanel());
	}
}
