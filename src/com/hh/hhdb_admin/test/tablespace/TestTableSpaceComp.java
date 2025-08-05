package com.hh.hhdb_admin.test.tablespace;


import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tablespace.TableSpaceComp;
import com.hh.hhdb_admin.test.MgrTestUtil;



public class TestTableSpaceComp {

    public static void main(String[] args) throws Exception {
		MgrTestUtil.initConfLang();
		MgrTestUtil.initCsVarJdbc();
        CsVarUtil.parentFrame.setWindowTitle( "表空间新增测试");
        TableSpaceComp spaceComp = new TableSpaceComp(CsVarUtil.conn, CsVarUtil.getDbtype()) {
            @Override
            public void refreshTree() {
                System.out.println("保存成功");
            }
        };
        MgrTestUtil.showComp(spaceComp.getPanel());

    }
}
