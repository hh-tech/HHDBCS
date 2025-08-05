package com.hh.hhdb_admin.test.table_space2;


import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.table_space2.TableSpacePanel;
import com.hh.hhdb_admin.test.MgrTestUtil;


public class TestTableSpaceComp {

    public static void main(String[] args) throws Exception {
		MgrTestUtil.initConfLang();
		MgrTestUtil.initCsVarJdbc();
        CsVarUtil.parentFrame.setWindowTitle( "表空间新增测试");
        TableSpacePanel spaceComp = new TableSpacePanel(CsVarUtil.conn, CsVarUtil.jdbc);
        spaceComp.show();

    }
}
