package com.hh.hhdb_admin.test.cmmn;


import com.hh.frame.common.util.db.ConnUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.comp.SqlExeDlgTool;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class SqlExceDlgTest {
	private static String tabName="aa";
	public static void main(String[] args) throws Exception {
		MgrTestUtil.initConfLang();
		MgrTestUtil.initCsVarJdbc();
		SqlExeDlgTool dlg=new SqlExeDlgTool();
		dlg.exeSql(CsVarUtil.jdbc,CsVarUtil.conn, "create table "+tabName+"(id int) ");
		dlg.exeSql(CsVarUtil.jdbc,CsVarUtil.conn, "drop table "+tabName);
		dlg.exeSql(CsVarUtil.jdbc,CsVarUtil.conn, "call DBMS_LOCK.sleep(5)");
		ConnUtil.close(CsVarUtil.conn);
		System.exit(0);

	}

}
