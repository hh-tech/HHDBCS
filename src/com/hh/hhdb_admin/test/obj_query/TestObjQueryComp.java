package com.hh.hhdb_admin.test.obj_query;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.obj_query.comp.ObjQueryComp;
import com.hh.hhdb_admin.test.MgrTestUtil;


public class TestObjQueryComp {
	public static void main(String[] args) throws Exception {
		MgrTestUtil.initConfLang();
		MgrTestUtil.initCsVarJdbc();
		ObjQueryComp comp = new ObjQueryComp(MgrTestUtil.getJdbcBean(), CsVarUtil.parentFrame);
		MgrTestUtil.showComp(comp.getRootPanel());
	}
}
